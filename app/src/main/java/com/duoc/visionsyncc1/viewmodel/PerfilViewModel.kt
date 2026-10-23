package com.duoc.visionsyncc1.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.duoc.visionsyncc1.model.PerfilAcceso
import com.duoc.visionsyncc1.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PerfilViewModel : ViewModel() {
    private val repository = PerfilRepository()

    //los tres perfiles que vienen del repository
    val perfiles: List<PerfilAcceso> = repository.obtenerPerfiles()

    //perfil seleccionado (el medico parte seleccionado, como en la imagen de referencia)
    private val _perfilSeleccionado = MutableStateFlow(perfiles[0])
    val perfilSeleccionado: StateFlow<PerfilAcceso> = _perfilSeleccionado.asStateFlow()
    //cuando  se toca una tarjeta de rol, se cambia el perfil seleccionado
    fun seleccionarPerfil(perfil: PerfilAcceso) {
        _perfilSeleccionado.value = perfil
    }

    //Texto que va dentro del botón según el perfil elegido
    fun textoBoton(): String {
        return when (perfilSeleccionado.value.id) {
            1 -> "Continuar como Médico"
            2 -> "Continuar como Tecnólogo"
            else -> "Continuar como paciente"
        }
    }
}