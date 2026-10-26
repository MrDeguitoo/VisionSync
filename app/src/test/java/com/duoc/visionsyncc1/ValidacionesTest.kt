package com.duoc.visionsyncc1

import com.duoc.visionsyncc1.util.rutValido
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {

    @Test
    fun rutValido_conFormatosCorrectos_retornaTrue() {
        assertTrue(rutValido("12.345.678-5"))
        assertTrue(rutValido("12345678-5"))
        assertTrue(rutValido("123456785"))
        assertTrue(rutValido("11.111.111-1"))
    }

    @Test
    fun rutValido_digitoVerificadorErroneo_retornaFalse() {
        // Mismo número pero con DV alterado
        assertFalse(rutValido("12.345.678-0"))
        assertFalse(rutValido("12345678-9"))
    }

    @Test
    fun rutValido_textosInvalidos_retornaFalse() {
        assertFalse(rutValido(""))
        assertFalse(rutValido("abc"))
        assertFalse(rutValido("1-9"))
    }
}