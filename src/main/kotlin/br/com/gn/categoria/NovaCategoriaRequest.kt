package br.com.gn.categoria

import br.com.gn.shared.validation.Unique
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class NovaCategoriaRequest(
    @field:NotBlank
    @field:Unique(field = "nome", domainClass = Categoria::class)
    val nome: String,
    @field:NotNull
    val umaPorMes: Boolean,
    @field:NotNull
    val movimento: Movimento
) {
    fun toModel(): Categoria {
        return Categoria(
            nome = nome,
            umaPorMes = umaPorMes,
            movimento = movimento
        )
    }
}

