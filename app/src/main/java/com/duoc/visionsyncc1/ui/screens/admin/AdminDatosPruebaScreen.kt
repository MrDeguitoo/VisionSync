package com.duoc.visionsyncc1.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.model.UserEntity
import com.duoc.visionsyncc1.repository.ExamenRepository
import com.duoc.visionsyncc1.repository.UserRepository
import com.duoc.visionsyncc1.viewmodel.AdminViewModel
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDatosPruebaScreen(
    onVolver: () -> Unit,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var cantidad by mutableStateOf("20")
    var generando by mutableStateOf(false)
    var mensaje by mutableStateOf<String?>(null)
    var tipoError by mutableStateOf(false)

    val examRepository = viewModel::class.java.getDeclaredField("examenRepository").let { f ->
        f.isAccessible = true
        f.get(viewModel) as ExamenRepository
    }
    val userRepository = viewModel::class.java.getDeclaredField("userRepository").let { f ->
        f.isAccessible = true
        f.get(viewModel) as UserRepository
    }

    val tiposExamen = listOf(
        "Campimetría", "OCT", "Retinografía", "Topografía Corneal",
        "Biometría", "Paquimetría", "Aberrometría", "Tomografía de Coherencia Óptica"
    )
    val estados = listOf("Pendiente", "Revisado", "Autorizado")
    val sucursales = listOf("Sucursal Centro", "Sucursal Norte", "Sucursal Sur", "Sucursal Oriente")
    val profesionales = listOf("Dr. José Duarte", "Dra. María García", "Téc. Pedro Pérez", "Dr. Carlos Ruiz")
    val ruts = listOf(
        "12345678-9", "98765432-1", "11223344-5", "55667788-0",
        "99887766-4", "44332211-7", "66778899-3", "33445566-8"
    )
    val nombres = listOf(
        "Juan Pérez", "María González", "Carlos Rodríguez", "Ana Martínez",
        "Luis Sánchez", "Laura Fernández", "Jorge López", "Patricia Torres"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Datos de Prueba") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Tarjeta informativa
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Generador de Datos Sintéticos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Crea exámenes y usuarios de prueba para testing. Los datos son aleatorios pero realistas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Configuración",
                        style = MaterialTheme.typography.titleSmall
                    )
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text("Cantidad de exámenes a generar") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        visualTransformation = androidx.compose.ui.text.input.VisualTransformation { text ->
                            androidx.compose.ui.text.AnnotatedString(text.text.filter { it.isDigit() })
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { generarDatosPrueba(cantidad.toIntOrNull() ?: 20) },
                            enabled = !generando
                        ) {
                            if (generando) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    androidx.compose.material3.CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generando...")
                                }
                            } else {
                                Text("Generar Datos")
                            }
                        }
                        OutlinedButton(
                            onClick = { limpiarDatosPrueba() },
                            enabled = !generando,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Limpiar Todo")
                        }
                    }
                }
            }

            // Mensaje de resultado
            mensaje?.let { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (tipoError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (tipoError) Icons.Default.Delete else Icons.Default.Upload,
                            contentDescription = "",
                            tint = if (tipoError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (tipoError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

private fun generarDatosPrueba(
    cantidad: Int = 20,
    examRepository: ExamenRepository,
    userRepository: UserRepository
) {
    val scope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO)
    scope.launch {
        try {
            // Obtener usuarios existentes
            val usuarios = userRepository.obtenerTodos()
            if (usuarios.isEmpty()) {
                throw IllegalStateException("No hay usuarios en el sistema. Crea usuarios primero.")
            }

            val random = Random()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sdfFull = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            repeat(cantidad) {
                val usuario = usuarios.random()
                val tipoExamen = tiposExamen.random()
                val estado = estados.random()
                val sucursal = sucursales.random()
                val profesional = profesionales.random()
                val rut = ruts.random()
                val nombre = nombres.random()
                val fecha = Date(System.currentTimeMillis() - random.nextLong(30L * 24 * 60 * 60 * 1000)) // últimos 30 días

                val examen = ExamenEntity(
                    pacienteNombre = nombre,
                    pacienteRut = rut,
                    tipoExamen = tipoExamen,
                    fecha = sdfFull.format(fecha),
                    archivoUri = null,
                    observaciones = "Examen de prueba generado automáticamente",
                    estado = estado,
                    ojoEvaluado = listOf("OD", "OI", "AMBOS").random(),
                    sucursal = sucursal,
                    profesional = profesional,
                    fechaRealizacion = sdf.format(fecha),
                    fechaValidacion = if (estado != "Pendiente") sdf.format(fecha) else "",
                    estadoExtendido = when (estado) {
                        "Pendiente" -> "PENDIENTE_VALIDACION"
                        "Revisado" -> "VALIDADO"
                        "Autorizado" -> "ENTREGADO"
                        else -> ""
                    },
                    comentarioValidacion = if (estado != "Pendiente") "Validado correctamente" else "",
                    comentarioEntrega = if (estado == "Autorizado") "Entregado al paciente" else ""
                )

                examRepository.guardar(examen)
            }

            kotlinx.coroutines.withContext(Dispatchers.Main) {
                mensaje = "Se generaron $cantidad exámenes de prueba exitosamente"
                tipoError = false
            }
        } catch (e: Exception) {
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                mensaje = "Error: ${e.message}"
                tipoError = true
            }
        }
    }
}

private fun limpiarDatosPrueba(
    examRepository: ExamenRepository
) {
    val scope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO)
    scope.launch {
        try {
            val examenes = examRepository.obtenerTodos().first()
            examenes.forEach { examenRepository.eliminar(examen) }
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                mensaje = "Todos los datos de prueba han sido eliminados"
                tipoError = false
            }
        } catch (e: Exception) {
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                mensaje = "Error al limpiar: ${e.message}"
                tipoError = true
            }
        }
    }
}