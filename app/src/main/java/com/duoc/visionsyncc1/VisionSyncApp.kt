package com.duoc.visionsyncc1

import android.app.Application
import com.duoc.visionsyncc1.data.AppDatabase
import com.duoc.visionsyncc1.network.ExamenRemoteDataSourceImpl
import com.duoc.visionsyncc1.network.NetworkModule
import com.duoc.visionsyncc1.network.SyncStateManager
import com.duoc.visionsyncc1.network.TokenStorage
import com.duoc.visionsyncc1.repository.AuthRepository
import com.duoc.visionsyncc1.repository.ExamenRepository
import com.duoc.visionsyncc1.repository.FhirRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Application class: crea la base de datos, red, repositorios y managers una sola vez
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

    override fun onCreate() {
        super.onCreate()
        // Iniciar observación de conexión para sincronización automática
        iniciarObservacionConexion()
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
}
