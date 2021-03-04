package br.com.gn.compra

import br.com.gn.cartao.Cartao
import br.com.gn.categoria.Categoria
import br.com.gn.compra.transacao.Transacao
import br.com.gn.conta.Conta
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.persistence.CascadeType.PERSIST
import javax.persistence.Entity
import javax.persistence.EnumType.STRING
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

@Entity
class Compra(
    @field:NotNull val realizadaEm: LocalDate = LocalDate.now(),
    @field:NotNull @field:Valid @ManyToOne val categoria: Categoria,
    @field:NotBlank @field:Size(max = 100) val descricao: String,
    @field:NotNull @field:Min(1) val numeroDeParcelas: Int,
    @field:NotNull @Enumerated(STRING) val formaDePagamento: FormaDePagamento,
    @field:NotNull @field:Positive val valor: BigDecimal,
    @field:NotNull val fixa: Boolean = false,
    @field:NotNull val valorVariavel: Boolean = false,
    @field:Valid @ManyToOne val conta: Conta? = null,
    @field:Valid @ManyToOne val cartao: Cartao? = null
) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null

    @field:Valid
    @field:Size(min = 1)
    @OneToMany(mappedBy = "compra", cascade = [PERSIST])
    val transacoes: MutableList<Transacao> = mutableListOf()

    fun precondicaoCredito(): Boolean {
        return cartao != null && conta == null
    }

    fun precodicaoNaoCredito(status: StatusPagamento?): Boolean {
        return cartao == null && status != null
    }

    fun verificarPrimeiroVencimento(): LocalDate {
        var vencimento: LocalDate? = null

        if (realizadaEm.dayOfMonth.compareTo(cartao!!.diaFechamento) < 1) {

            vencimento = if (cartao.mesVencimentoMesmoFechamento) {
                LocalDate.of(realizadaEm.year, realizadaEm.month, cartao.diaVencimento)
            } else {
                val date = realizadaEm.plusMonths(1)
                LocalDate.of(date.year, date.month, cartao.diaVencimento)
            }

        } else {
            vencimento = if (cartao.mesVencimentoMesmoFechamento) {
                val date = realizadaEm.plusMonths(1)
                LocalDate.of(date.year, date.month, cartao.diaVencimento)
            } else {
                val date = realizadaEm.plusMonths(2)
                LocalDate.of(date.year, date.month, cartao.diaVencimento)
            }
        }

        return vencimento
    }

    fun gerarTransacoes(primeiroVencimento: LocalDate?, status: StatusPagamento?) {
        for (i in 1..numeroDeParcelas) {
            val valor: BigDecimal = this.valor.divide(
                numeroDeParcelas.toBigDecimal(),
                2,
                RoundingMode.HALF_EVEN
            )
            val vencimento: LocalDate = if (i == 1) primeiroVencimento!!
            else primeiroVencimento!!.plusMonths(i.toLong())
            transacoes.add(Transacao(this, valor, vencimento, status!!))
        }
    }

}
