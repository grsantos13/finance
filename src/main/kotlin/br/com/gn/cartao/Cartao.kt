package br.com.gn.cartao

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Cartao(
    @field:NotBlank val nome: String,
    @field:NotNull @Min(1) @Max(31) var diaVencimento: Int,
    @field:NotNull @Min(1) @Max(31) var diaFechamento: Int,
    @field:NotNull var mesVencimentoMesmoFechamento: Boolean
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    fun atualizar(update: UpdateCartaoRequest) {
        diaVencimento = update.diaVencimento
        diaFechamento = update.diaFechamento
        mesVencimentoMesmoFechamento = update.mesVencimentoMesmoFechamento
    }

}