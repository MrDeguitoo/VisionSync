package com.duoc.visionsyncc1.data

import com.duoc.visionsyncc1.model.UserEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(user: UserEntity)

    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun obtenerPorEmail(email: String): UserEntity?

    @Query("SELECT * FROM usuarios")
    fun observarTodos(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    @Query("DELETE FROM usuarios")
    suspend fun eliminarTodos()
}