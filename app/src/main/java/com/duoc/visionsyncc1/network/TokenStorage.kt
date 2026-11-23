package com.duoc.visionsyncc1.network

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Interfaz para almacenamiento de sesión - permite testing con MockK
 */
interface SesionStorage {
    fun guardar(token: String)
    fun leer(): String?
    fun borrar()
}

/**
 * Almacenamiento de token cifrado con Android Keystore (AES/GCM)
 *
 * Por qué Keystore:
 * - La clave maestra nunca sale del dispositivo (hardware-backed si disponible)
 * - Protección contra extracción incluso con root en muchos dispositivos
 * - Rotación de claves gestionada por el sistema
 *
 * Por qué AES/GCM:
 * - Cifrado autenticado (confidencialidad + integridad)
 * - IV único por cifrado (evita ataques de replay)
 * - Estándar moderno, sin padding oracle attacks
 */
class TokenStorage(private val context: Context) : SesionStorage {

    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
    private val alias = "visionsync_token_key"

    private fun clave(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getKey(alias, null) as? SecretKey)?.let { return it }

        val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        gen.init(
            KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return gen.generateKey()
    }

    override fun guardar(token: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, clave())
        val datos = cipher.iv + cipher.doFinal(token.toByteArray())
        prefs.edit().putString("token", Base64.encodeToString(datos, Base64.NO_WRAP)).apply()
    }

    override fun leer(): String? {
        val raw = prefs.getString("token", null) ?: return null
        return try {
            val b = Base64.decode(raw, Base64.NO_WRAP)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            // IV son los primeros 12 bytes (96 bits) para GCM
            cipher.init(Cipher.DECRYPT_MODE, clave(), GCMParameterSpec(128, b.copyOfRange(0, 12)))
            String(cipher.doFinal(b.copyOfRange(12, b.size)))
        } catch (e: Exception) {
            // Si falla el descifrado (corrupción, cambio de dispositivo, etc.), borramos y devolvemos null
            borrar()
            null
        }
    }

    override fun borrar() {
        prefs.edit().remove("token").apply()
    }
}