package com.duoc.visionsyncc1

import android.app.Application
import com.duoc.visionsyncc1.data.AppDatabase
import com.duoc.visionsyncc1.model.UserEntity
import com.duoc.visionsyncc1.network.ExamenRemoteDataSourceImpl
import com.duoc.visionsyncc1.network.NetworkModule
import com.duoc.visionsyncc1.network.SyncStateManager
import com.duoc.visionsyncc1.network.TokenStorage
import com.duoc.visionsyncc1.repository.AuthRepository
import com.duoc.visionsyncc1.repository.ConsultaRepository
import com.duoc.visionsyncc1.repository.ExamenRepository
import com.duoc.visionsyncc1.repository.FhirRepository
import com.duoc.visionsyncc1.repository.PacienteRepository
import com.duoc.visionsyncc1.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Application class: crea la base de datos y los repositorios una sola vez
 * (Singleton) y los deja disponibles para AppViewModelProvider.
 */
class VisionSyncApp : Application() {

    // Base de datos
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    // Almacenamiento de token cifrado
    val tokenStorage: TokenStorage by lazy { TokenStorage(this) }

    // Módulo de red (Retrofit + APIs)
    val visionSyncApi: com.duoc.visionsyncc1.network.VisionSyncApi by lazy {
        NetworkModule.provideVisionSyncApi(this, tokenStorage)
    }

    val fhirApi: com.duoc.visionsyncc1.network.FhirApi by lazy {
        NetworkModule.provideFhirApi()
    }

    // Data sources remotos (para testing con MockK)
    val examenRemoteDataSource: ExamenRemoteDataSourceImpl by lazy {
        ExamenRemoteDataSourceImpl(visionSyncApi)
    }

    // Manager de estado de sincronización
    val syncStateManager: SyncStateManager by lazy { SyncStateManager() }

    // Repositorios
    val examenRepository: ExamenRepository by lazy {
        ExamenRepository(
            dao = database.examenDao(),
            remote = examenRemoteDataSource,
            syncStateManager = syncStateManager
        )
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(visionSyncApi, tokenStorage)
    }

    val fhirRepository: FhirRepository by lazy {
        FhirRepository(fhirApi)
    }

    val consultaRepository: ConsultaRepository by lazy {
        ConsultaRepository(database.consultaDao())
    }

    val pacienteRepository: PacienteRepository by lazy {
        PacienteRepository(database.pacienteDao())
    }

    // Repositorio local de usuarios (para login offline/seed)
    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

    override fun onCreate() {
        super.onCreate()
        // Iniciar observación de conexión para sincronización automática
        iniciarObservacionConexion()
        // Poblar base de datos con usuarios de prueba
        poblarUsuariosPrueba()
    }

    private fun iniciarObservacionConexion() {
        CoroutineScope(Dispatchers.IO).launch {
            com.duoc.visionsyncc1.util.observarConexion(this@VisionSyncApp)
                .distinctUntilChanged()
                .collect { conectado ->
                    syncStateManager.setOnline(conectado)
                    if (conectado) {
                        // Al reconectar: sincronizar pendientes y refrescar
                        examenRepository.sincronizarPendientes()
                        examenRepository.refrescar()
                    }
                }
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
                ),
                UserEntity(
                    email = "admin@visionsync.cl",
                    passwordHash = passwordHash,
                    nombre = "Administrador",
                    rol = "ADMINISTRADOR"
                )
            )

            usuariosPrueba.forEach { database.userDao().insertar(it) }
        }
    }
}