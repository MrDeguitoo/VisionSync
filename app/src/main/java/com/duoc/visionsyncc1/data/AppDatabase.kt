package com.duoc.visionsyncc1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.model.UserEntity

@Database(entities = [ExamenEntity::class, UserEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun examenDao(): ExamenDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "visionsync.db"
                ).fallbackToDestructiveMigration() // Para desarrollo, migración simple
                    .build().also { INSTANCE = it }
            }
    }
}
