package com.duoc.visionsyncc1.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Paleta VisionSync (README) ajustada a WCAG 2.2 AA.
 * Contrastes verificados (texto >= 4,5:1; bordes/componentes >= 3:1).
 *
 * Cambios respecto a la paleta original, y por qué:
 *  - Secundario #3F8EFC -> #2B66C9: el original daba 3,2:1 con blanco.
 *  - Éxito #20A39E -> #0B6E6A: el original daba 2,7:1 como texto sobre su fondo tenue.
 */

// ---------- Tema claro ----------
val AzulPrimario = Color(0xFF3A4BBE)          // blanco sobre él: 7,2:1
val AzulPrimarioContenedor = Color(0xFFDDE1FA)
val SobreAzulContenedor = Color(0xFF141A5C)   // 12,2:1
val AzulSecundario = Color(0xFF2B66C9)        // 5,5:1 sobre blanco
val VerdeExito = Color(0xFF0B6E6A)            // 6,1:1 sobre blanco
val RojoError = Color(0xFFB3261E)             // 6,5:1 sobre blanco
val FondoClaro = Color(0xFFF7F4F3)
val SuperficieClara = Color(0xFFFFFFFF)
val TextoPrincipal = Color(0xFF1E3B48)        // 10,8:1 sobre el fondo
val TextoSecundario = Color(0xFF46565F)       // 7,0:1 sobre el fondo
val BordeClaro = Color(0xFF6B7A83)            // 4,0:1 (componentes >= 3:1)

// ---------- Tema oscuro ----------
val AzulPrimarioOscuro = Color(0xFFB4BCFF)
val AzulContenedorOscuro = Color(0xFF2A3796)
val SobreAzulContenedorOscuro = Color(0xFFDDE1FF)
val VerdeExitoOscuro = Color(0xFF5FD3CD)
val RojoErrorOscuro = Color(0xFFFFB4AB)
val FondoOscuro = Color(0xFF10191E)
val SuperficieOscura = Color(0xFF17232A)
val TextoPrincipalOscuro = Color(0xFFE8EEF1)
val TextoSecundarioOscuro = Color(0xFFB8C6CD)
val BordeOscuro = Color(0xFF8A9AA3)