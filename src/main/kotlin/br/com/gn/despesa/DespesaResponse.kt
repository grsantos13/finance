package br.com.gn.despesa

import br.com.gn.cartao.CartaoResponse
import br.com.gn.categoria.CategoriaResponse
import br.com.gn.despesa.transacao.TransacaoResponse
import br.com.gn.pagamento.FormaDePagamento
import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDate

class DespesaResponse(despesa: Despesa) {
    val id = despesa.id!!

    @JsonFormat(pattern = "yyyy-MM-dd")
    val realizadaEm: LocalDate = despesa.realizadaEm
    val categoria: CategoriaResponse = CategoriaResponse(despesa.categoria)
    val descricao: String = despesa.descricao
    val numeroDeParcelas: Int = despesa.numeroDeParcelas
    val formaDePagamento: FormaDePagamento = despesa.formaDePagamento
    val valor: BigDecimal = despesa.valor
    val conta: String? = despesa.conta?.nome
    val cartao: CartaoResponse? = if (despesa.cartao == null) null else CartaoResponse(cartao = despesa.cartao)
    val fixa = despesa.fixa
    val valorVariavel = despesa.valorVariavel
    val transacoes: List<TransacaoResponse> = despesa.transacoes.map { transacao -> TransacaoResponse(transacao) }
}