package br.com.gn.entrada

import br.com.gn.categoria.Categoria
import br.com.gn.categoria.CategoriaResponse
import br.com.gn.conta.Conta
import br.com.gn.conta.ContaResponse
import br.com.gn.pagamento.FormaDePagamento
import br.com.gn.pagamento.StatusPagamento
import br.com.gn.shared.validation.ExistsResource
import com.fasterxml.jackson.annotation.JsonFormat
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
data class EntradaRequest(
    @field:NotNull val realizadaEm: LocalDate = LocalDate.now(),
    @field:NotNull @field:ExistsResource(field = "id", domainClass = Categoria::class) val idCategoria: Long,
    @field:NotBlank @field:Size(max = 100) val descricao: String,
    @field:NotNull @Enumerated(EnumType.STRING) val formaDePagamento: FormaDePagamento,
    @field:NotNull @field:Positive val valor: BigDecimal,
    @field:NotNull val fixa: Boolean = false,
    @field:NotNull val valorVariavel: Boolean = false,
    @field:ExistsResource(field = "id", domainClass = Conta::class) val idConta: Long,
    @field:NotNull @Enumerated(EnumType.STRING) val status: StatusPagamento
) {

    fun toModel(manager: EntityManager): Entrada {
        val categoria = manager.find(Categoria::class.java, idCategoria)
            ?: throw HttpStatusException(NOT_FOUND, "Categoria não encontrada para o id $idCategoria")
        val conta = manager.find(Conta::class.java, idConta)
            ?: throw HttpStatusException(NOT_FOUND, "Conta não encontrada para o id $idConta")

        return Entrada(
            realizadaEm = realizadaEm,
            categoria = categoria,
            descricao = descricao,
            formaDePagamento = formaDePagamento,
            valor = valor,
            fixa = fixa,
            valorVariavel = valorVariavel,
            conta = conta,
            status = status
        )
    }
}

class EntradaResponse(entrada: Entrada) {
    val id = entrada.id!!

    @JsonFormat(pattern = "yyyy-MM-dd")
    val realizadaEm = entrada.realizadaEm
    val categoria = CategoriaResponse(entrada.categoria)
    val descricao = entrada.descricao
    val formaDePagamento = entrada.formaDePagamento
    val valor = entrada.valor
    val fixa = entrada.fixa
    val valorVariavel = entrada.valorVariavel
    val conta = ContaResponse(entrada.conta)
    val status = entrada.status
}