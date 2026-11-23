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

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerTodos(): List<UserEntity>

    @Query("UPDATE usuarios SET email = :email, passwordHash = :passwordHash, nombre = :nombre, rol = :rol WHERE id = :id")
    suspend fun actualizar(id: Int, email: String, passwordHash: String, nombre: String, rol: String)

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminar(id: Int)
}