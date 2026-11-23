package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.repository.ExamenRepository
import com.duoc.visionsyncc1.util.rutValido
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioState(
    val nombre: String = "",
    val rut: String = "",
    val tipoExamen: String = "",
    val ojo: String = "",
    val profesional: String = "",
    val sucursal: String = "",
    val fecha: String = "",
    val archivoUri: String? = null,
    val nombreError: String? = null,
    val rutError: String? = null,
    val tipoError: String? = null,
    val ojoError: String? = null,
    val profesionalError: String? = null,
    val sucursalError: String? = null,
    val fechaError: String? = null,
    val archivoError: String? = null
)

sealed class GuardadoState {
    data object Inactivo : GuardadoState()
    data object Guardando : GuardadoState()
    data object Exito : GuardadoState()
    data class Error(val mensaje: String) : GuardadoState()
}

class RegistroExamenViewModel(
    private val repository: ExamenRepository
) : ViewModel() {

    // Constructor para testing - usa un repositorio fake si no se proporciona
    constructor() : this(FakeExamenRepository())

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

    fun onOjoChange(valor: String) {
        _form.update { it.copy(ojo = valor, ojoError = null) }
    }

    fun onProfesionalChange(valor: String) {
        _form.update { it.copy(profesional = valor, profesionalError = null) }
    }

    fun onSucursalChange(valor: String) {
        _form.update { it.copy(sucursal = valor, sucursalError = null) }
    }

    fun onFechaChange(valor: String) {
        _form.update { it.copy(fecha = valor, fechaError = null) }
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

        val ojoError = if (actual.ojo.isBlank()) {
            valido = false
            "Debe seleccionar un ojo"
        } else null

        val profesionalError = if (actual.profesional.trim().isEmpty()) {
            valido = false
            "Debe ingresar el profesional responsable"
        } else null

        val sucursalError = if (actual.sucursal.trim().isEmpty()) {
            valido = false
            "Debe ingresar la sucursal"
        } else null

        val fechaError = if (actual.fecha.trim().isEmpty()) {
            valido = false
            "Debe ingresar la fecha y hora"
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
                    ojoError = ojoError,
                    profesionalError = profesionalError,
                    sucursalError = sucursalError,
                    fechaError = fechaError,
                    archivoError = archivoError
                )
            }
            return
        }

        viewModelScope.launch {
            _guardado.value = GuardadoState.Guardando
            try {
                val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

                val examen = ExamenEntity(
                    pacienteNombre = actual.nombre.trim(),
                    pacienteRut = actual.rut.trim(),
                    tipoExamen = actual.tipoExamen,
                    ojo = actual.ojo,
                    profesional = actual.profesional.trim(),
                    sucursal = actual.sucursal.trim(),
                    fecha = if (actual.fecha.trim().isEmpty()) fechaActual else actual.fecha.trim(),
                    archivoUri = actual.archivoUri,
                    observaciones = "",
                    estado = "Pendiente"
                )

                val result = repository.guardar(examen)
                // Compatibilidad: si devuelve Result, usar onSuccess/onFailure; si no, try/catch
                if (result is kotlin.Result) {
                    result.onSuccess { _guardado.value = GuardadoState.Exito }
                        .onFailure { e ->
                            _guardado.value = GuardadoState.Error("Error al guardar: ${e.message}")
                        }
                } else {
                    _guardado.value = GuardadoState.Exito
                }
            } catch (e: Exception) {
                _guardado.value = GuardadoState.Error(e.message ?: "Error al guardar examen")
            }
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

/**
 * Repositorio fake para testing - no requiere Room ni DAO
 */
class FakeExamenRepository : ExamenRepository(FakeExamenDao()) {
    override suspend fun guardar(examen: ExamenEntity) {
        // No-op para testing
    }
}

/**
 * DAO fake para testing
 */
class FakeExamenDao : com.duoc.visionsyncc1.data.ExamenDao {
    override suspend fun insertar(examen: ExamenEntity) { }
    override fun observarTodos(): kotlinx.coroutines.flow.Flow<List<ExamenEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
    override fun observarAutorizados(): kotlinx.coroutines.flow.Flow<List<ExamenEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
    override suspend fun obtenerPorId(id: Int) = null
    override suspend fun actualizar(id: Int, texto: String, estado: String) { }
}