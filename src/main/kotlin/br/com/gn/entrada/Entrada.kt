package br.com.gn.entrada

import br.com.gn.categoria.Categoria
import br.com.gn.conta.Conta
import br.com.gn.pagamento.FormaDePagamento
import br.com.gn.pagamento.StatusPagamento
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

@Entity
class Entrada(
    @field:NotNull val realizadaEm: LocalDate = LocalDate.now(),
    @field:NotNull @field:Valid @ManyToOne val categoria: Categoria,
    @field:NotBlank @field:Size(max = 100) val descricao: String,
    @field:NotNull @Enumerated(EnumType.STRING) val formaDePagamento: FormaDePagamento,
    @field:NotNull @field:Positive val valor: BigDecimal,
    @field:NotNull val fixa: Boolean = false,
    @field:NotNull val valorVariavel: Boolean = false,
    @field:NotNull @field:Valid @ManyToOne val conta: Conta,
    @field:NotNull @Enumerated(EnumType.STRING) val status: StatusPagamento
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}