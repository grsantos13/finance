package br.com.gn.compra

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface CompraRepository : JpaRepository<Compra, Long> {
    fun findByFixa(b: Boolean): List<Compra>
}