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

    private val _sucursalFiltro = MutableStateFlow<String?>(null)
    val sucursalFiltro: StateFlow<String?> = _sucursalFiltro.asStateFlow()

    private val _estadoFiltro = MutableStateFlow<String?>(null)
    val estadoFiltro: StateFlow<String?> = _estadoFiltro.asStateFlow()

    private val _fechaInicio = MutableStateFlow<String>("")
    val fechaInicio: StateFlow<String> = _fechaInicio.asStateFlow()

    private val _fechaFin = MutableStateFlow<String>("")
    val fechaFin: StateFlow<String> = _fechaFin.asStateFlow()

    val examenesFiltrados: StateFlow<List<ExamenEntity>> =
        combine(
            repository.observarTodos(),
            _busqueda,
            _tipoFiltro,
            _sucursalFiltro,
            _estadoFiltro,
            _fechaInicio,
            _fechaFin
        ) { lista, query, tipo, sucursal, estado, fechaIni, fechaFin ->
            lista.filter { examen ->
                val coincideTexto = query.isEmpty() ||
                    examen.pacienteNombre.contains(query, ignoreCase = true) ||
                    examen.pacienteRut.contains(query, ignoreCase = true)

                val coincideTipo = tipo == null || examen.tipoExamen.equals(tipo, ignoreCase = true)

                val coincideSucursal = sucursal == null || examen.sucursal.equals(sucursal, ignoreCase = true)

                val coincideEstado = estado == null || examen.estado.equals(estado, ignoreCase = true)

                val coincideFecha = if (fechaIni.isNotEmpty() && fechaFin.isNotEmpty()) {
                    val fechaExamen = examen.fecha
                    fechaExamen >= fechaIni && fechaExamen <= fechaFin
                } else true

                coincideTexto && coincideTipo && coincideSucursal && coincideEstado && coincideFecha
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

    fun onSucursalFiltroSeleccionado(sucursal: String?) {
        _sucursalFiltro.value = if (_sucursalFiltro.value == sucursal) null else sucursal
    }

    fun onEstadoFiltroSeleccionado(estado: String?) {
        _estadoFiltro.value = if (_estadoFiltro.value == estado) null else estado
    }

    fun onFechaRangoCambiado(inicio: String, fin: String) {
        _fechaInicio.value = inicio
        _fechaFin.value = fin
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

    // Validar: Pendiente → Validado (Tecnólogo)
    fun validarExamen(examen: ExamenEntity, profesionalId: Int, profesionalNombre: String, onExito: () -> Unit) {
        viewModelScope.launch {
            repository.validarExamen(examen.id, profesionalId, profesionalNombre)
            onExito()
        }
    }

    // Entregar: Validado → Entregado (Médico)
    fun entregarExamen(examen: ExamenEntity, observaciones: String, onExito: () -> Unit) {
        viewModelScope.launch {
            repository.entregarExamen(examen.id, observaciones.trim())
            onExito()
        }
    }

    // Observar: Cualquier → Observado (Médico/Tecnólogo)
    fun observarExamen(examen: ExamenEntity, observaciones: String, onExito: () -> Unit) {
        viewModelScope.launch {
            repository.observarExamen(examen.id, observaciones.trim())
            onExito()
        }
    }

    // Actualizar: Observado → Actualizado (Tecnólogo)
    fun actualizarExamen(examen: ExamenEntity, observaciones: String, onExito: () -> Unit) {
        viewModelScope.launch {
            repository.actualizarExamen(examen.id, observaciones.trim())
            onExito()
        }
    }
}
