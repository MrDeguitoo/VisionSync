package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.data.ConsultaDao
import com.duoc.visionsyncc1.model.ConsultaEntity
import kotlinx.coroutines.flow.Flow

class ConsultaRepository(private val dao: ConsultaDao) {
    suspend fun guardar(consulta: ConsultaEntity) = dao.insertar(consulta)

    fun observarPorExamen(examenId: Int): Flow<List<ConsultaEntity>> = dao.observarPorExamen(examenId)

    fun observarTodas(): Flow<List<ConsultaEntity>> = dao.observarTodas()

    suspend fun contarPorExamen(examenId: Int): Int = dao.contarPorExamen(examenId)
}