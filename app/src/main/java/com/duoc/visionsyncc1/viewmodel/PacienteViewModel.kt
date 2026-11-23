package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.model.PacienteEntity
import com.duoc.visionsyncc1.repository.ExamenRepository
import com.duoc.visionsyncc1.repository.PacienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.duoc.visionsyncc1.util.rutValido

data class PacienteUIState(
    val nombre: String = "",
    val rut: String = "",
    val busqueda: String = "",
    val nombreError: String? = null,
    val rutError: String? = null
)

class PacienteViewModel(
    private val examenRepository: ExamenRepository,
    private val pacienteRepository: PacienteRepository
) : ViewModel() {

    // Regla de negocio: el paciente solo ve exámenes en estado 'Entregado'
    val examenesEntregados: StateFlow<List<ExamenEntity>> =
        repository.observarPorEstado("Entregado").stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Lista de pacientes para búsqueda
    val pacientes: StateFlow<List<PacienteEntity>> =
        combine(pacienteRepository.observarTodos(), _uiState.map { it.busqueda }) { pacientes, query ->
            if (query.isBlank()) {
                pacientes
            } else {
                val q = query.lowercase()
                pacientes.filter { p ->
                    p.nombre.lowercase().contains(q) || p.rut.contains(q)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onNombreChange(valor: String) {
        _uiState.update { it.copy(nombre = valor, nombreError = null) }
    }

    fun onRutChange(valor: String) {
        _uiState.update { it.copy(rut = valor, rutError = null) }
    }

    fun onBusquedaChange(valor: String) {
        _uiState.update { it.copy(busqueda = valor) }
    }

    fun registrarPaciente() {
        val actual = _uiState.value
        var valido = true

        val nombreError = if (actual.nombre.trim().length < 3) {
            valido = false
            "El nombre debe tener al menos 3 caracteres"
        } else null

        val rutError = if (!rutValido(actual.rut)) {
            valido = false
            "RUT inválido (ej: 12345678-9)"
        } else null

        if (!valido) {
            _uiState.update {
                it.copy(nombreError = nombreError, rutError = rutError)
            }
            return
        }

        viewModelScope.launch {
            val paciente = PacienteEntity(
                nombre = actual.nombre.trim(),
                rut = actual.rut.trim()
            )
            pacienteRepository.insertar(paciente)
            // Limpiar formulario tras éxito
            _uiState.update {
                it.copy(nombre = "", rut = "", nombreError = null, rutError = null)
            }
        }
    }
}