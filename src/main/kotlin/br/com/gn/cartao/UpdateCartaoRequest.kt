package br.com.gn.cartao

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Introspected
data class UpdateCartaoRequest(
    @field:NotNull @Min(1) @Max(31) val diaVencimento: Int,
    @field:NotNull @Min(1) @Max(31) val diaFechamento: Int,
    @field:NotNull val mesVencimentoMesmoFechamento: Boolean
)