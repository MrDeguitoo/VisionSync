package com.duoc.visionsyncc1

import android.app.Application
import com.duoc.visionsyncc1.data.AppDatabase
import com.duoc.visionsyncc1.repository.ExamenRepository

/**
 * Application class: crea la base de datos y el repositorio una sola vez
 * (Singleton) y los deja disponibles para AppViewModelProvider.
 */
class VisionSyncApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val examenRepository: ExamenRepository by lazy {
        ExamenRepository(database.examenDao())
    }
}
