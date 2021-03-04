package br.com.gn.entrada

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Controller("/entradas")
class EntradaController(
    private val repository: EntradaRepository,
    private val entityManager: EntityManager
) {

    @Post
    @Transactional
    fun cadastrar(@Body @Valid request: EntradaRequest): HttpResponse<EntradaResponse> {
        val entrada = request.toModel(entityManager)
        repository.save(entrada)
        return HttpResponse.created(EntradaResponse(entrada))
    }

    @Get("/fixas")
    @Transactional
    fun buscaEntradasFixas(): HttpResponse<List<EntradaResponse>> {
        val entradasFixas = repository.findByFixa(true)
            .map { entrada -> EntradaResponse(entrada) }
        return HttpResponse.ok(entradasFixas)
    }

    @Get
    @Transactional
    fun buscaEntradas(pageable: Pageable): HttpResponse<Page<EntradaResponse>> {
        val page = repository.findAll(pageable)
            .map { entrada -> EntradaResponse(entrada) }
        return HttpResponse.ok(page)
    }
}