package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.repository.ExamenRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PacienteViewModel(
    private val repository: ExamenRepository
) : ViewModel() {

    // Regla de negocio: el paciente solo ve exámenes en estado 'Entregado'
    val examenesEntregados: StateFlow<List<ExamenEntity>> =
        repository.observarPorEstado("Entregado").stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}