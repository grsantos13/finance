package br.com.gn.entrada

import br.com.gn.shared.validation.CategoriaDuplicadaNoMesValidator
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Controller("/entradas")
class EntradaController(
    private val repository: EntradaRepository,
    private val entityManager: EntityManager,
    private val categoriaValidator: CategoriaDuplicadaNoMesValidator
) {

    @Post
    @Transactional
    fun cadastrar(@Body @Valid request: NovaEntradaRequest): HttpResponse<EntradaResponse> {
        val entrada = request.toModel(entityManager)
        categoriaValidator.validarCategoriaDuplicadaNoMes(
            entrada.categoria,
            entrada.realizadaEm,
            entrada::class.qualifiedName!!
        )
        repository.save(entrada)
        return HttpResponse.created(EntradaResponse(entrada))
    }

    @Get("/fixas")
    @Transactional
    fun buscaEntradasFixas(@QueryValue(defaultValue = "false") variavel: Boolean): HttpResponse<List<EntradaResponse>> {
        val start = LocalDate.now().minusMonths(1).run {
            LocalDate.of(year, month, 1)
        }
        val end = with(start) {
            LocalDate.of(year, month, lengthOfMonth())
        }

        val entradasFixas = repository.findByFixaAndValorVariavelAndRealizadaEmBetween(
            fixa = true,
            valorVariavel = variavel,
            start = start,
            end = end
        )
            .map { entrada -> EntradaResponse(entrada) }
        return HttpResponse.ok(entradasFixas)
    }

    @Get
    @Transactional
    fun buscaEntradas(pageable: Pageable): HttpResponse<Page<EntradaResponse>> {
        val page = repository.findAll(pageable)
            .map { entrada -> EntradaResponse(entrada) }
        return HttpResponse.ok(page)
    }
}