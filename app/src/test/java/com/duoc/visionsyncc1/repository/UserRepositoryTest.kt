package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.data.UserDao
import com.duoc.visionsyncc1.model.UserEntity
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryTest {

    private val dao = mockk<UserDao>()
    private val repo = UserRepository(dao)

    private val usuario = UserEntity(
        id = 1,
        email = "medico@visionsync.test",
        passwordHash = "demo1234",
        nombre = "Médico Ficticio",
        rol = "MEDICO"
    )

    @Test
    fun `guardar envia el usuario al DAO`() = runTest {
        // Dado: un DAO que acepta insertar
        coEvery { dao.insertar(any()) } just Runs

        // Cuando
        repo.guardar(usuario)

        // Entonces
        coVerify(exactly = 1) { dao.insertar(usuario) }
    }

    @Test
    fun `obtenerPorEmail devuelve el usuario si existe`() = runTest {
        coEvery { dao.obtenerPorEmail("medico@visionsync.test") } returns usuario

        assertEquals(usuario, repo.obtenerPorEmail("medico@visionsync.test"))
    }

    @Test
    fun `obtenerPorEmail devuelve null si no existe`() = runTest {
        coEvery { dao.obtenerPorEmail("nadie@visionsync.test") } returns null

        assertNull(repo.obtenerPorEmail("nadie@visionsync.test"))
    }

    @Test
    fun `observarTodos entrega la lista del DAO`() = runTest {
        every { dao.observarTodos() } returns flowOf(listOf(usuario))

        assertEquals(listOf(usuario), repo.observarTodos().first())
    }

    @Test
    fun `eliminarTodos llama al DAO`() = runTest {
        coEvery { dao.eliminarTodos() } just Runs

        repo.eliminarTodos()

        coVerify(exactly = 1) { dao.eliminarTodos() }
    }

    @Test
    fun `verificarContrasena es true solo si coincide`() {
        assertTrue(repo.verificarContrasena("demo1234", "demo1234"))
        assertFalse(repo.verificarContrasena("otra", "demo1234"))
    }

    @Test
    fun `hashearContrasena devuelve el mismo texto en esta version de demo`() {
        assertEquals("demo1234", repo.hashearContrasena("demo1234"))
    }
}
