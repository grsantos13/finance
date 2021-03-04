package br.com.gn.conta

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
@Controller("/contas")
class ContaController(private val manager: EntityManager) {

    @Post
    @Transactional
    fun cadastrar(@Body @Valid request: ContaRequest): HttpResponse<ContaResponse> {
        val conta = request.toModel()
        manager.persist(conta)
        return HttpResponse.created(ContaResponse(conta))
    }

    @Get
    @Transactional
    fun buscar(): HttpResponse<List<ContaResponse>> {
        val query = manager.createQuery(" select c from Conta c order by nome ", Conta::class.java)
        val list = query.resultList.map { conta ->
            ContaResponse(conta)
        }
        return HttpResponse.ok(list)
    }
}