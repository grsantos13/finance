package br.com.gn.despesa.cadastro

import br.com.gn.despesa.Despesa
import br.com.gn.despesa.DespesaFixaRequest
import br.com.gn.despesa.DespesaResponse
import br.com.gn.shared.validation.CategoriaDuplicadaNoMesValidator
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
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
@Controller("/despesas/fixas")
class NovaDespesaFixaController(
    private val manager: EntityManager,
    private val categoriaValidator: CategoriaDuplicadaNoMesValidator
) {

    @Post
    @Transactional
    fun addDespesasFixas(@Body @Valid request: List<DespesaFixaRequest>): HttpResponse<List<DespesaResponse>> {
        val list: MutableList<Despesa> = mutableListOf()
        request.forEach {

            // Valida se a despesa é realmente fixa
            if (!it.fixa) throw HttpStatusException(
                HttpStatus.PRECONDITION_FAILED,
                "Tentando cadastrar despesas não fixas."
            )

            // Valida se a despesa base da desejada é fixa
            val despesa = it.toModel(manager)
                ?: throw HttpStatusException(
                    HttpStatus.PRECONDITION_FAILED,
                    "Tentando cadastrar despesas fixas com bases não fixas."
                )

            // Valida se a despesa a ser gerada já existe no mês em questão
            categoriaValidator.validarCategoriaDuplicadaNoMes(
                despesa.categoria,
                despesa.realizadaEm,
                despesa::class.qualifiedName!!
            )
            var status = it.statusPagamento
            var primeiroVencimento: LocalDate = it.vencimento ?: LocalDate.now()

            if (!despesa.precodicaoNaoCredito(it.statusPagamento))
                throw HttpStatusException(HttpStatus.PRECONDITION_FAILED, "Cartão preenchido ou conta não preenchida.")

            despesa.gerarTransacoes(
                primeiroVencimento = primeiroVencimento,
                status = status
            )

            list.add(despesa)
        }

        list.forEach(manager::persist)
        return HttpResponse.ok(list.map(::DespesaResponse))
    }
}