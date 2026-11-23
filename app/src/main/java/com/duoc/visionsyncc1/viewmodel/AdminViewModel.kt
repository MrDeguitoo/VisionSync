package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.model.UserEntity
import com.duoc.visionsyncc1.repository.ConsultaRepository
import com.duoc.visionsyncc1.repository.ExamenRepository
import com.duoc.visionsyncc1.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

data class AdminDashboardStats(
    val totalExamenes: Int = 0,
    val pendientes: Int = 0,
    val revisados: Int = 0,
    val autorizados: Int = 0,
    val observados: Int = 0,
    val totalUsuarios: Int = 0,
    val totalConsultas: Int = 0
)

class AdminViewModel(
    private val examenRepository: ExamenRepository,
    private val userRepository: UserRepository,
    private val consultaRepository: ConsultaRepository
) : ViewModel() {

    // Dashboard stats
    private val _stats = MutableStateFlow(AdminDashboardStats())
    val stats: Flow<AdminDashboardStats> = _stats.asStateFlow()

    // Usuarios
    private val _usuarios = MutableStateFlow<List<UserEntity>>(emptyList())
    val usuarios: Flow<List<UserEntity>> = _usuarios.asStateFlow()

    // Auditoría
    private val _auditoria = MutableStateFlow<List<com.duoc.visionsyncc1.model.ConsultaEntity>>(emptyList())
    val auditoria: Flow<List<com.duoc.visionsyncc1.model.ConsultaEntity>> = _auditoria.asStateFlow()

    // Cargando
    private val _cargando = MutableStateFlow(false)
    val cargando: Flow<Boolean> = _cargando.asStateFlow()

    init {
        cargarDashboardStats()
        cargarUsuarios()
        cargarAuditoria()
    }

    fun cargarDashboardStats() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val totalExamenes = examenRepository.contarTotal()
                val pendientes = examenRepository.contarPendientesEstado()
                val revisados = examenRepository.contarRevisados()
                val autorizados = examenRepository.contarAutorizados()
                val observados = examenRepository.contarObservados()
                val totalUsuarios = userRepository.contarUsuarios()
                val totalConsultas = consultaRepository.contarTotal()

                _stats.value = AdminDashboardStats(
                    totalExamenes = totalExamenes,
                    pendientes = pendientes,
                    revisados = revisados,
                    autorizados = autorizados,
                    observados = observados,
                    totalUsuarios = totalUsuarios,
                    totalConsultas = totalConsultas
                )
            } catch (e: Exception) {
                // Manejar error silenciosamente
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarUsuarios() {
        viewModelScope.launch {
            val lista = userRepository.obtenerTodos()
            _usuarios.value = lista
        }
    }

    fun cargarAuditoria(limit: Int = 100) {
        viewModelScope.launch {
            val lista = consultaRepository.obtenerUltimas(limit).first()
            _auditoria.value = lista
        }
    }

    suspend fun crearUsuario(usuario: UserEntity): Long = userRepository.guardar(usuario)

    suspend fun actualizarUsuario(usuario: UserEntity) = userRepository.actualizar(usuario)

    suspend fun eliminarUsuario(id: Int) = userRepository.eliminar(id)

    // Sucursales y profesionales para catálogo
    val sucursales: Flow<List<String>> = examenRepository.obtenerSucursales().distinctUntilChanged()
    val profesionales: Flow<List<String>> = examenRepository.obtenerProfesionales().distinctUntilChanged()
}