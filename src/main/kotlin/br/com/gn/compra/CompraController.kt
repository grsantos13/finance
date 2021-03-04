package br.com.gn.compra

import br.com.gn.categoria.CategoriaRepository
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import javax.validation.Valid

@Validated
@Controller("/compras")
class CompraController(
    private val repository: CompraRepository,
    private val categoriaRepository: CategoriaRepository
) {

    @Post
    fun criar(@Body @Valid request: CompraRequest): HttpResponse<CompraResponse> {
        val compra = request.toModel(categoriaRepository)
        repository.save(compra)
        return HttpResponse.created(CompraResponse(compra))
    }
}