package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.repository.ExamenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResumenExamenViewModel(
    private val repository: ExamenRepository
) : ViewModel() {

    private val _examen = MutableStateFlow<ExamenEntity?>(null)
    val examen: StateFlow<ExamenEntity?> = _examen.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    fun cargar(id: Int) {
        viewModelScope.launch {
            _cargando.value = true
            val encontrado = repository.obtenerPorId(id)
            // Regla de negocio: el paciente jamás ve un examen no autorizado,
            // aunque alguien consulte un id directamente.
            _examen.value = encontrado?.takeIf { it.estado == "Autorizado" }
            _cargando.value = false
        }
    }
}