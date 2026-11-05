package com.android.hydratame.data.remote

import com.google.gson.annotations.SerializedName

/** Respuesta JSON de Open-Meteo: {"current": {"temperature_2m": 21.4}} */
data class ClimaResponse(
    val current: ClimaActual
)

data class ClimaActual(
    @SerializedName("temperature_2m") val temperatura: Double
)
