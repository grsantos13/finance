package br.com.gn.despesa.transacao

import br.com.gn.despesa.Despesa
import br.com.gn.despesa.StatusPagamento
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.EnumType.STRING
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

@Entity
class Transacao(
    @field:Valid
    @field:NotNull
    @ManyToOne
    val despesa: Despesa,
    @field:Positive
    @field:NotNull
    val valor: BigDecimal,
    @field:NotNull
    val vencimento: LocalDate,
    @field:NotNull
    @Enumerated(STRING)
    val status: StatusPagamento

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

}