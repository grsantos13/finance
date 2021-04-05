package br.com.gn.entrada

import br.com.gn.categoria.Categoria
import br.com.gn.categoria.Movimento
import br.com.gn.conta.Conta
import br.com.gn.pagamento.FormaDePagamento
import br.com.gn.pagamento.StatusPagamento
import br.com.gn.shared.validation.ExistsResource
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus.NOT_FOUND
import io.micronaut.http.exceptions.HttpStatusException
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

@Introspected
data class NovaEntradaRequest(
    @field:NotNull val realizadaEm: LocalDate? = LocalDate.now(),
    @field:NotNull @field:ExistsResource(field = "id", domainClass = Categoria::class) val idCategoria: Long?,
    @field:NotBlank @field:Size(max = 100) val descricao: String?,
    @field:NotNull @Enumerated(EnumType.STRING) val formaDePagamento: FormaDePagamento?,
    @field:NotNull @field:Positive val valor: BigDecimal?,
    @field:NotNull val fixa: Boolean? = false,
    @field:NotNull val valorVariavel: Boolean? = false,
    @field:NotNull @field:ExistsResource(field = "id", domainClass = Conta::class) val idConta: Long?,
    @field:NotNull @Enumerated(EnumType.STRING) val status: StatusPagamento?
) {

    fun toModel(manager: EntityManager): Entrada {
        val categoria = manager.find(Categoria::class.java, idCategoria)
            ?: throw HttpStatusException(NOT_FOUND, "Categoria não encontrada para o id $idCategoria")
        val conta = manager.find(Conta::class.java, idConta)
            ?: throw HttpStatusException(NOT_FOUND, "Conta não encontrada para o id $idConta")

        return Entrada(
            realizadaEm = realizadaEm!!,
            categoria = categoria,
            descricao = descricao!!,
            formaDePagamento = formaDePagamento!!,
            valor = valor!!,
            fixa = fixa!!,
            valorVariavel = valorVariavel!!,
            conta = conta,
            status = status!!
        )
    }

    private fun buscarCategoria(manager: EntityManager): Categoria {

        val categorias = manager.createQuery(
            " select c from Categoria c where c.id = :id and c.movimento = :movimento",
            Categoria::class.java
        )
            .setParameter("id", idCategoria)
            .setParameter("movimento", Movimento.ENTRADA)
            .resultList

        if (categorias.isEmpty())
            throw HttpStatusException(NOT_FOUND, "Categoria não encontrada para o id $idCategoria")

        return categorias[0]

    }
}

