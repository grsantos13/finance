package br.com.gn.categoria

class CategoriaResponse(categoria: Categoria) {
    val id = categoria.id
    val nome = categoria.nome
    val movimento = categoria.movimento
    val umaPorMes = categoria.umaPorMes
}