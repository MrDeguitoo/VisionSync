package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.data.ExamenDao
import com.duoc.visionsyncc1.model.ExamenEntity
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExamenRepositoryTest {

    private val dao = mockk<ExamenDao>()
    private val repo = ExamenRepository(dao)

    private fun examen(id: Int = 1, estado: String = "Pendiente") = ExamenEntity(
        id = id,
        pacienteNombre = "Ana Pérez",
        pacienteRut = "12345678-5",
        tipoExamen = "Campimetría",
        fecha = "20/11/2026",
        archivoUri = "content://archivo/examen.pdf",
        estado = estado
    )

    @Test
    fun `guardar envia el examen al DAO una sola vez`() = runTest {
        // Dado: un DAO simulado que acepta insertar
        val e = examen()
        coEvery { dao.insertar(any()) } just Runs

        // Cuando: se guarda desde el repositorio
        repo.guardar(e)

        // Entonces: el DAO recibió exactamente ese examen
        coVerify(exactly = 1) { dao.insertar(e) }
    }

    @Test
    fun `observarTodos entrega la lista que emite el DAO`() = runTest {
        // Dado: un DAO que emite dos exámenes
        val lista = listOf(examen(1), examen(2))
        every { dao.observarTodos() } returns flowOf(lista)

        // Cuando / Entonces: el repositorio entrega la misma lista
        assertEquals(lista, repo.observarTodos().first())
    }

    @Test
    fun `observarAutorizados entrega solo lo que emite el DAO`() = runTest {
        // Dado: un DAO que emite un examen autorizado
        val lista = listOf(examen(3, "Autorizado"))
        every { dao.observarAutorizados() } returns flowOf(lista)

        // Cuando / Entonces
        assertEquals(lista, repo.observarAutorizados().first())
    }

    @Test
    fun `obtenerPorId devuelve el examen cuando existe`() = runTest {
        // Dado: un examen con id 5 en el DAO
        val e = examen(5)
        coEvery { dao.obtenerPorId(5) } returns e

        // Cuando / Entonces
        assertEquals(e, repo.obtenerPorId(5))
    }

    @Test
    fun `obtenerPorId devuelve null cuando no existe`() = runTest {
        // Dado: un DAO que no encuentra el id
        coEvery { dao.obtenerPorId(99) } returns null

        // Cuando / Entonces
        assertNull(repo.obtenerPorId(99))
    }

    @Test
    fun `actualizarObservaciones pasa id, texto y estado al DAO`() = runTest {
        // Dado: un DAO que acepta actualizar
        coEvery { dao.actualizar(any(), any(), any()) } just Runs

        // Cuando: se actualiza
        repo.actualizarObservaciones(3, "Sin hallazgos", "Autorizado")

        // Entonces: el DAO recibió los tres datos tal cual
        coVerify(exactly = 1) { dao.actualizar(3, "Sin hallazgos", "Autorizado") }
    }
}
