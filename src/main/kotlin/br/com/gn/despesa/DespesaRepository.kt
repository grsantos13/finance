package br.com.gn.despesa

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface DespesaRepository : JpaRepository<Despesa, Long> {
    fun findByFixa(fixa: Boolean): List<Despesa>
}