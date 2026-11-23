package com.duoc.visionsyncc1.viewmodel

import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.repository.ExamenRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroExamenViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `guardar con datos invalidos muestra errores y no llama al repositorio`() = runTest(dispatcher) {
        // Dado: un formulario vacío y un repositorio simulado
        val repo = mockk<ExamenRepository>(relaxed = true)
        val vm = RegistroExamenViewModel(repo)

        // Cuando: se intenta guardar
        vm.guardar()

        // Entonces: aparecen los 4 errores, no se guarda nada y el estado sigue inactivo
        val form = vm.form.value
        assertNotNull(form.nombreError)
        assertNotNull(form.rutError)
        assertNotNull(form.tipoError)
        assertNotNull(form.archivoError)
        assertEquals(GuardadoState.Inactivo, vm.guardado.value)
        coVerify(exactly = 0) { repo.guardar(any()) }
    }

    @Test
    fun `guardar con datos validos guarda el examen como Pendiente y termina en Exito`() = runTest(dispatcher) {
        // Dado: un repositorio que acepta guardar y un formulario completo
        val repo = mockk<ExamenRepository>()
        val capturado = slot<ExamenEntity>()
        coEvery { repo.guardar(capture(capturado)) } just Runs
        val vm = RegistroExamenViewModel(repo)
        vm.onNombreChange("  Ana Pérez  ")
        vm.onRutChange("12345678-5")
        vm.onTipoChange("Campimetría")
        vm.onArchivoSeleccionado("content://archivo/examen.pdf")

        // Cuando: se guarda y se deja correr la corrutina
        vm.guardar()
        advanceUntilIdle()

        // Entonces: se guardó una vez, sin espacios sobrantes y con estado Pendiente
        assertEquals(GuardadoState.Exito, vm.guardado.value)
        assertEquals("Ana Pérez", capturado.captured.pacienteNombre)
        assertEquals("Pendiente", capturado.captured.estado)
        coVerify(exactly = 1) { repo.guardar(any()) }
    }

    @Test
    fun `al escribir en un campo se borra su mensaje de error`() = runTest(dispatcher) {
        // Dado: un formulario vacío que ya mostró errores
        val vm = RegistroExamenViewModel(mockk(relaxed = true))
        vm.guardar()
        assertNotNull(vm.form.value.nombreError)

        // Cuando: el usuario escribe un nombre
        vm.onNombreChange("Ana")

        // Entonces: el error del nombre desaparece
        assertNull(vm.form.value.nombreError)
    }

    @Test
    fun `limpiarFormulario deja todo en blanco y el estado inactivo`() = runTest(dispatcher) {
        // Dado: un formulario con datos
        val vm = RegistroExamenViewModel(mockk(relaxed = true))
        vm.onNombreChange("Ana")
        vm.onRutChange("12345678-5")

        // Cuando: se limpia
        vm.limpiarFormulario()

        // Entonces: vuelve al estado inicial
        assertEquals(FormularioState(), vm.form.value)
        assertEquals(GuardadoState.Inactivo, vm.guardado.value)
    }
}