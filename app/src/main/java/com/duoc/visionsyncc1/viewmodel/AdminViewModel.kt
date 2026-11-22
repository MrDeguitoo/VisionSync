package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.model.ConsultaEntity
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.model.UserEntity
import com.duoc.visionsyncc1.repository.ConsultaRepository
import com.duoc.visionsyncc1.repository.ExamenRepository
import com.duoc.visionsyncc1.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Random

class AdminViewModel(
    private val userRepository: UserRepository,
    private val examenRepository: ExamenRepository,
    private val consultaRepository: ConsultaRepository
) : ViewModel() {

    // ==================== USER MANAGEMENT ====================
    private val _usuarios = MutableStateFlow<List<UserEntity>>(emptyList())
    val usuarios: StateFlow<List<UserEntity>> = _usuarios.asStateFlow()

    // Expose user repository flow
    val usuariosFlow: StateFlow<List<UserEntity>> =
        userRepository.observarTodos().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun cargarUsuarios() {
        viewModelScope.launch {
            _usuarios.value = userRepository.observarTodos().firstOrNull() ?: emptyList()
        }
    }

    fun crearUsuario(email: String, password: String, nombre: String, rol: String) {
        viewModelScope.launch {
            val passwordHash = userRepository.hashearContrasena(password)
            val usuario = UserEntity(
                email = email.trim().lowercase(),
                passwordHash = passwordHash,
                nombre = nombre.trim(),
                rol = rol.uppercase()
            )
            userRepository.guardar(usuario)
            cargarUsuarios()
        }
    }

    fun eliminarUsuario(id: Int) {
        viewModelScope.launch {
            userRepository.eliminarUsuario(id)
            cargarUsuarios()
        }
    }

    fun actualizarUsuarioRol(id: Int, nuevoRol: String) {
        viewModelScope.launch {
            userRepository.actualizarRol(id, nuevoRol.uppercase())
            cargarUsuarios()
        }
    }

    // ==================== CATALOGO EXAMENES ====================
    private val _tiposExamen = MutableStateFlow<List<String>>(
        listOf(
            "Campimetría",
            "OCT",
            "Tonometría",
            "Topografía Corneal",
            "Refracción",
            "Biometría",
            "Paquimetría",
            "Retinografía",
            "Angiografía Fluoresceína",
            "Electrorretinografía"
        )
    )
    val tiposExamen: StateFlow<List<String>> = _tiposExamen.asStateFlow()

    fun agregarTipo(tipo: String) {
        val tipoTrim = tipo.trim()
        if (tipoTrim.isNotEmpty() && !_tiposExamen.value.any { it.equals(tipoTrim, ignoreCase = true) }) {
            _tiposExamen.value = _tiposExamen.value + tipoTrim
        }
    }

    fun eliminarTipo(tipo: String) {
        _tiposExamen.value = _tiposExamen.value.filter { !it.equals(tipo, ignoreCase = true) }
    }

    // ==================== DATOS DE PRUEBA ====================
    private val _generandoDatos = MutableStateFlow(false)
    val generandoDatos: StateFlow<Boolean> = _generandoDatos.asStateFlow()

    private val _mensajeGeneracion = MutableStateFlow<String>("")
    val mensajeGeneracion: StateFlow<String> = _mensajeGeneracion.asStateFlow()

    fun generarDatosPrueba(cantidad: Int) {
        viewModelScope.launch {
            _generandoDatos.value = true
            _mensajeGeneracion.value = "Generando $cantidad exámenes de prueba..."

            val random = Random()
            val nombres = listOf("Juan Pérez", "María González", "Carlos López", "Ana Martínez", "Pedro Rodríguez", "Laura Fernández", "Miguel Sánchez", "Isabel Ramírez")
            val ruts = listOf("12345678-9", "98765432-1", "11223344-5", "55667788-9", "99887766-5", "44332211-0", "77889900-1", "22334455-6")
            val tipos = _tiposExamen.value
            val ojos = listOf("Derecho", "Izquierdo", "Ambos")
            val sucursales = listOf("Sucursal Centro", "Sucursal Norte", "Sucursal Sur", "Sucursal Oriente", "Sucursal Poniente")
            val estados = listOf("Pendiente", "Validado", "Autorizado", "Entregado", "Observado", "Actualizado")
            val profesionales = listOf(
                Pair(1, "José Duarte"),
                Pair(2, "María García")
            )

            val fechaFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            for (i in 1..cantidad) {
                val fecha = fechaFormat.format(Date(System.currentTimeMillis() - random.nextLong(30L * 24 * 60 * 60 * 1000)))
                val examen = ExamenEntity(
                    pacienteNombre = nombres.random(),
                    pacienteRut = ruts.random(),
                    tipoExamen = tipos.random(),
                    fecha = fecha,
                    archivoUri = "content://examenes/prueba_$i.pdf",
                    observaciones = "Examen de prueba generado automáticamente",
                    estado = estados.random(),
                    ojoEvaluado = ojos.random(),
                    sucursal = sucursales.random(),
                    profesionalId = profesionales.random().first,
                    profesionalNombre = profesionales.random().second,
                    comentariosRevision = if (estados.random() != "Pendiente") "Revisado por profesional" else "",
                    fechaEntrega = if (estados.random() == "Entregado") fechaFormat.format(Date()) else null,
                    fechaActualizacion = if (estados.random() != "Pendiente") fechaFormat.format(Date()) else null
                )
                examenRepository.guardar(examen)
            }

            _generandoDatos.value = false
            _mensajeGeneracion.value = "$cantidad exámenes de prueba generados correctamente"
        }
    }

    // ==================== AUDITORIA ====================
    val consultasFlow: StateFlow<List<ConsultaEntity>> =
        consultaRepository.observarTodas().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun consultasPorExamen(examenId: Int): StateFlow<List<ConsultaEntity>> =
        consultaRepository.observarPorExamen(examenId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}