package br.com.gn.despesa

import com.fasterxml.jackson.annotation.JsonFormat


class TransacaoResponse(transacao: Transacao) {
    val id = transacao.id!!
    val valor = transacao.valor

    @JsonFormat(pattern = "yyyy-MM-dd")
    val vencimento = transacao.vencimento
    val status = transacao.status
}