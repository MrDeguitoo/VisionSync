package com.duoc.visionsyncc1.viewmodel

import com.duoc.visionsyncc1.model.UserEntity
import com.duoc.visionsyncc1.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val usuario = UserEntity(
        id = 1,
        email = "medico@visionsync.test",
        passwordHash = "demo1234",
        nombre = "Médico Ficticio",
        rol = "MEDICO"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login con campos vacios muestra errores y no consulta el repositorio`() = runTest(dispatcher) {
        // Dado: un formulario vacío
        val repo = mockk<UserRepository>()
        val vm = LoginViewModel(repo)

        // Cuando: se intenta entrar
        vm.intentarLogin()

        // Entonces: hay un error por campo y nunca se consultó al repositorio
        val s = vm.uiState.value
        assertEquals("El email es obligatorio", s.emailError)
        assertEquals("La contraseña no puede estar vacía", s.contrasenaError)
        assertFalse(s.loginExitoso)
        coVerify(exactly = 0) { repo.obtenerPorEmail(any()) }
    }

    @Test
    fun `login con email de formato invalido muestra error de formato`() = runTest(dispatcher) {
        // Dado: un email mal escrito y una contraseña
        val repo = mockk<UserRepository>()
        val vm = LoginViewModel(repo)
        vm.onEmailChange("correo-malo")
        vm.onContrasenaChange("demo1234")

        // Cuando
        vm.intentarLogin()

        // Entonces: error de formato en el email, sin error de contraseña
        val s = vm.uiState.value
        assertEquals("Formato de email inválido", s.emailError)
        assertNull(s.contrasenaError)
        coVerify(exactly = 0) { repo.obtenerPorEmail(any()) }
    }

    @Test
    fun `login con usuario inexistente muestra error en el email`() = runTest(dispatcher) {
        // Dado: un repositorio que no encuentra al usuario
        val repo = mockk<UserRepository>()
        coEvery { repo.obtenerPorEmail("nadie@visionsync.test") } returns null
        val vm = LoginViewModel(repo)
        vm.onEmailChange("Nadie@VisionSync.test")
        vm.onContrasenaChange("demo1234")

        // Cuando
        vm.intentarLogin()
        advanceUntilIdle()

        // Entonces: error de usuario y login no exitoso
        val s = vm.uiState.value
        assertEquals("No existe un usuario con este email", s.emailError)
        assertFalse(s.loginExitoso)
    }

    @Test
    fun `login con contrasena incorrecta muestra error en la contrasena`() = runTest(dispatcher) {
        // Dado: un usuario que existe, pero la contraseña no coincide
        val repo = mockk<UserRepository>()
        coEvery { repo.obtenerPorEmail("medico@visionsync.test") } returns usuario
        every { repo.verificarContrasena("mala", "demo1234") } returns false
        val vm = LoginViewModel(repo)
        vm.onEmailChange("medico@visionsync.test")
        vm.onContrasenaChange("mala")

        // Cuando
        vm.intentarLogin()
        advanceUntilIdle()

        // Entonces
        val s = vm.uiState.value
        assertEquals("Contraseña incorrecta", s.contrasenaError)
        assertFalse(s.loginExitoso)
        assertNull(s.usuario)
    }

    @Test
    fun `login con credenciales correctas es exitoso y entrega el usuario`() = runTest(dispatcher) {
        // Dado: un usuario que existe y una contraseña correcta
        val repo = mockk<UserRepository>()
        coEvery { repo.obtenerPorEmail("medico@visionsync.test") } returns usuario
        every { repo.verificarContrasena("demo1234", "demo1234") } returns true
        val vm = LoginViewModel(repo)
        vm.onEmailChange("Medico@VisionSync.test")
        vm.onContrasenaChange("demo1234")

        // Cuando
        vm.intentarLogin()
        advanceUntilIdle()

        // Entonces: login exitoso con el usuario correcto (el email se normalizó a minúsculas)
        val s = vm.uiState.value
        assertTrue(s.loginExitoso)
        assertEquals(usuario, s.usuario)
        assertNull(s.errorGeneral)
    }

    @Test
    fun `al escribir en un campo se borra su error`() = runTest(dispatcher) {
        // Dado: un formulario vacío que ya mostró errores
        val vm = LoginViewModel(mockk())
        vm.intentarLogin()

        // Cuando: el usuario escribe en cada campo
        vm.onEmailChange("a")
        vm.onContrasenaChange("b")

        // Entonces: los errores desaparecen
        assertNull(vm.uiState.value.emailError)
        assertNull(vm.uiState.value.contrasenaError)
    }

    @Test
    fun `limpiarEstadoExito quita el exito y el usuario`() = runTest(dispatcher) {
        // Dado: un login exitoso
        val repo = mockk<UserRepository>()
        coEvery { repo.obtenerPorEmail("medico@visionsync.test") } returns usuario
        every { repo.verificarContrasena("demo1234", "demo1234") } returns true
        val vm = LoginViewModel(repo)
        vm.onEmailChange("medico@visionsync.test")
        vm.onContrasenaChange("demo1234")
        vm.intentarLogin()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.loginExitoso)

        // Cuando
        vm.limpiarEstadoExito()

        // Entonces
        assertFalse(vm.uiState.value.loginExitoso)
        assertNull(vm.uiState.value.usuario)
    }
}
