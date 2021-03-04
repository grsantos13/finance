package br.com.gn.compra

import br.com.gn.cartao.Cartao
import br.com.gn.cartao.CartaoResponse
import br.com.gn.categoria.Categoria
import br.com.gn.categoria.CategoriaResponse
import br.com.gn.compra.transacao.Transacao
import br.com.gn.conta.Conta
import br.com.gn.shared.validation.ExistsResource
import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus.NOT_FOUND
import io.micronaut.http.exceptions.HttpStatusException
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PastOrPresent
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

@Introspected
data class CompraRequest(
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
    val statusPagamento: StatusPagamento? = null
) {

    fun toModel(manager: EntityManager): Compra {
        val categoria = manager.find(Categoria::class.java, idCategoria)
            ?: throw HttpStatusException(NOT_FOUND, "Categoria não encontrada para o id $idCategoria")

        var cartao: Cartao? = null
        var conta: Conta? = null

        if (idCartao != null)
            cartao = manager.find(Cartao::class.java, idCartao)
                ?: throw HttpStatusException(NOT_FOUND, "Cartão não encontrado para o id $idCartao")

        if (idConta != null)
            conta = manager.find(Conta::class.java, idConta)
                ?: throw HttpStatusException(NOT_FOUND, "Cartão não encontrado para o id $idConta")

        return Compra(
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

class CompraResponse(compra: Compra) {
    @JsonFormat(pattern = "yyyy-MM-dd")
    val realizadaEm: LocalDate = compra.realizadaEm
    val categoria: CategoriaResponse = CategoriaResponse(compra.categoria)
    val descricao: String = compra.descricao
    val numeroDeParcelas: Int = compra.numeroDeParcelas
    val formaDePagamento: FormaDePagamento = compra.formaDePagamento
    val valor: BigDecimal = compra.valor
    val conta: String? = compra.conta?.nome
    val cartao: CartaoResponse? = if (compra.cartao == null) null else CartaoResponse(cartao = compra.cartao)
    val fixa = compra.fixa
    val valorVariavel = compra.valorVariavel
    val transacoes: List<TransacaoResponse> = compra.transacoes.map { transacao -> TransacaoResponse(transacao) }
}

class TransacaoResponse(transacao: Transacao) {
    val id = transacao.id!!
    val valor = transacao.valor

    @JsonFormat(pattern = "yyyy-MM-dd")
    val vencimento = transacao.vencimento
    val status = transacao.status
}
