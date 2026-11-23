package com.duoc.visionsyncc1.network

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface VisionSyncApi {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("examenes")
    suspend fun examenes(
        @Query("texto") texto: String? = null,
        @Query("tipo") tipo: String? = null,
        @Query("estado") estado: String? = null
    ): List<ExamenDto>

    @GET("examenes/{id}")
    suspend fun examen(@Path("id") id: Long): ExamenDto

    @POST("examenes")
    suspend fun crearExamen(@Body e: ExamenDto): ExamenDto

    @PUT("examenes/{id}")
    suspend fun actualizarExamen(@Path("id") id: Long, @Body e: ExamenDto): ExamenDto

    @DELETE("examenes/{id}")
    suspend fun eliminarExamen(@Path("id") id: Long)

    @Multipart
    @POST("examenes/{id}/documento")
    suspend fun subirDocumento(
        @Path("id") id: Long,
        @Part archivo: MultipartBody.Part
    ): DocumentoDto

    @Streaming
    @GET("documentos/{id}/archivo")
    suspend fun descargarDocumento(@Path("id") id: Long): ResponseBody

    // Pacientes
    @GET("pacientes")
    suspend fun pacientes(): List<PacienteDto>

    @GET("pacientes/{id}")
    suspend fun paciente(@Path("id") id: Long): PacienteDto
}

data class PacienteDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("rut") val rut: String,
    @SerializedName("email") val email: String,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String?
)