package com.duoc.visionsyncc1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.model.UserEntity
import com.duoc.visionsyncc1.viewmodel.AdminViewModel
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(
    onVolver: () -> Unit,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val usuarios by viewModel.usuariosFlow.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserEntity?>(null) }
    var email by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("TECNOLOGO") }
    var emailError by remember { mutableStateOf("") }
    var nombreError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val roles = listOf("TECNOLOGO", "MEDICO", "PACIENTE", "ADMINISTRADOR")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingUser = null
                    email = ""
                    nombre = ""
                    password = ""
                    rol = "TECNOLOGO"
                    emailError = ""
                    nombreError = ""
                    passwordError = ""
                    showDialog = true
                },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Nuevo usuario") },
                text = { Text("Nuevo Usuario") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (usuarios.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedCard {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("No hay usuarios registrados", style = MaterialTheme.typography.titleMedium)
                            Text("Presiona el botón flotante para agregar uno", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(usuarios, key = { it.id }) { usuario ->
                        UserCard(
                            usuario = usuario,
                            onEdit = {
                                editingUser = usuario
                                email = usuario.email
                                nombre = usuario.nombre
                                password = ""
                                rol = usuario.rol
                                emailError = ""
                                nombreError = ""
                                passwordError = ""
                                showDialog = true
                            },
                            onDelete = {
                                userToDelete = usuario
                                showDeleteConfirmation = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog para crear/editar usuario
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false; editingUser = null },
            title = { Text(if (editingUser != null) "Editar Usuario" else "Nuevo Usuario") },
            text = {
                Column(modifier = Modifier.padding(16.dp).width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; emailError = "" },
                        label = { Text("Email *") },
                        singleLine = true,
                        isError = emailError.isNotBlank(),
                        supportingText = { if (emailError.isNotBlank()) Text(emailError) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it; nombreError = "" },
                        label = { Text("Nombre *") },
                        singleLine = true,
                        isError = nombreError.isNotBlank(),
                        supportingText = { if (nombreError.isNotBlank()) Text(nombreError) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (editingUser == null) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; passwordError = "" },
                            label = { Text("Contraseña *") },
                            visualTransformation = androidx.compose.foundation.text.PasswordVisualTransformation(),
                            singleLine = true,
                            isError = passwordError.isNotBlank(),
                            supportingText = { if (passwordError.isNotBlank()) Text(passwordError) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    // Dropdown para rol
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = rol,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Rol *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            roles.forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r) },
                                    onClick = { rol = r; expanded = false }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    var valid = true
                    if (email.trim().isEmpty()) {
                        emailError = "Email obligatorio"
                        valid = false
                    }
                    if (nombre.trim().isEmpty()) {
                        nombreError = "Nombre obligatorio"
                        valid = false
                    }
                    if (editingUser == null && password.trim().isEmpty()) {
                        passwordError = "Contraseña obligatoria"
                        valid = false
                    }
                    if (!valid) return@TextButton

                    if (editingUser != null) {
                        viewModel.actualizarUsuarioRol(editingUser!!.id, rol)
                        snackbarMessage = "Usuario actualizado"
                    } else {
                        viewModel.crearUsuario(email.trim().lowercase(), password, nombre.trim(), rol)
                        snackbarMessage = "Usuario creado"
                    }
                    showDialog = false
                    editingUser = null
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; editingUser = null }) { Text("Cancelar") }
            }
        )
    }

    // Dialog confirmación eliminar
    if (showDeleteConfirmation && userToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false; userToDelete = null },
            title = { Text("Eliminar Usuario") },
            text = { Text("¿Estás seguro de que deseas eliminar a ${userToDelete!!.nombre} (${userToDelete!!.email})? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarUsuario(userToDelete!!.id)
                    snackbarMessage = "Usuario eliminado"
                    showDeleteConfirmation = false
                    userToDelete = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false; userToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun UserCard(
    usuario: UserEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = usuario.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Badge de rol
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .wrapContentSize()
                            .background(
                                color = when (usuario.rol) {
                                    "ADMINISTRADOR" -> MaterialTheme.colorScheme.errorContainer
                                    "MEDICO" -> MaterialTheme.colorScheme.primaryContainer
                                    "TECNOLOGO" -> MaterialTheme.colorScheme.secondaryContainer
                                    "PACIENTE" -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceContainerHighest
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = usuario.rol,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = when (usuario.rol) {
                                "ADMINISTRADOR" -> MaterialTheme.colorScheme.onErrorContainer
                                "MEDICO" -> MaterialTheme.colorScheme.onPrimaryContainer
                                "TECNOLOGO" -> MaterialTheme.colorScheme.onSecondaryContainer
                                "PACIENTE" -> MaterialTheme.colorScheme.onTertiaryContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
                Text(
                    text = usuario.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

    // Snackbar LaunchedEffect
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            snackbarMessage = null
        }
    }
}