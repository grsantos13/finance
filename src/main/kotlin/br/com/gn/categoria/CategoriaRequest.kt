package br.com.gn.categoria

import br.com.gn.shared.validation.Unique
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class CategoriaRequest(
    @field:NotBlank
    @field:Unique(field = "nome", domainClass = Categoria::class)
    val nome: String
) {
    fun toModel(): Categoria {
        return Categoria(nome = nome)
    }
}

class CategoriaResponse(categoria: Categoria) {
    val nome = categoria.nome
    val id = categoria.id
}
