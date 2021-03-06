package br.com.gn.categoria

import javax.persistence.Entity
import javax.persistence.EnumType.STRING
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Categoria(
    @field:NotBlank
    val nome: String,
    @field:NotNull
    val umaPorMes: Boolean,
    @field:NotNull
    @Enumerated(STRING)
    val movimento: Movimento
) {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null
}
