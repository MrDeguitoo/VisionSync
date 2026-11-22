package com.duoc.visionsyncc1.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.duoc.visionsyncc1.model.UserEntity
import com.duoc.visionsyncc1.ui.screens.*
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN,
        modifier = modifier
    ) {
        // Login - Pantalla inicial
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = { user: UserEntity ->
                    when (user.rol) {
                        "TECNOLOGO" -> navController.navigate(Rutas.DASH_TECNOLOGO) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                        "MEDICO" -> navController.navigate(Rutas.DASH_MEDICO) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                        "PACIENTE" -> navController.navigate(Rutas.DASH_PACIENTE) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                        "ADMINISTRADOR" -> navController.navigate(Rutas.ADMIN_DASHBOARD) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                        else -> navController.navigate(Rutas.LOGIN)
                    }
                },
                onVolver = { /* No hay vuelta atrás desde login inicial */ }
            )
        }

        // RF1: Flujo Tecnólogo - Dashboard
        composable(Rutas.DASH_TECNOLOGO) {
            DashTecnologoScreen(
                onNuevoExamen = { navController.navigate(Rutas.REGISTRO_EXAMEN) },
                onCerrarSesion = { navController.navigate(Rutas.LOGIN) { popUpTo(Rutas.LOGIN) { inclusive = true } } }
            )
        }

        // RF2 y RF3: Formulario y selector de archivos
        composable(Rutas.REGISTRO_EXAMEN) {
            RegistroExamenScreen(
                onVolver = { navController.popBackStack() },
                onGuardadoExitoso = { navController.navigate(Rutas.CONFIRMACION) }
            )
        }

        // Confirmación de registro exitoso
        composable(Rutas.CONFIRMACION) {
            ConfirmacionScreen(
                onNuevo = { navController.popBackStack(Rutas.REGISTRO_EXAMEN, inclusive = false) },
                onInicio = { navController.popBackStack(Rutas.DASH_TECNOLOGO, inclusive = false) }
            )
        }

        // Flujo Médico - Dashboard (Buscador)
        composable(Rutas.DASH_MEDICO) {
            BuscadorScreen(
                onExamenSeleccionado = { id ->
                    navController.navigate(Rutas.DETALLE.replace("{id}", id.toString()))
                },
                onVolver = { navController.navigate(Rutas.LOGIN) { popUpTo(Rutas.LOGIN) { inclusive = true } } }
            )
        }

        // Flujo Médico - Detalle de examen
        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            DetalleExamenScreen(
                examenId = id,
                onVolver = { navController.popBackStack() },
                onExamenAutorizado = { navController.popBackStack() }
            )
        }

        // RF6: Portal del Paciente (Integrante C)
        composable(Rutas.DASH_PACIENTE) {
            DashPacienteScreen(
                onVerResumen = { id ->
                    navController.navigate(Rutas.RESUMEN.replace("{id}", id.toString()))
                },
                onCerrarSesion = { navController.navigate(Rutas.LOGIN) { popUpTo(Rutas.LOGIN) { inclusive = true } } }
            )
        }

        // RF6: Resumen de un examen autorizado
        composable(
            route = Rutas.RESUMEN,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            ResumenExamenScreen(
                examenId = id,
                onVolver = { navController.popBackStack() }
            )
        }

        // Admin Dashboard
        composable(Rutas.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onGestionUsuarios = { navController.navigate(Rutas.ADMIN_USUARIOS) },
                onCatalogo = { navController.navigate(Rutas.ADMIN_CATALOGO) },
                onDatosPrueba = { navController.navigate(Rutas.ADMIN_DATOS_PRUEBA) },
                onAuditoria = { navController.navigate(Rutas.ADMIN_AUDITORIA) },
                onCerrarSesion = { navController.navigate(Rutas.LOGIN) { popUpTo(Rutas.LOGIN) { inclusive = true } } }
            )
        }

        // Admin - Gestión de Usuarios
        composable(Rutas.ADMIN_USUARIOS) {
            AdminUsuariosScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        // Admin - Catálogo de Exámenes
        composable(Rutas.ADMIN_CATALOGO) {
            AdminCatalogoScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        // Admin - Generar Datos de Prueba
        composable(Rutas.ADMIN_DATOS_PRUEBA) {
            AdminDatosPruebaScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        // Admin - Auditoría de Consultas
        composable(Rutas.ADMIN_AUDITORIA) {
            AdminAuditoriaScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}