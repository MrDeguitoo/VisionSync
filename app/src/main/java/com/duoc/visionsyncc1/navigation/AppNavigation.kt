package com.duoc.visionsyncc1.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.visionsyncc1.ui.screens.*

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.PERFIL,
        modifier = modifier
    ) {
        // RF1: Pantalla inicial de selección de perfil
        composable(Rutas.PERFIL) {
            PerfilScreen(
                onPerfilTecnologo = { navController.navigate(Rutas.LOGIN) },
                onPerfilMedico = { navController.navigate(Rutas.DASH_MEDICO) },
                onPerfilPaciente = { navController.navigate(Rutas.DASH_PACIENTE) }
            )
        }

        // RF1: Flujo Tecnólogo - Login
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = { navController.navigate(Rutas.DASH_TECNOLOGO) },
                onVolver = { navController.popBackStack() }
            )
        }

        // RF1: Flujo Tecnólogo - Dashboard
        composable(Rutas.DASH_TECNOLOGO) {
            DashTecnologoScreen(
                onNuevoExamen = { navController.navigate(Rutas.REGISTRO_EXAMEN) },
                onCerrarSesion = { navController.popBackStack(Rutas.PERFIL, inclusive = false) }
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

        // Marcadores de posición temporales para B y C (evitan errores de compilación)
        composable(Rutas.DASH_MEDICO) {
            /* Pantalla del Médico (la conectará B) */
        }
        composable(Rutas.DASH_PACIENTE) {
            /* Pantalla del Paciente (la conectará C) */
        }
    }
}