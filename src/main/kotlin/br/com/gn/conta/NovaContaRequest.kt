package br.com.gn.conta

import br.com.gn.shared.validation.Unique
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class NovaContaRequest(
    @field:NotBlank
    @field:Unique(field = "nome", domainClass = Conta::class)
    val nome: String
) {
    fun toModel(): Conta {
        return Conta(nome = nome)
    }
}

