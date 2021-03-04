package br.com.gn.compra

import br.com.gn.categoria.Categoria
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PastOrPresent
import javax.validation.constraints.Size

@Entity
class Compra(
    @field:NotNull @field:PastOrPresent val realizadaEm: LocalDate = LocalDate.now(),
    @field:NotNull @field:Valid @ManyToOne val categoria: Categoria,
    @field:NotBlank @field:Size(max = 100) val descricao: String,
    @field:NotNull @field:Min(1) val numeroDeParcelas: Int,
    @field:NotNull val formaDePagamento: FormaDePagamento,
    val conta: Conta? = null,
    val cartao: Cartao? = null
) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null

}
