package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.repository.ExamenRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExamenesViewModel(
    private val repository: ExamenRepository
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _tipoFiltro = MutableStateFlow<String?>(null)
    val tipoFiltro: StateFlow<String?> = _tipoFiltro.asStateFlow()

    val examenesFiltrados: StateFlow<List<ExamenEntity>> =
        combine(repository.observarTodos(), _busqueda, _tipoFiltro) { lista, query, tipo ->
            lista.filter { examen ->
                val coincideTexto = query.isEmpty() ||
                    examen.pacienteNombre.contains(query, ignoreCase = true) ||
                    examen.pacienteRut.contains(query, ignoreCase = true)

                val coincideTipo = tipo == null || examen.tipoExamen.equals(tipo, ignoreCase = true)

                coincideTexto && coincideTipo
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onBusquedaCambiada(nuevaBusqueda: String) {
        _busqueda.value = nuevaBusqueda
    }

    fun onTipoFiltroSeleccionado(tipo: String?) {
        _tipoFiltro.value = if (_tipoFiltro.value == tipo) null else tipo
    }

    fun autorizarExamen(examen: ExamenEntity, observaciones: String, onExito: () -> Unit) {
        viewModelScope.launch {
            val examenActualizado = examen.copy(
                estado = "Autorizado",
                observaciones = observaciones.trim()
            )
            val result = repository.actualizar(examenActualizado)
            result.onSuccess { onExito() }
                .onFailure { e ->
                    // Error handled by repository (sync state updated)
                }
        }
    }
}
