package br.com.gn.compra

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
@Controller("/compras")
class CompraController(
    private val repository: CompraRepository,
    private val manager: EntityManager
) {

    @Post
    @Transactional
    fun criar(@Body @Valid request: CompraRequest): HttpResponse<CompraResponse> {
        val compra = request.toModel(manager)
        var status = request.statusPagamento
        var primeiroVencimento: LocalDate = request.vencimento ?: LocalDate.now()

        if (compra.formaDePagamento == FormaDePagamento.CREDITO) {
            if (!compra.precondicaoCredito())
                throw HttpStatusException(PRECONDITION_FAILED, "Cartão não preenchido ou conta preenchida.")

            status = request.statusPagamento ?: StatusPagamento.PENDENTE
            primeiroVencimento = compra.verificarPrimeiroVencimento()

        } else {
            if (!compra.precodicaoNaoCredito(request.statusPagamento))
                throw HttpStatusException(PRECONDITION_FAILED, "Cartão preenchido ou conta não preenchida.")
        }

        compra.gerarTransacoes(primeiroVencimento, status)
        repository.save(compra)

        return HttpResponse.created(CompraResponse(compra))
    }

    @Get("/fixas")
    @Transactional
    fun buscaComprasFixas(): HttpResponse<List<CompraResponse>> {
        val contasFixas = repository.findByFixa(true)
            .map { compra -> CompraResponse(compra) }
        return HttpResponse.ok(contasFixas)
    }

    @Get
    @Transactional
    fun buscaCompras(pageable: Pageable): HttpResponse<Page<CompraResponse>> {
        val page = repository.findAll(pageable)
            .map { compra -> CompraResponse(compra) }
        return HttpResponse.ok(page)
    }
}