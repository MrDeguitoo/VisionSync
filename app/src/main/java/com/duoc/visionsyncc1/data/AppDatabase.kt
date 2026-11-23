package com.duoc.visionsyncc1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.duoc.visionsyncc1.model.ConsultaEntity
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.model.PacienteEntity
import com.duoc.visionsyncc1.model.UserEntity

@Database(entities = [ExamenEntity::class, PacienteEntity::class, UserEntity::class, ConsultaEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun examenDao(): ExamenDao
    abstract fun pacienteDao(): PacienteDao
    abstract fun userDao(): UserDao
    abstract fun consultaDao(): ConsultaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "visionsync.db"
                ).fallbackToDestructiveMigration() // Para desarrollo, migración simple (los datos se vuelven a bajar del servidor)
                    .build().also { INSTANCE = it }
            }
    }
}