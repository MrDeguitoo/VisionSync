package com.duoc.visionsyncc1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val passwordHash: String,
    val nombre: String,
    val rol: String // "TECNOLOGO", "MEDICO", "PACIENTE"
)