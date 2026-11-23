package com.duoc.visionsyncc1.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
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
import com.duoc.visionsyncc1.model.UserEntity
import com.duoc.visionsyncc1.viewmodel.AdminViewModel
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(
    onVolver: () -> Unit,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val usuarios by viewModel.usuarios.collectAsState()
    var showDialog by mutableStateOf(false)
    var editingUser by mutableStateOf<UserEntity?>(null)
    var email by mutableStateOf("")
    var nombre by mutableStateOf("")
    var rol by mutableStateOf("TECNOLOGO")
    var password by mutableStateOf("")

    val roles = listOf("TECNOLOGO", "MEDICO", "PACIENTE", "ADMINISTRADOR")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        editingUser = null
                        email = ""
                        nombre = ""
                        rol = "TECNOLOGO"
                        password = "123456"
                        showDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar usuario")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (usuarios.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Person,
                    title = "No hay usuarios",
                    message = "Agrega el primer usuario con el botón +"
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(usuarios, key = { it.id }) { usuario ->
                        UserCard(
                            usuario = usuario,
                            onEdit = {
                                editingUser = usuario
                                email = usuario.email
                                nombre = usuario.nombre
                                rol = usuario.rol
                                password = ""
                                showDialog = true
                            },
                            onDelete = {
                                viewModel.eliminarUsuario(usuario.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        UserDialog(
            title = if (editingUser != null) "Editar Usuario" else "Nuevo Usuario",
            email = email,
            onEmailChange = { email = it },
            nombre = nombre,
            onNombreChange = { nombre = it },
            rol = rol,
            onRolChange = { rol = it },
            password = password,
            onPasswordChange = { password = it },
            roles = roles,
            isEditing = editingUser != null,
            onConfirm = {
                if (editingUser != null) {
                    val updated = editingUser!!.copy(
                        email = email,
                        nombre = nombre,
                        rol = rol
                    )
                    viewModel.actualizarUsuario(updated)
                } else {
                    val newUser = UserEntity(
                        email = email,
                        passwordHash = password,
                        nombre = nombre,
                        rol = rol
                    )
                    viewModel.crearUsuario(newUser)
                }
                showDialog = false
                editingUser = null
            },
            onDismiss = {
                showDialog = false
                editingUser = null
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (usuario.rol == "ADMINISTRADOR") Icons.Default.Shield else Icons.Default.Person,
                    contentDescription = "Usuario",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row {
                    Text(
                        text = usuario.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(
                        label = usuario.rol,
                        colors = BadgeDefaults.badgeColors(
                            containerColor = if (usuario.rol == "ADMINISTRADOR")
                                MaterialTheme.colorScheme.errorContainer
                            else if (usuario.rol == "MEDICO")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
                Text(
                    text = usuario.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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

@Composable
private fun UserDialog(
    title: String,
    email: String,
    onEmailChange: (String) -> Unit,
    nombre: String,
    onNombreChange: (String) -> Unit,
    rol: String,
    onRolChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    roles: List<String>,
    isEditing: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isEnabled = !isEditing
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = onNombreChange,
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth()
                ) { expanded ->
                    OutlinedTextField(
                        value = rol,
                        onValueChange = onRolChange,
                        label = { Text("Rol") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        readOnly = true,
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { }
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role) },
                                onClick = { onRolChange(role) }
                            )
                        }
                    }
                }
                if (!isEditing) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.material3.PasswordVisualTransformation()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
            TextButton(onClick = { onConfirm() }) {
                Text(if (isEditing) "Guardar" else "Crear")
            }
        }
    )
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}