package br.com.gn.despesa

import br.com.gn.cartao.Cartao
import br.com.gn.cartao.CartaoResponse
import br.com.gn.categoria.Categoria
import br.com.gn.categoria.CategoriaResponse
import br.com.gn.categoria.Movimento
import br.com.gn.conta.Conta
import br.com.gn.pagamento.FormaDePagamento
import br.com.gn.pagamento.StatusPagamento
import br.com.gn.shared.validation.ExistsResource
import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.validation.constraints.*

@Introspected
data class NovaDespesaRequest(
    @field:NotNull @field:PastOrPresent val realizadaEm: LocalDate? = LocalDate.now(),
    @field:NotNull @field:ExistsResource(field = "id", domainClass = Categoria::class) val idCategoria: Long?,
    @field:NotBlank @field:Size(max = 100) val descricao: String?,
    @field:NotNull @field:Min(1) val numeroDeParcelas: Int?,
    @field:NotNull val formaDePagamento: FormaDePagamento?,
    @field:NotNull @field:Positive val valor: BigDecimal?,
    @field:FutureOrPresent val vencimento: LocalDate? = null,
    @field:ExistsResource(field = "id", domainClass = Cartao::class) val idCartao: Long? = null,
    @field:ExistsResource(field = "id", domainClass = Conta::class) val idConta: Long? = null,
    @field:NotNull val fixa: Boolean? = false,
    @field:NotNull val valorVariavel: Boolean? = false,
    val statusPagamento: StatusPagamento? = null
) {

    fun toModel(manager: EntityManager): Despesa {
        val categoria = buscarCategoria(manager)

        var cartao: Cartao? = null
        var conta: Conta? = null

        if (idCartao != null)
            cartao = manager.find(Cartao::class.java, idCartao)
                ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "Cartão não encontrado para o id $idCartao")

        if (idConta != null)
            conta = manager.find(Conta::class.java, idConta)
                ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "Cartão não encontrado para o id $idConta")

        return Despesa(
            realizadaEm = realizadaEm ?: LocalDate.now(),
            categoria = categoria,
            descricao = descricao!!,
            numeroDeParcelas = numeroDeParcelas!!,
            formaDePagamento = formaDePagamento!!,
            conta = conta,
            cartao = cartao,
            valor = valor!!,
            fixa = fixa!!,
            valorVariavel = valorVariavel!!
        )
    }

    private fun buscarCategoria(manager: EntityManager): Categoria {

        val categorias = manager.createQuery(
            " select c from Categoria c where c.id = :id and c.movimento = :movimento",
            Categoria::class.java
        )
            .setParameter("id", idCategoria)
            .setParameter("movimento", Movimento.DESPESA)
            .resultList

        if (categorias.isEmpty())
            throw HttpStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada para o id $idCategoria")

        return categorias[0]

    }
}

@Introspected
data class DespesaFixaRequest(
    @field:NotNull @field:PastOrPresent val realizadaEm: LocalDate = LocalDate.now(),
    @field:NotNull @field:ExistsResource(field = "id", domainClass = Categoria::class) val idCategoria: Long,
    @field:NotBlank @field:Size(max = 100) val descricao: String,
    @field:NotNull @field:Min(1) val numeroDeParcelas: Int,
    @field:NotNull val formaDePagamento: FormaDePagamento,
    @field:NotNull @field:Positive val valor: BigDecimal,
    @field:FutureOrPresent val vencimento: LocalDate? = null,
    @field:ExistsResource(field = "id", domainClass = Cartao::class) val idCartao: Long? = null,
    @field:ExistsResource(field = "id", domainClass = Conta::class) val idConta: Long? = null,
    @field:NotNull val fixa: Boolean = false,
    @field:NotNull val valorVariavel: Boolean = false,
    val statusPagamento: StatusPagamento? = null,
    @field:ExistsResource(field = "id", domainClass = Despesa::class) val idDespesa: Long
) {

    /**
     * Retorna nulo quando a despesa base não for considerada fixa
     */
    fun toModel(manager: EntityManager): Despesa? {
        val categoria = manager.find(Categoria::class.java, idCategoria)
            ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada para o id $idCategoria")

        val despesa = manager.find(Despesa::class.java, idDespesa)
            ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "Despesa não encontrada para o id $idDespesa")

        if (!despesa.fixa) return null

        var cartao: Cartao? = null
        var conta: Conta? = null

        if (idCartao != null)
            cartao = manager.find(Cartao::class.java, idCartao)
                ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "Cartão não encontrado para o id $idCartao")

        if (idConta != null)
            conta = manager.find(Conta::class.java, idConta)
                ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "Cartão não encontrado para o id $idConta")

        return Despesa(
            realizadaEm = realizadaEm,
            categoria = categoria,
            descricao = descricao,
            numeroDeParcelas = numeroDeParcelas,
            formaDePagamento = formaDePagamento,
            conta = conta,
            cartao = cartao,
            valor = valor,
            fixa = fixa,
            valorVariavel = valorVariavel
        )
    }
}

class DespesaResponse(despesa: Despesa) {
    val id = despesa.id!!

    @JsonFormat(pattern = "yyyy-MM-dd")
    val realizadaEm: LocalDate = despesa.realizadaEm
    val categoria: CategoriaResponse = CategoriaResponse(despesa.categoria)
    val descricao: String = despesa.descricao
    val numeroDeParcelas: Int = despesa.numeroDeParcelas
    val formaDePagamento: FormaDePagamento = despesa.formaDePagamento
    val valor: BigDecimal = despesa.valor
    val conta: String? = despesa.conta?.nome
    val cartao: CartaoResponse? = if (despesa.cartao == null) null else CartaoResponse(cartao = despesa.cartao)
    val fixa = despesa.fixa
    val valorVariavel = despesa.valorVariavel
    val transacoes: List<TransacaoResponse> = despesa.transacoes.map { transacao -> TransacaoResponse(transacao) }
}