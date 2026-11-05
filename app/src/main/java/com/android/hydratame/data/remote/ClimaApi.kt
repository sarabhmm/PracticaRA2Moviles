package com.android.hydratame.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaApi {

    @GET("v1/forecast")
    suspend fun obtenerClima(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") actual: String = "temperature_2m"
    ): ClimaResponse
}
