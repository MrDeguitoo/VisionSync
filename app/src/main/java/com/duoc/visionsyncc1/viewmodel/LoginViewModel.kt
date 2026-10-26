package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val usuario: String = "",
    val contrasena: String = "",
    val usuarioError: String? = null,
    val contrasenaError: String? = null,
    val loginExitoso: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsuarioChange(valor: String) {
        _uiState.update {
            it.copy(usuario = valor, usuarioError = null, loginExitoso = false)
        }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update {
            it.copy(contrasena = valor, contrasenaError = null, loginExitoso = false)
        }
    }

    fun intentarLogin(): Boolean {
        val estadoActual = _uiState.value
        var hayError = false

        val errorUsuario = if (estadoActual.usuario.isBlank()) {
            hayError = true
            "El nombre de usuario es obligatorio"
        } else null

        val errorContrasena = if (estadoActual.contrasena.isBlank()) {
            hayError = true
            "La contraseña no puede estar vacía"
        } else null

        _uiState.update {
            it.copy(
                usuarioError = errorUsuario,
                contrasenaError = errorContrasena,
                loginExitoso = !hayError
            )
        }

        return !hayError
    }

    fun limpiarEstadoExito() {
        _uiState.update { it.copy(loginExitoso = false) }
    }
}