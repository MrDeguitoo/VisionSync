package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.model.UserEntity
import com.duoc.visionsyncc1.network.LoginRequest
import com.duoc.visionsyncc1.network.LoginResponse
import com.duoc.visionsyncc1.network.SesionStorage
import com.duoc.visionsyncc1.network.VisionSyncApi
import kotlin.runCatching

/**
 * Repositorio de autenticación - Login real contra backend.
 * Reemplaza el login local con contraseñas en Room.
 */
class AuthRepository(
    private val api: VisionSyncApi,
    private val sesionStorage: SesionStorage
) {

    /**
     * Login contra el backend.
     * Guarda el token cifrado y devuelve rol, nombre y pacienteId.
     */
    suspend fun login(email: String, password: String): Result<LoginResult> = runCatching {
        val request = LoginRequest(email = email.trim().lowercase(), password = password)
        val response = api.login(request)

        // Guardar token cifrado
        sesionStorage.guardar(response.token)

        LoginResult(
            token = response.token,
            rol = response.rol,
            nombre = response.nombre,
            pacienteId = response.pacienteId
        )
    }

    /**
     * Cerrar sesión: borra el token y navega al login.
     */
    fun logout() {
        sesionStorage.borrar()
    }

    /**
     * Verifica si hay una sesión activa (token guardado).
     */
    fun hasActiveSession(): Boolean = sesionStorage.leer() != null
}

data class LoginResult(
    val token: String,
    val rol: String,
    val nombre: String,
    val pacienteId: Long?
)

/**
 * Mapeo de LoginResult a UserEntity para compatibilidad con navegación existente
 */
fun LoginResult.toUserEntity(): UserEntity = UserEntity(
    id = 0, // Se asignará al obtener del backend si es necesario
    email = "", // No viene en login response, se puede obtener aparte
    passwordHash = "", // Ya no se guarda en la app
    nombre = nombre,
    rol = rol
)