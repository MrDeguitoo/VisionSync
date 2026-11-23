package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.model.ConsultaEntity
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.repository.ConsultaRepository
import com.duoc.visionsyncc1.repository.ExamenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResumenExamenViewModel(
    private val repository: ExamenRepository,
    private val consultaRepository: ConsultaRepository
) : ViewModel() {

    private val _examen = MutableStateFlow<ExamenEntity?>(null)
    val examen: StateFlow<ExamenEntity?> = _examen.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    fun cargar(id: Int, usuarioId: Int, usuarioNombre: String, rol: String) {
        viewModelScope.launch {
            _cargando.value = true
            val encontrado = repository.obtenerPorId(id)
            // Regla de negocio: el paciente jamás ve un examen no autorizado,
            // aunque alguien consulte un id directamente.
            val examenFiltrado = encontrado?.takeIf { it.estado == "Autorizado" }
            _examen.value = examenFiltrado
            _cargando.value = false

            // Log de auditoría: VISUALIZAR
            if (examenFiltrado != null) {
                val fechaConsulta = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                val consulta = ConsultaEntity(
                    examenId = id,
                    usuarioId = usuarioId,
                    usuarioNombre = usuarioNombre,
                    rol = rol,
                    fechaConsulta = fechaConsulta,
                    accion = "VISUALIZAR"
                )
                consultaRepository.guardar(consulta)
            }
        }
    }
}