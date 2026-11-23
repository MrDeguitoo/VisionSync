package com.duoc.visionsyncc1.viewmodel

import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.repository.ExamenRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExamenesViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val ana = ExamenEntity(
        id = 1, pacienteNombre = "Ana Pérez", pacienteRut = "12345678-5",
        tipoExamen = "Campimetría", fecha = "20/11/2026", archivoUri = null
    )
    private val luis = ExamenEntity(
        id = 2, pacienteNombre = "Luis Soto", pacienteRut = "11111111-1",
        tipoExamen = "Topografía", fecha = "21/11/2026", archivoUri = null
    )
    private val anaRojas = ExamenEntity(
        id = 3, pacienteNombre = "Ana Rojas", pacienteRut = "22222222-2",
        tipoExamen = "Topografía", fecha = "22/11/2026", archivoUri = null
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun crearVm(): ExamenesViewModel {
        val repo = mockk<ExamenRepository>()
        every { repo.observarTodos() } returns flowOf(listOf(ana, luis, anaRojas))
        return ExamenesViewModel(repo)
    }

    @Test
    fun `sin filtros muestra todos los examenes`() = runTest(dispatcher) {
        val vm = crearVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.examenesFiltrados.collect {}
        }
        advanceUntilIdle()

        assertEquals(listOf(ana, luis, anaRojas), vm.examenesFiltrados.value)
    }

    @Test
    fun `buscar por nombre ignora mayusculas`() = runTest(dispatcher) {
        val vm = crearVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.examenesFiltrados.collect {}
        }

        vm.onBusquedaCambiada("ANA")
        advanceUntilIdle()

        assertEquals(listOf(ana, anaRojas), vm.examenesFiltrados.value)
    }

    @Test
    fun `buscar por rut encuentra el examen`() = runTest(dispatcher) {
        val vm = crearVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.examenesFiltrados.collect {}
        }

        vm.onBusquedaCambiada("11111111")
        advanceUntilIdle()

        assertEquals(listOf(luis), vm.examenesFiltrados.value)
    }

    @Test
    fun `filtrar por tipo deja solo ese tipo`() = runTest(dispatcher) {
        val vm = crearVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.examenesFiltrados.collect {}
        }

        vm.onTipoFiltroSeleccionado("Topografía")
        advanceUntilIdle()

        assertEquals(listOf(luis, anaRojas), vm.examenesFiltrados.value)
    }

    @Test
    fun `busqueda y tipo juntos se combinan`() = runTest(dispatcher) {
        val vm = crearVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.examenesFiltrados.collect {}
        }

        vm.onBusquedaCambiada("ana")
        vm.onTipoFiltroSeleccionado("Topografía")
        advanceUntilIdle()

        assertEquals(listOf(anaRojas), vm.examenesFiltrados.value)
    }

    @Test
    fun `sin coincidencias devuelve lista vacia`() = runTest(dispatcher) {
        val vm = crearVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.examenesFiltrados.collect {}
        }

        vm.onBusquedaCambiada("zzz")
        advanceUntilIdle()

        assertTrue(vm.examenesFiltrados.value.isEmpty())
    }

    @Test
    fun `tocar dos veces el mismo tipo quita el filtro`() = runTest(dispatcher) {
        val vm = crearVm()

        vm.onTipoFiltroSeleccionado("Topografía")
        assertEquals("Topografía", vm.tipoFiltro.value)

        vm.onTipoFiltroSeleccionado("Topografía")
        assertNull(vm.tipoFiltro.value)
    }

    @Test
    fun `autorizarExamen guarda observaciones sin espacios con estado Autorizado y avisa`() = runTest(dispatcher) {
        // Dado: un repositorio que acepta actualizar
        val repo = mockk<ExamenRepository>()
        every { repo.observarTodos() } returns flowOf(emptyList())
        coEvery { repo.actualizarObservaciones(any(), any(), any()) } just Runs
        val vm = ExamenesViewModel(repo)
        var avisado = false

        // Cuando: el médico autoriza con observaciones
        vm.autorizarExamen(ana, "  Sin hallazgos  ") { avisado = true }
        advanceUntilIdle()

        // Entonces: se guardó limpio, como Autorizado, y se ejecutó el aviso
        coVerify(exactly = 1) { repo.actualizarObservaciones(1, "Sin hallazgos", "Autorizado") }
        assertTrue(avisado)
    }
}
