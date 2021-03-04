package br.com.gn.compra

import br.com.gn.categoria.Categoria
import br.com.gn.categoria.CategoriaRepository
import br.com.gn.categoria.CategoriaResponse
import br.com.gn.shared.validation.ExistsResource
import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus.NOT_FOUND
import io.micronaut.http.exceptions.HttpStatusException
import java.time.LocalDate
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PastOrPresent
import javax.validation.constraints.Size

@Introspected
data class CompraRequest(
    @field:NotNull @field:PastOrPresent val realizadaEm: LocalDate = LocalDate.now(),
    @field:NotNull @field:ExistsResource(field = "id", domainClass = Categoria::class) val idCategoria: Long,
    @field:NotBlank @field:Size(max = 100) val descricao: String,
    @field:NotNull @field:Min(1) val numeroDeParcelas: Int,
    @field:NotNull val formaDePagamento: FormaDePagamento,
    val conta: Conta? = null,
    val cartao: Cartao? = null
) {
    fun toModel(categoriaRepository: CategoriaRepository): Compra {
        val categoria = categoriaRepository.findById(idCategoria)
            .orElseThrow { throw HttpStatusException(NOT_FOUND, "Categoria não encontrada para o id $idCategoria") }
        return Compra(
            realizadaEm = realizadaEm,
            categoria = categoria,
            descricao = descricao,
            numeroDeParcelas = numeroDeParcelas,
            formaDePagamento = formaDePagamento,
            conta = conta,
            cartao = cartao
        )
    }

}

class CompraResponse(compra: Compra) {
    @JsonFormat(pattern = "yyyy-MM-dd")
    val realizadaEm: LocalDate = compra.realizadaEm
    val categoria: CategoriaResponse = CategoriaResponse(compra.categoria)
    val descricao: String = compra.descricao
    val numeroDeParcelas: Int = compra.numeroDeParcelas
    val formaDePagamento: FormaDePagamento = compra.formaDePagamento
    val conta: Conta? = compra.conta
    val cartao: Cartao? = compra.cartao
}
