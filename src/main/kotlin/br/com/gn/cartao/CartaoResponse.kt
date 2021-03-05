package br.com.gn.cartao

class CartaoResponse(cartao: Cartao) {
    val id = cartao.id!!
    val nome = cartao.nome
    val diaVencimento = cartao.diaVencimento
    val diaFechamento = cartao.diaFechamento
    val mesVencimentoMesmoFechamento = cartao.mesVencimentoMesmoFechamento
}