package br.com.gn.despesa

import br.com.gn.pagamento.FormaDePagamento
import br.com.gn.pagamento.StatusPagamento
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
import java.time.format.TextStyle
import java.util.*
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Controller("/despesas")
class DespesaController(
    private val repository: DespesaRepository,
    private val manager: EntityManager,
) {

    private fun validarCategoriaDuplicadaNoMes(despesa: Despesa) {
        val categoria = despesa.categoria

        if (categoria.umaPorMes) {
            val inicio = despesa.realizadaEm.withDayOfMonth(1)
            val fim = despesa.realizadaEm.withDayOfMonth(despesa.realizadaEm.lengthOfMonth())
            val existeLancamento =
                repository.existsByCategoriaIdAndRealizadaEmBetween(
                    id = despesa.categoria.id!!,
                    inicio = inicio,
                    fim = fim
                )

            if (existeLancamento) {
                val mes = despesa.realizadaEm.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                throw HttpStatusException(
                    PRECONDITION_FAILED,
                    "Já existe um lançamento para a categoria ${categoria.nome} em $mes de ${despesa.realizadaEm.year}"
                )
            }
        }

    }

    @Post
    @Transactional
    fun criar(@Body @Valid request: NovaDespesaRequest): HttpResponse<DespesaResponse> {
        val despesa = request.toModel(manager)
        validarCategoriaDuplicadaNoMes(despesa)
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
        repository.save(despesa)

        return HttpResponse.created(DespesaResponse(despesa))
    }

    @Post("/fixas")
    @Transactional
    fun addDespesasFixas(@Body @Valid request: List<DespesaFixaRequest>): HttpResponse<List<DespesaResponse>> {
        val list: MutableList<Despesa> = mutableListOf()
        request.forEach {
            if (!it.fixa) throw HttpStatusException(PRECONDITION_FAILED, "Tentando cadastrar despesas não fixas.")

            val despesa = it.toModel(manager)
                ?: throw HttpStatusException(
                    PRECONDITION_FAILED,
                    "Tentando cadastrar despesas fixas com bases não fixas."
                )

            var status = it.statusPagamento
            var primeiroVencimento: LocalDate = it.vencimento ?: LocalDate.now()

            if (!despesa.precodicaoNaoCredito(it.statusPagamento))
                throw HttpStatusException(PRECONDITION_FAILED, "Cartão preenchido ou conta não preenchida.")

            despesa.gerarTransacoes(
                primeiroVencimento = primeiroVencimento,
                status = status
            )

            list.add(despesa)
        }

        repository.saveAll(list)
        return HttpResponse.ok(list.map(::DespesaResponse))
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