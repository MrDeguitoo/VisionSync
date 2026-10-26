package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.duoc.visionsyncc1.VisionSyncApp

fun CreationExtras.visionSyncApp(): VisionSyncApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VisionSyncApp

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { RegistroExamenViewModel(visionSyncApp().repository) }
        initializer { ExamenesViewModel(visionSyncApp().repository) }
        initializer { PacienteViewModel(visionSyncApp().repository) }
    }
}