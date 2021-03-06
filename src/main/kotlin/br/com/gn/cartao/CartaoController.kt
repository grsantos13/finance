package br.com.gn.cartao

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.NOT_FOUND
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.validation.Validated
import javax.validation.Valid

@Validated
@Controller("/cartoes")
class CartaoController(private val repository: CartaoRepository) {

    @Post
    fun cadastrar(@Body @Valid request: NovoCartaoRequest): HttpResponse<CartaoResponse> {
        val cartao = request.toModel()
        repository.save(cartao)
        return HttpResponse.created(CartaoResponse(cartao))
    }

    @Put("/{id}")
    fun atualizar(
        @Body @Valid request: UpdateCartaoRequest,
        @PathVariable id: Long
    ): HttpResponse<CartaoResponse> {
        val cartao = repository.findById(id)
            .orElseThrow { throw HttpStatusException(NOT_FOUND, "Não encontrado cartão para o id $id") }

        cartao.atualizar(request)
        repository.update(cartao)

        return HttpResponse.ok(CartaoResponse(cartao))
    }

    @Get
    fun buscaCartoes(): HttpResponse<List<CartaoResponse>>{
        val list = repository.findAll()
            .map { CartaoResponse(it) }
        return HttpResponse.ok(list)
    }
}