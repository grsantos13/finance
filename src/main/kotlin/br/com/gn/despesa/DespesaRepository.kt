package br.com.gn.despesa

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.time.LocalDate

@Repository
interface DespesaRepository : JpaRepository<Despesa, Long> {
    fun existsByCategoriaIdAndRealizadaEmBetween
                (id: Long, inicio: LocalDate, fim: LocalDate): Boolean
    fun findByFixa(fixa: Boolean): List<Despesa>
    fun findByFixaAndValorVariavel(fixa: Boolean, valorVariavel: Boolean): List<Despesa>
}