package com.android.hydratame.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * Envuelve el contador de pasos del móvil (TYPE_STEP_COUNTER).
 * El sensor devuelve los pasos acumulados desde el último reinicio del teléfono.
 */
class PasosSensor(context: Context) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var alCambiar: ((Float) -> Unit)? = null

    /** false en móviles que no tienen contador de pasos. */
    val disponible: Boolean get() = sensor != null

    fun iniciar(alCambiar: (Float) -> Unit) {
        this.alCambiar = alCambiar
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun detener() {
        sensorManager.unregisterListener(this)
        alCambiar = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        alCambiar?.invoke(event.values[0])
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
