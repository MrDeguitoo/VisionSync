package com.duoc.visionsyncc1

import android.app.Application
import com.duoc.visionsyncc1.data.AppDatabase
import com.duoc.visionsyncc1.model.UserEntity
import com.duoc.visionsyncc1.repository.ExamenRepository
import com.duoc.visionsyncc1.repository.PacienteRepository
import com.duoc.visionsyncc1.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Application class: crea la base de datos y el repositorio una sola vez
 * (Singleton) y los deja disponibles para AppViewModelProvider.
 */
class VisionSyncApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val examenRepository: ExamenRepository by lazy {
        ExamenRepository(database.examenDao())
    }

    val pacienteRepository: PacienteRepository by lazy {
        PacienteRepository(database.pacienteDao())
    }

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

    override fun onCreate() {
        super.onCreate()
        // Poblar base de datos con usuarios de prueba
        CoroutineScope(Dispatchers.IO).launch {
            poblarUsuariosPrueba()
        }
    }

    private suspend fun poblarUsuariosPrueba() {
        val count = database.userDao().contarUsuarios()
        if (count == 0) {
            val passwordHash = "123456" // En producción usar hash real (BCrypt)

            val usuariosPrueba = listOf(
                UserEntity(
                    email = "jduarte@gmail.com",
                    passwordHash = passwordHash,
                    nombre = "José Duarte",
                    rol = "TECNOLOGO"
                ),
                UserEntity(
                    email = "mgarcia@gmail.com",
                    passwordHash = passwordHash,
                    nombre = "María García",
                    rol = "MEDICO"
                ),
                UserEntity(
                    email = "pperez@gmail.com",
                    passwordHash = passwordHash,
                    nombre = "Pedro Pérez",
                    rol = "PACIENTE"
                )
            )

            usuariosPrueba.forEach { database.userDao().insertar(it) }
        }
    }
}
