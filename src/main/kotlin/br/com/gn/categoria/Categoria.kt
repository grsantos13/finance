package br.com.gn.categoria

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Categoria(
    @field:NotBlank
    val nome: String
) {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null
}
