package br.com.gn.despesa.cadastro

import br.com.gn.despesa.DespesaResponse
import br.com.gn.despesa.NovaDespesaRequest
import br.com.gn.pagamento.FormaDePagamento
import br.com.gn.pagamento.StatusPagamento
import br.com.gn.shared.validation.CategoriaDuplicadaNoMesValidator
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.PRECONDITION_FAILED
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.validation.Validated
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Controller("/despesas")
class NovaDespesaController(
    private val manager: EntityManager,
    private val categoriaValidator: CategoriaDuplicadaNoMesValidator
) {
    @Post
    @Transactional
    fun criar(@Body @Valid request: NovaDespesaRequest): HttpResponse<DespesaResponse> {
        val despesa = request.toModel(manager)
        categoriaValidator.validarCategoriaDuplicadaNoMes(
            despesa.categoria,
            despesa.realizadaEm,
            despesa::class.qualifiedName!!
        )
        var status = request.statusPagamento
        var primeiroVencimento: LocalDate = request.vencimento ?: LocalDate.now()

        if (despesa.formaDePagamento == FormaDePagamento.CREDITO) {
            if (!despesa.precondicaoCredito())
                throw HttpStatusException(PRECONDITION_FAILED, "Cartão não preenchido ou conta preenchida.")

            status = request.statusPagamento ?: StatusPagamento.PENDENTE
            primeiroVencimento = despesa.verificarPrimeiroVencimento()

        } else {
            if (!despesa.precodicaoNaoCredito(request.statusPagamento))
                throw HttpStatusException(PRECONDITION_FAILED, "Cartão preenchido ou conta não preenchida.")
        }

        despesa.gerarTransacoes(
            primeiroVencimento = primeiroVencimento,
            status = status
        )
        manager.persist(despesa)

        return HttpResponse.created(DespesaResponse(despesa))
    }
}