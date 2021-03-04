package br.com.gn.cartao

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface CartaoRepository : JpaRepository<Cartao, Long>