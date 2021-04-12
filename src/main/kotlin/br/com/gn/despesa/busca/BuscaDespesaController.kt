package br.com.gn.despesa.busca

import br.com.gn.despesa.DespesaRepository
import br.com.gn.despesa.DespesaResponse
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import javax.transaction.Transactional

@Controller("/despesas")
class BuscaDespesaController(
    private val repository: DespesaRepository
) {

    @Get("/fixas")
    @Transactional
    fun buscaDespesasFixas(@QueryValue(defaultValue = "false") variavel: Boolean): HttpResponse<List<DespesaResponse>> {
        val contasFixas = repository.findByFixaAndValorVariavel(true, variavel)
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