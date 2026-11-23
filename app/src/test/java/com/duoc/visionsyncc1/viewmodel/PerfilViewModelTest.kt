package com.duoc.visionsyncc1.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class PerfilViewModelTest {

    @Test
    fun `expone los tres perfiles y parte con el medico seleccionado`() {
        // Dado / Cuando: se crea el ViewModel
        val vm = PerfilViewModel()

        // Entonces: hay 3 perfiles y el seleccionado es el de id 1 (médico)
        assertEquals(3, vm.perfiles.size)
        assertEquals(1, vm.perfilSeleccionado.value.id)
    }

    @Test
    fun `seleccionarPerfil cambia el perfil seleccionado`() {
        // Dado: un ViewModel y el perfil del tecnólogo
        val vm = PerfilViewModel()
        val tecnologo = vm.perfiles[1]

        // Cuando: se selecciona
        vm.seleccionarPerfil(tecnologo)

        // Entonces: el perfil seleccionado cambió
        assertEquals(tecnologo, vm.perfilSeleccionado.value)
    }

    @Test
    fun `textoBoton cambia segun el perfil elegido`() {
        // Dado: un ViewModel que parte con el médico
        val vm = PerfilViewModel()
        assertEquals("Continuar como Médico", vm.textoBoton())

        // Cuando / Entonces: al elegir cada perfil, cambia el texto
        vm.seleccionarPerfil(vm.perfiles[1])
        assertEquals("Continuar como Tecnólogo", vm.textoBoton())

        vm.seleccionarPerfil(vm.perfiles[2])
        assertEquals("Continuar como paciente", vm.textoBoton())
    }
}
