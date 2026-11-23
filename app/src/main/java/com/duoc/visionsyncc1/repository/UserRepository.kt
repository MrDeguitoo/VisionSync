package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.data.UserDao
import com.duoc.visionsyncc1.model.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao: UserDao) {
    suspend fun guardar(user: UserEntity) = dao.insertar(user)

    suspend fun obtenerPorEmail(email: String): UserEntity? = dao.obtenerPorEmail(email)

    fun observarTodos(): Flow<List<UserEntity>> = dao.observarTodos()

    suspend fun eliminarTodos() = dao.eliminarTodos()

    suspend fun contarUsuarios(): Int = dao.contarUsuarios()

    suspend fun obtenerTodos(): List<UserEntity> = dao.obtenerTodos()

    suspend fun actualizar(user: UserEntity) = dao.actualizar(user)

    suspend fun eliminar(id: Int) = dao.eliminar(id)

    // Función auxiliar para verificar contraseña (en producción usar bcrypt/argon2)
    fun verificarContrasena(input: String, hash: String): Boolean {
        // Para demo: comparación simple. En producción: BCrypt.checkpw(input, hash)
        return input == hash
    }

    // Función auxiliar para hashear contraseña (en producción usar bcrypt/argon2)
    fun hashearContrasena(input: String): String {
        // Para demo: sin hash real. En producción: BCrypt.hashpw(input, BCrypt.gensalt())
        return input
    }
}