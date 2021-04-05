package br.com.gn.categoria

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.NOT_FOUND
import io.micronaut.http.annotation.*
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.validation.Validated
import javax.validation.Valid

//@Secured(SecurityRule.IS_AUTHENTICATED)
@Validated
@Controller("/categorias")
class CategoriaController(private val repository: CategoriaRepository) {

    @Post
    fun cadastrar(@Body @Valid request: NovaCategoriaRequest): HttpResponse<CategoriaResponse> {
        val categoria = request.toModel()
        repository.save(categoria)
        return HttpResponse.created(CategoriaResponse(categoria))
    }

    @Get
    fun buscar(@QueryValue(defaultValue = "") nome: String,
    @QueryValue movimentos: List<Movimento>) : HttpResponse<List<CategoriaResponse>>{
        val list = repository.findByMovimentoIn(movimentos).map(::CategoriaResponse)
        return HttpResponse.ok(list)
    }

    @Delete("/{id}")
    fun deletar(@PathVariable id: Long){
        val categoria = repository.findById(id)
            .orElseThrow { throw HttpStatusException(NOT_FOUND, "Não foi encontrada categoria para o id $id") }
        repository.delete(categoria)
    }
}