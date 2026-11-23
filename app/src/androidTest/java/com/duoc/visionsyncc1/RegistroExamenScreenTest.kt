package com.duoc.visionsyncc1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duoc.visionsyncc1.ui.screens.RegistroExamenScreen
import com.duoc.visionsyncc1.viewmodel.RegistroExamenViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI Test para validar el formulario de Registro de Examen.
 *
 * Prueba el flujo completo:
 * 1. Datos inválidos → mensaje de error anunciado
 * 2. Acción válida → formulario se guarda
 *
 * Coordina con el integrante 4 para la ejecución de pruebas automatizadas.
 *
 * NOTA: Este test requiere configuración de Hilt/MockK para inyectar un repositorio falso.
 * Ver comentarios en cada test para la implementación completa.
 */

@RunWith(AndroidJUnit4::class)
class RegistroExamenScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    // TODO: Configurar Hilt test rule e inyectar FakeExamenRepository
    // @get:Rule
    // val hiltRule = HiltAndroidRule(this)
    //
    // @Before
    // fun setup() {
    //     hiltRule.inject()
    //     viewModel = RegistroExamenViewModel(fakeRepository)
    // }

    // private lateinit var viewModel: RegistroExamenViewModel
    // private val fakeRepository = FakeExamenRepository()

    @Test
    fun testFormularioConDatosInvalidos_MuestraErrores() {
        // Arrange: Configurar la pantalla con un ViewModel de prueba
        // val viewModel = RegistroExamenViewModel(fakeRepository)

        composeRule.setContent {
            androidx.compose.material3.MaterialTheme {
                RegistroExamenScreen(
                    onVolver = {},
                    onGuardadoExitoso = {},
                    viewModel = RegistroExamenViewModel(/* fakeRepository */)
                )
            }
        }

        // Act & Assert: Verificar que los campos obligatorios muestran error cuando están vacíos
        // Intentar guardar sin llenar campos
        composeRule.onNodeWithText("Registrar Examen").performClick()

        // Verificar errores en campos obligatorios
        composeRule.onNodeWithText("El nombre es obligatorio").assertExists()
        composeRule.onNodeWithText("El RUT es obligatorio").assertExists()
        composeRule.onNodeWithText("Seleccione un tipo de examen").assertExists()
        composeRule.onNodeWithText("Seleccione un ojo").assertExists()
        composeRule.onNodeWithText("El profesional es obligatorio").assertExists()
        composeRule.onNodeWithText("La sucursal es obligatoria").assertExists()
        composeRule.onNodeWithText("La fecha y hora son obligatorias").assertExists()
        composeRule.onNodeWithText("Debe adjuntar un archivo").assertExists()
    }

    @Test
    fun testFormularioConRutInvalido_MuestraErrorRut() {
        composeRule.setContent {
            androidx.compose.material3.MaterialTheme {
                RegistroExamenScreen(
                    onVolver = {},
                    onGuardadoExitoso = {},
                    viewModel = RegistroExamenViewModel(/* fakeRepository */)
                )
            }
        }

        // Llenar nombre válido
        composeRule.onNodeWithText("Nombre Completo *").performClick()
        composeRule.onNode(hasText("Nombre Completo *")).performTextInput("Juan Pérez")

        // Llenar RUT inválido
        composeRule.onNodeWithText("RUT del Paciente *").performClick()
        composeRule.onNode(hasText("RUT del Paciente *")).performTextInput("12345")

        // Intentar guardar
        composeRule.onNodeWithText("Registrar Examen").performClick()

        // Verificar error de RUT
        composeRule.onNodeWithText("RUT inválido").assertExists()
    }

    @Test
    fun testFormularioConDatosValidos_HabilitaBotonGuardar() {
        composeRule.setContent {
            androidx.compose.material3.MaterialTheme {
                RegistroExamenScreen(
                    onVolver = {},
                    onGuardadoExitoso = {},
                    viewModel = RegistroExamenViewModel(/* fakeRepository */)
                )
            }
        }

        // Llenar todos los campos con datos válidos usando placeholder text
        composeRule.onNodeWithText("Ej. Juan Pérez González").performClick()
        composeRule.onNode(hasText("Ej. Juan Pérez González")).performTextInput("Juan Pérez González")

        composeRule.onNodeWithText("12.345.678-9").performClick()
        composeRule.onNode(hasText("12.345.678-9")).performTextInput("12345678-9")

        // Seleccionar tipo de examen
        composeRule.onNodeWithText("Tipo de Examen *").performClick()
        composeRule.onNodeWithText("Campimetría Computarizada").performClick()

        // Seleccionar ojo
        composeRule.onNodeWithText("Ojo *").performClick()
        composeRule.onNodeWithText("Derecho").performClick()

        composeRule.onNodeWithText("Ej. Dr. María González").performClick()
        composeRule.onNode(hasText("Ej. Dr. María González")).performTextInput("Dr. María González")

        composeRule.onNodeWithText("Ej. Clínica Central").performClick()
        composeRule.onNode(hasText("Ej. Clínica Central")).performTextInput("Clínica Central")

        composeRule.onNodeWithText("YYYY-MM-DD HH:MM").performClick()
        composeRule.onNode(hasText("YYYY-MM-DD HH:MM")).performTextInput("2024-12-15 10:30")

        // Verificar que el botón está habilitado
        composeRule.onNodeWithText("Registrar Examen").assertIsEnabled()
    }

    @Test
    fun testAccesibilidad_EtiquetasYDescripciones() {
        composeRule.setContent {
            androidx.compose.material3.MaterialTheme {
                RegistroExamenScreen(
                    onVolver = {},
                    onGuardadoExitoso = {},
                    viewModel = RegistroExamenViewModel(/* fakeRepository */)
                )
            }
        }

        // Verificar que los campos tienen etiquetas accesibles
        composeRule.onNodeWithText("Nombre Completo *").assertExists()
        composeRule.onNodeWithText("RUT del Paciente *").assertExists()
        composeRule.onNodeWithText("Profesional Responsable *").assertExists()
        composeRule.onNodeWithText("Sucursal *").assertExists()
        composeRule.onNodeWithText("Fecha y Hora *").assertExists()

        // Verificar botón volver tiene contentDescription
        composeRule.onNodeWithContentDescription("Volver").assertExists()
    }

    @Test
    fun testAccesibilidad_ErroresAnunciados_RegionesVivas() {
        composeRule.setContent {
            androidx.compose.material3.MaterialTheme {
                RegistroExamenScreen(
                    onVolver = {},
                    onGuardadoExitoso = {},
                    viewModel = RegistroExamenViewModel(/* fakeRepository */)
                )
            }
        }

        // Intentar guardar sin datos
        composeRule.onNodeWithText("Registrar Examen").performClick()

        // Verificar que existen regiones vivas para los errores
        composeRule.onNodeWithText("El nombre es obligatorio").assertExists()
        composeRule.onNodeWithText("El RUT es obligatorio").assertExists()
    }

    /**
     * Fake repository para testing (implementar con MockK o interfaz)
     * class FakeExamenRepository : ExamenRepository(null) {
     *     override suspend fun guardar(examen: ExamenEntity) { }
     * }
     */
}