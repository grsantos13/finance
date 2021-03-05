package br.com.gn.entrada

import br.com.gn.categoria.CategoriaResponse
import br.com.gn.conta.ContaResponse
import com.fasterxml.jackson.annotation.JsonFormat

class EntradaResponse(entrada: Entrada) {
    val id = entrada.id!!

    @JsonFormat(pattern = "yyyy-MM-dd")
    val realizadaEm = entrada.realizadaEm
    val categoria = CategoriaResponse(entrada.categoria)
    val descricao = entrada.descricao
    val formaDePagamento = entrada.formaDePagamento
    val valor = entrada.valor
    val fixa = entrada.fixa
    val valorVariavel = entrada.valorVariavel
    val conta = ContaResponse(entrada.conta)
    val status = entrada.status
}