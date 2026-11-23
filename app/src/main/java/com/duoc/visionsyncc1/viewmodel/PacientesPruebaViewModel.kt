package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Modelo de datos para un paciente de prueba de la API externa
 */
data class PacientePrueba(
    val id: Int,
    val nombre: String,
    val rut: String,
    val email: String
)

data class PacientesPruebaUIState(
    val pacientes: List<PacientePrueba> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class PacientesPruebaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PacientesPruebaUIState())
    val uiState: StateFlow<PacientesPruebaUIState> = _uiState.asStateFlow()

    fun cargarPacientes() {
        _uiState.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {
            try {
                // Simular llamada a API externa
                // En producción, aquí se haría la llamada real a Retrofit/integrante 2
                val pacientesSimulados = simularApiExterna()

                _uiState.update {
                    it.copy(pacientes = pacientesSimulados, cargando = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(cargando = false, error = "Error al cargar: ${e.message}")
                }
            }
        }
    }

    /**
     * Simula una respuesta de API externa con pacientes de prueba
     * En producción, esto sería reemplazado por una llamada real a Retrofit
     */
    private suspend fun simularApiExterna(): List<PacientePrueba> {
        // Simular latencia de red
        kotlinx.coroutines.delay(800)

        return listOf(
            PacientePrueba(1, "Ana María Silva", "15.234.567-8", "ana.silva@ejemplo.cl"),
            PacientePrueba(2, "Carlos Andrés Rojas", "18.987.654-3", "carlos.rojas@ejemplo.cl"),
            PacientePrueba(3, "Patricia Elena Torres", "12.456.789-0", "patricia.torres@ejemplo.cl"),
            PacientePrueba(4, "Roberto Javier Mendoza", "20.123.456-7", "roberto.mendoza@ejemplo.cl"),
            PacientePrueba(5, "Lucía Fernanda Vargas", "16.789.012-5", "lucia.vargas@ejemplo.cl"),
            PacientePrueba(6, "Fernando José Herrera", "19.345.678-9", "fernando.herrera@ejemplo.cl"),
            PacientePrueba(7, "Sofía Alejandra Núñez", "14.567.890-1", "sofia.nunez@ejemplo.cl"),
            PacientePrueba(8, "Diego Armando Paredes", "17.890.123-4", "diego.paredes@ejemplo.cl"),
            PacientePrueba(9, "Valentina Paz Cortés", "13.678.901-2", "valentina.cortes@ejemplo.cl"),
            PacientePrueba(10, "Andrés Felipe Muñoz", "21.012.345-6", "andres.munoz@ejemplo.cl")
        )
    }
}