package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.data.ExamenDao
import com.duoc.visionsyncc1.model.ExamenEntity
import kotlinx.coroutines.flow.Flow

open class ExamenRepository(private val dao: ExamenDao) {
    open suspend fun guardar(examen: ExamenEntity) = dao.insertar(examen)
    open fun observarTodos(): Flow<List<ExamenEntity>> = dao.observarTodos()
    open fun observarAutorizados(): Flow<List<ExamenEntity>> = dao.observarAutorizados()
    open suspend fun obtenerPorId(id: Int): ExamenEntity? = dao.obtenerPorId(id)
    open suspend fun actualizarObservaciones(id: Int, texto: String, estado: String) =
        dao.actualizar(id, texto, estado)
}