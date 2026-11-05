package com.android.hydratame.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Tipos de recipiente que se pueden elegir al registrar agua. */
val TIPOS_REGISTRO = listOf("Vaso", "Botella", "Taza")

/** Tabla "registros": cada fila es una vez que el usuario ha bebido agua. */
@Entity(tableName = "registros")
data class Registro(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ml: Int,
    /** Momento del registro en milisegundos (así no hace falta TypeConverter). */
    val fechaHora: Long,
    val tipo: String
)
