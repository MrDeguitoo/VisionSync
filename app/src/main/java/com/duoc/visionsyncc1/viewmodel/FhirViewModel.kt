package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.repository.FhirPacienteUi
import com.duoc.visionsyncc1.repository.FhirRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FhirViewModel(
    private val repository: FhirRepository
) : ViewModel() {

    private val _pacientes = MutableStateFlow<List<FhirPacienteUi>>(emptyList())
    val pacientes: StateFlow<List<FhirPacienteUi>> = _pacientes.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarPacientes(cantidad: Int = 10) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            val result = repository.obtenerPacientes(cantidad)
            result.onSuccess { lista ->
                _pacientes.value = lista
                _cargando.value = false
            }.onFailure { e ->
                _error.value = "Error al cargar pacientes FHIR: ${e.message}"
                _cargando.value = false
            }
        }
    }
}