package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.repository.ExamenRepository
import kotlinx.coroutines.flow.*

class ExamenesViewModel(private val repo: ExamenRepository) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _filtroTipo = MutableStateFlow<String?>(null)
    val filtroTipo: StateFlow<String?> = _filtroTipo.asStateFlow()

    val examenes: StateFlow<List<ExamenEntity>> =
        combine(repo.observarTodos(), _busqueda, _filtroTipo) { lista, texto, tipo ->
            lista.filter { e ->
                (texto.isBlank() ||
                        e.pacienteNombre.contains(texto, ignoreCase = true) ||
                        e.pacienteRut.contains(texto)) &&
                        (tipo == null || e.tipoExamen == tipo)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onBusquedaChange(texto: String) { _busqueda.value = texto }
    fun onFiltroChange(tipo: String?) { _filtroTipo.value = tipo }
}
