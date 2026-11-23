package com.duoc.visionsyncc1.network

import android.content.Context
import android.content.Intent
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Interceptor que agrega el token Bearer a cada petición.
 * Si el servidor responde 401, limpia la sesión y lanza excepción para que la UI navegue al login.
 */
class AuthInterceptor(
    private val tokenStorage: SesionStorage,
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStorage.leer()
        val originalRequest = chain.request()

        val requestBuilder = originalRequest.newBuilder()
        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        // Si el token expiró o es inválido (401), cerrar sesión
        if (response.code == 401) {
            tokenStorage.borrar()
            // Navegar al login - se maneja en la capa de repositorio/ViewModel
            throw UnauthorizedException("Token expirado o inválido")
        }

        return response
    }
}

class UnauthorizedException(message: String) : Exception(message)