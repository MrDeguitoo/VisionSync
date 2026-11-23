package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.data.ConsultaDao
import com.duoc.visionsyncc1.model.ConsultaEntity
import kotlinx.coroutines.flow.Flow

class ConsultaRepository(private val dao: ConsultaDao) {

    suspend fun registrar(consulta: ConsultaEntity): Long = dao.insertar(consulta)

    fun observarPorExamen(examenId: Int): Flow<List<ConsultaEntity>> = dao.observarPorExamen(examenId)

    fun observarPorUsuario(usuarioId: Int): Flow<List<ConsultaEntity>> = dao.observarPorUsuario(usuarioId)

    fun observarUltimas(limit: Int = 50): Flow<List<ConsultaEntity>> = dao.observarUltimas(limit)

    suspend fun contarTotal(): Int = dao.contarTotal()
}