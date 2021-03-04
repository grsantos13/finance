package br.com.gn.entrada

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface EntradaRepository : JpaRepository<Entrada, Long> {
    fun findByFixa(fixa: Boolean): List<Entrada>
}