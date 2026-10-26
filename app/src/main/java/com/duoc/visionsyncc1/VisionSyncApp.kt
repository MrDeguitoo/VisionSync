package com.duoc.visionsyncc1

import android.app.Application
import com.duoc.visionsyncc1.data.AppDatabase
import com.duoc.visionsyncc1.repository.ExamenRepository

class VisionSyncApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { ExamenRepository(database.examenDao()) }
}