package br.com.gn.compra

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
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
        repository.save(compra)
        return HttpResponse.created(CompraResponse(compra))
    }
}