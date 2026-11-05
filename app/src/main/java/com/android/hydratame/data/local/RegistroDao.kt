package com.android.hydratame.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    // CREATE
    @Insert
    suspend fun insertar(registro: Registro)

    // READ: el Flow avisa a la pantalla cada vez que cambia la tabla
    @Query("SELECT * FROM registros ORDER BY fechaHora DESC")
    fun observarTodos(): Flow<List<Registro>>

    @Query("SELECT * FROM registros WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Registro?

    // UPDATE
    @Update
    suspend fun actualizar(registro: Registro)

    // DELETE
    @Delete
    suspend fun borrar(registro: Registro)
}
