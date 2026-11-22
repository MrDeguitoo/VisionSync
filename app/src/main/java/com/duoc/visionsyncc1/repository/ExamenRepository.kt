package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.data.ExamenDao
import com.duoc.visionsyncc1.model.ExamenEntity
import kotlinx.coroutines.flow.Flow

class ExamenRepository(private val dao: ExamenDao) {
    suspend fun guardar(examen: ExamenEntity) = dao.insertar(examen)
    fun observarTodos(): Flow<List<ExamenEntity>> = dao.observarTodos()
    fun observarAutorizados(): Flow<List<ExamenEntity>> = dao.observarAutorizados()
    suspend fun obtenerPorId(id: Int): ExamenEntity? = dao.obtenerPorId(id)
    suspend fun actualizarObservaciones(id: Int, texto: String, estado: String) =
        dao.actualizar(id, texto, estado)

    // Nuevas consultas expuestas
    fun observarPorSucursal(sucursal: String): Flow<List<ExamenEntity>> = dao.observarPorSucursal(sucursal)
    fun observarPorEstado(estado: String): Flow<List<ExamenEntity>> = dao.observarPorEstado(estado)
    fun observarPorFechaRango(fechaInicio: String, fechaFin: String): Flow<List<ExamenEntity>> = dao.observarPorFechaRango(fechaInicio, fechaFin)
    fun observarPorProfesional(profesionalId: Int): Flow<List<ExamenEntity>> = dao.observarPorProfesional(profesionalId)

    // Métodos de lógica de negocio para transición de estados
    // Pendiente → Validado
    suspend fun validarExamen(id: Int, profesionalId: Int, profesionalNombre: String) {
        val fechaActual = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        dao.actualizarEstadoCompleto(
            id = id,
            estado = "Validado",
            comentariosRevision = "",
            fechaEntrega = "",
            fechaActualizacion = fechaActual
        )
    }

    // Validado → Entregado (con fechaEntrega = now)
    suspend fun entregarExamen(id: Int, comentariosRevision: String) {
        val fechaActual = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        dao.actualizarEstadoCompleto(
            id = id,
            estado = "Entregado",
            comentariosRevision = comentariosRevision,
            fechaEntrega = fechaActual,
            fechaActualizacion = fechaActual
        )
    }

    // Cualquier → Observado (requiere comentarios)
    suspend fun observarExamen(id: Int, comentariosRevision: String) {
        val fechaActual = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        dao.actualizarEstadoCompleto(
            id = id,
            estado = "Observado",
            comentariosRevision = comentariosRevision,
            fechaEntrega = "",
            fechaActualizacion = fechaActual
        )
    }

    // Observado → Actualizado (con fechaActualizacion = now)
    suspend fun actualizarExamen(id: Int, comentariosRevision: String) {
        val fechaActual = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        dao.actualizarEstadoCompleto(
            id = id,
            estado = "Actualizado",
            comentariosRevision = comentariosRevision,
            fechaEntrega = "",
            fechaActualizacion = fechaActual
        )
    }
}