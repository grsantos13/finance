package br.com.gn.shared.validation

import br.com.gn.categoria.Categoria
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import javax.inject.Singleton
import javax.persistence.EntityManager

@Singleton
class CategoriaDuplicadaNoMesValidator(private val manager: EntityManager) {

    fun validarCategoriaDuplicadaNoMes(
        categoria: Categoria,
        realizadaEm: LocalDate,
        entidade: String
    ) {
        val query = manager.createQuery(
            " select 1 from $entidade c where c.categoria.id = :idCategoria " +
                    " and c.realizadaEm between :inicio and :fim "
        )
        query.setParameter("inicio", realizadaEm.withDayOfMonth(1))
        query.setParameter("fim", realizadaEm.withDayOfMonth(realizadaEm.lengthOfMonth()))
        query.setParameter("idCategoria", categoria.id)
        val existeLancamento = query.resultList.isNotEmpty()
        if (existeLancamento) {
            val mes = realizadaEm.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            throw HttpStatusException(
                HttpStatus.PRECONDITION_FAILED,
                "Já existe um lançamento para a categoria ${categoria.nome} em $mes de ${realizadaEm.year}"
            )
        }

    }

}