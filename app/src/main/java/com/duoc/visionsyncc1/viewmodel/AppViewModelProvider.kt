package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.duoc.visionsyncc1.VisionSyncApp

/**
 * Fábrica centralizada de ViewModels que necesitan repositorios.
 * Uso en una pantalla:  viewModel(factory = AppViewModelProvider.Factory)
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            PacienteViewModel(visionSyncApp().examenRepository)
        }
        initializer {
            ResumenExamenViewModel(visionSyncApp().examenRepository)
        }
        initializer {
            ExamenesViewModel(visionSyncApp().examenRepository)
        }
        initializer {
            RegistroExamenViewModel(visionSyncApp().examenRepository)
        }
        initializer {
            LoginViewModel(visionSyncApp().authRepository)
        }
        initializer {
            FhirViewModel(visionSyncApp().fhirRepository)
        }
        initializer {
            DashTecnologoViewModel(visionSyncApp().examenRepository)
        }
        // Aquí se agregan los ViewModels de A y B cuando usen Room.
    }
}

fun CreationExtras.visionSyncApp(): VisionSyncApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VisionSyncApp