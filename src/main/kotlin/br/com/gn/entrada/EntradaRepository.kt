package br.com.gn.entrada

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.time.LocalDate

@Repository
interface EntradaRepository : JpaRepository<Entrada, Long> {
    fun findByFixa(fixa: Boolean): List<Entrada>
    fun findByFixaAndValorVariavelAndRealizadaEmBetween(
        fixa: Boolean,
        valorVariavel: Boolean,
        start: LocalDate,
        end: LocalDate
    ): List<Entrada>
}