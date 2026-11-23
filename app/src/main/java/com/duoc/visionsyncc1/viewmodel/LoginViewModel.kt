package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.model.UserEntity
import com.duoc.visionsyncc1.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val contrasena: String = "",
    val emailError: String? = null,
    val contrasenaError: String? = null,
    val loginExitoso: Boolean = false,
    val errorGeneral: String? = null,
    val usuario: UserEntity? = null
)

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(valor: String) {
        _uiState.update {
            it.copy(email = valor, emailError = null, loginExitoso = false, errorGeneral = null)
        }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update {
            it.copy(contrasena = valor, contrasenaError = null, loginExitoso = false, errorGeneral = null)
        }
    }

    fun intentarLogin() {
        val estadoActual = _uiState.value
        var hayError = false

        val errorEmail = if (estadoActual.email.isBlank()) {
            hayError = true
            "El email es obligatorio"
        } else if (!emailRegex.matches(estadoActual.email)) {
            hayError = true
            "Formato de email inválido"
        } else null

        val errorContrasena = if (estadoActual.contrasena.isBlank()) {
            hayError = true
            "La contraseña no puede estar vacía"
        } else null

        if (hayError) {
            _uiState.update {
                it.copy(
                    emailError = errorEmail,
                    contrasenaError = errorContrasena,
                    loginExitoso = false
                )
            }
            return
        }

        // Autenticación real contra Room
        viewModelScope.launch {
            val usuario = userRepository.obtenerPorEmail(estadoActual.email.trim().lowercase())

            if (usuario == null) {
                _uiState.update {
                    it.copy(
                        emailError = "No existe un usuario con este email",
                        loginExitoso = false
                    )
                }
                return@launch
            }

            if (!userRepository.verificarContrasena(estadoActual.contrasena, usuario.passwordHash)) {
                _uiState.update {
                    it.copy(
                        contrasenaError = "Contraseña incorrecta",
                        loginExitoso = false
                    )
                }
                return@launch
            }

            // Login exitoso
            _uiState.update {
                it.copy(
                    loginExitoso = true,
                    usuario = usuario,
                    errorGeneral = null
                )
            }
        }
    }

    fun limpiarEstadoExito() {
        _uiState.update { it.copy(loginExitoso = false, usuario = null) }
    }
}