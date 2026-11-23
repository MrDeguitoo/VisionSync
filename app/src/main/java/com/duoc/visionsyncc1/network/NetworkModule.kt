package com.duoc.visionsyncc1.network

import android.content.Context
import com.duoc.visionsyncc1.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Módulo de configuración de red.
 * Centraliza la creación de Retrofit, OkHttpClient y las APIs.
 */
object NetworkModule {

    private const val FHIR_BASE_URL = "https://hapi.fhir.org/baseR4/"

    /**
     * Cliente HTTP para la API propia (con interceptor de auth y logging)
     */
    fun provideOkHttpClient(context: Context, tokenStorage: SesionStorage): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStorage, context))
            .addInterceptor(logging)
            .build()
    }

    /**
     * Cliente HTTP para API externa FHIR (SIN interceptor de auth)
     */
    fun provideFhirOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    /**
     * Gson configurado para serialización
     */
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    /**
     * Retrofit para la API propia de VisionSync
     */
    fun provideVisionSyncApi(context: Context, tokenStorage: SesionStorage): VisionSyncApi {
        val client = provideOkHttpClient(context, tokenStorage)
        val gson = provideGson()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(VisionSyncApi::class.java)
    }

    /**
     * Retrofit para la API externa HAPI FHIR
     */
    fun provideFhirApi(): FhirApi {
        val client = provideFhirOkHttpClient()
        val gson = provideGson()

        return Retrofit.Builder()
            .baseUrl(FHIR_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FhirApi::class.java)
    }
}

/**
 * API externa HAPI FHIR - solo lectura, sin autenticación
 */
interface FhirApi {
    @GET("Patient")
    suspend fun pacientes(@Query("_count") cantidad: Int = 10): FhirBundle
}