package br.com.gn.despesa

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.PRECONDITION_FAILED
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.validation.Validated
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Controller("/despesas")
class DespesaController(
    private val repository: DespesaRepository,
    private val manager: EntityManager,
) {

    @Post
    @Transactional
    fun criar(@Body @Valid request: DespesaRequest): HttpResponse<DespesaResponse> {
        val despesa = request.toModel(manager)
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

        despesa.gerarTransacoes(primeiroVencimento, status)
        repository.save(despesa)

        return HttpResponse.created(DespesaResponse(despesa))
    }

    @Get("/fixas")
    @Transactional
    fun buscaDespesasFixas(): HttpResponse<List<DespesaResponse>> {
        val contasFixas = repository.findByFixa(true)
            .map { despesa -> DespesaResponse(despesa) }
        return HttpResponse.ok(contasFixas)
    }

    @Get
    @Transactional
    fun buscaDespesas(pageable: Pageable): HttpResponse<Page<DespesaResponse>> {
        val page = repository.findAll(pageable)
            .map { despesa -> DespesaResponse(despesa) }
        return HttpResponse.ok(page)
    }
}