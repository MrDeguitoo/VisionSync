package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.util.rutValido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class FormularioState(
    val nombre: String = "",
    val rut: String = "",
    val tipoExamen: String = "",
    val archivoUri: String? = null,
    val nombreError: String? = null,
    val rutError: String? = null,
    val tipoError: String? = null,
    val archivoError: String? = null
)

sealed class GuardadoState {
    data object Inactivo : GuardadoState()
    data object  Guardando : GuardadoState()
    data object Exito : GuardadoState()
    data class Error(val mensaje : String) : GuardadoState()
}

class RegistroExamenViewModel : ViewModel() {

    private val _form = MutableStateFlow(FormularioState())
    val form: StateFlow<FormularioState> = _form.asStateFlow()

    private val _guardado = MutableStateFlow<GuardadoState>(GuardadoState.Inactivo)
    val guardado: StateFlow<GuardadoState> = _guardado.asStateFlow()

    fun onNombreChange(valor: String) {
        _form.update { it.copy(nombre = valor, nombreError = null) }
    }

    fun onRutChange(valor: String) {
        _form.update { it.copy(rut = valor, rutError = null) }
    }

    fun onTipoChange(valor: String) {
        _form.update { it.copy(tipoExamen = valor, tipoError = null) }
    }

    fun onArchivoSeleccionado(uri: String?) {
        _form.update { it.copy(archivoUri = uri, archivoError = null) }
    }

    fun guardar() {
        val actual = _form.value
        var valido = true
        val nombreError = if (actual.nombre.trim().length < 3) {
            valido = false
            "El nombre debe tener al menos 3 caracteres"
        } else null

        val rutError = if (!rutValido(actual.rut)) {
            valido = false
            "RUT inválido (ej: 12345678-9)"
        } else null

        val tipoError = if (actual.tipoExamen.isBlank()) {
            valido = false
            "Debe seleccionar un tipo de examen"
        } else null

        val archivoError = if (actual.archivoUri.isNullOrBlank()) {
            valido = false
            "Debe adjuntar un archivo (PDF o Imagen)"
        } else null

        if (!valido) {
            _form.update {
                it.copy(
                    nombreError = nombreError,
                    rutError = rutError,
                    tipoError = tipoError,
                    archivoError = archivoError
                )
            }
            return
        }
        // Simulación temporal mientras B sube Room a main
        viewModelScope.launch {
            _guardado.value = GuardadoState.Guardando
            // TODO: Cuando B termine Room, aquí llamaremos a:
            // repository.guardar(ExamenEntity(...))
            _guardado.value = GuardadoState.Exito
        }
    }

    fun reiniciarEstadoGuardado() {
        _guardado.value = GuardadoState.Inactivo
    }

    fun limpiarFormulario() {
        _form.value = FormularioState()
        _guardado.value = GuardadoState.Inactivo
    }
}