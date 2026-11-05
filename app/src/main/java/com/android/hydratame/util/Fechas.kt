package com.android.hydratame.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Funciones para convertir los milisegundos guardados en Room a texto y comparar días. */
object Fechas {

    private val zona = ZoneId.systemDefault()
    private val formatoHora = DateTimeFormatter.ofPattern("HH:mm")
    private val formatoDia = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val formatoCompleto = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    private fun aFecha(ms: Long) = Instant.ofEpochMilli(ms).atZone(zona)

    fun esHoy(ms: Long): Boolean = aFecha(ms).toLocalDate() == LocalDate.now(zona)

    fun hora(ms: Long): String = aFecha(ms).format(formatoHora)

    fun dia(ms: Long): String = aFecha(ms).format(formatoDia)

    fun diaYHora(ms: Long): String = aFecha(ms).format(formatoCompleto)

    fun minutosDesde(ms: Long): Long = (System.currentTimeMillis() - ms) / 60_000

    /** Texto del día actual (sirve para saber si hay que reiniciar el contador de pasos). */
    fun hoyComoTexto(): String = LocalDate.now(zona).toString()
}
