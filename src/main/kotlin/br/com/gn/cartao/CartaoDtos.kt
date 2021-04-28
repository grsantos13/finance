package br.com.gn.cartao

import br.com.gn.shared.validation.Unique
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Introspected
data class NovoCartaoRequest(
    @field:NotBlank @field:Unique(field = "nome", domainClass = Cartao::class) val nome: String,
    @field:NotNull @Min(1) @Max(31) val diaVencimento: Int,
    @field:NotNull @Min(1) @Max(31) val diaFechamento: Int,
    @field:NotNull val mesVencimentoMesmoFechamento: Boolean
) {

    fun toModel(): Cartao {
        return Cartao(
            nome = nome,
            diaVencimento = diaVencimento,
            diaFechamento = diaFechamento,
            mesVencimentoMesmoFechamento = mesVencimentoMesmoFechamento
        )
    }

}


@Introspected
data class UpdateCartaoRequest(
    @field:NotNull @Min(1) @Max(31) val diaVencimento: Int,
    @field:NotNull @Min(1) @Max(31) val diaFechamento: Int,
    @field:NotNull val mesVencimentoMesmoFechamento: Boolean
)

class CartaoResponse(cartao: Cartao) {
    val id = cartao.id!!
    val nome = cartao.nome
    val diaVencimento = cartao.diaVencimento
    val diaFechamento = cartao.diaFechamento
    val mesVencimentoMesmoFechamento = cartao.mesVencimentoMesmoFechamento
}