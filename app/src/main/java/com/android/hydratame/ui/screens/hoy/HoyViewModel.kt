package com.android.hydratame.ui.screens.hoy

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.hydratame.data.RegistroRepository
import com.android.hydratame.data.local.AppDatabase
import com.android.hydratame.data.local.Registro
import com.android.hydratame.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estados posibles de la petición de temperatura. */
sealed interface EstadoClima {
    data object Cargando : EstadoClima
    data class Exito(val temperatura: Double) : EstadoClima
    data object Error : EstadoClima
}

class HoyViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        RegistroRepository(AppDatabase.getInstance(application).registroDao())
    private val preferencias =
        application.getSharedPreferences("pasos", Context.MODE_PRIVATE)

    /** Registros del día actual (se recalcula solo cuando cambia la base de datos). */
    val registrosHoy: StateFlow<List<Registro>> = repositorio.registros
        .map { lista -> lista.filter { Fechas.esHoy(it.fechaHora) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalHoy: StateFlow<Int> = registrosHoy
        .map { lista -> lista.sumOf { it.ml } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _pasosHoy = MutableStateFlow(0)
    val pasosHoy: StateFlow<Int> = _pasosHoy.asStateFlow()

    private val _clima = MutableStateFlow<EstadoClima>(EstadoClima.Cargando)
    val clima: StateFlow<EstadoClima> = _clima.asStateFlow()

    /** La meta sube con los pasos y con el calor. */
    val metaDiaria: StateFlow<Int> = combine(_pasosHoy, _clima) { pasos, clima ->
        META_BASE + (pasos / PASOS_POR_TRAMO) * ML_POR_TRAMO + extraPorCalor(clima)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), META_BASE)

    init {
        cargarClima()
    }

    fun anadirVaso(ml: Int) {
        viewModelScope.launch {
            repositorio.insertar(
                Registro(ml = ml, fechaHora = System.currentTimeMillis(), tipo = "Vaso")
            )
        }
    }

    fun borrar(registro: Registro) {
        viewModelScope.launch { repositorio.borrar(registro) }
    }

    fun cargarClima() {
        viewModelScope.launch {
            _clima.value = EstadoClima.Cargando
            _clima.value = try {
                EstadoClima.Exito(repositorio.obtenerTemperatura())
            } catch (e: Exception) {
                EstadoClima.Error
            }
        }
    }

    /**
     * El sensor da los pasos acumulados desde que se encendió el móvil.
     * Guardamos el primer valor de cada día como punto de partida y restamos.
     */
    fun onPasosSensor(totalSensor: Float) {
        val total = totalSensor.toLong()
        val hoy = Fechas.hoyComoTexto()
        var base = preferencias.getLong(CLAVE_BASE, -1L)
        val fechaBase = preferencias.getString(CLAVE_FECHA, null)

        // Día nuevo o móvil reiniciado: empezamos a contar desde el valor actual
        if (fechaBase != hoy || base < 0 || total < base) {
            base = total
            preferencias.edit()
                .putString(CLAVE_FECHA, hoy)
                .putLong(CLAVE_BASE, base)
                .apply()
        }
        _pasosHoy.value = (total - base).toInt()
    }

    companion object {
        const val META_BASE = 2000
        const val ML_POR_TRAMO = 250
        const val PASOS_POR_TRAMO = 3000
        const val UMBRAL_CALOR = 28.0
        const val EXTRA_CALOR = 300

        private const val CLAVE_BASE = "pasos_base"
        private const val CLAVE_FECHA = "fecha_base"

        /** Mililitros extra de la meta cuando la temperatura supera el umbral. */
        fun extraPorCalor(clima: EstadoClima): Int =
            if (clima is EstadoClima.Exito && clima.temperatura >= UMBRAL_CALOR) EXTRA_CALOR else 0
    }
}
