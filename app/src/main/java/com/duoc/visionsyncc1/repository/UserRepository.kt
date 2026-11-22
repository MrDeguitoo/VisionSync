package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.data.UserDao
import com.duoc.visionsyncc1.model.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao: UserDao) {
    suspend fun guardar(user: UserEntity) = dao.insertar(user)

    suspend fun obtenerPorEmail(email: String): UserEntity? = dao.obtenerPorEmail(email)

    fun observarTodos(): Flow<List<UserEntity>> = dao.observarTodos()

    suspend fun eliminarTodos() = dao.eliminarTodos()

    suspend fun eliminarUsuario(id: Int) = dao.eliminarPorId(id)

    suspend fun actualizarRol(id: Int, rol: String) = dao.actualizarRol(id, rol)

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