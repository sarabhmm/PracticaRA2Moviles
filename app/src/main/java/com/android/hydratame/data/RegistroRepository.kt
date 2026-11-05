package com.android.hydratame.data

import com.android.hydratame.data.local.Registro
import com.android.hydratame.data.local.RegistroDao
import com.android.hydratame.data.remote.ClimaApi
import com.android.hydratame.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

/** Une la base de datos (Room) y la API (Retrofit) para que los ViewModel usen un único punto de acceso. */
class RegistroRepository(
    private val dao: RegistroDao,
    private val api: ClimaApi = RetrofitClient.api
) {

    val registros: Flow<List<Registro>> = dao.observarTodos()

    suspend fun obtenerPorId(id: Int): Registro? = dao.obtenerPorId(id)

    suspend fun insertar(registro: Registro) = dao.insertar(registro)

    suspend fun actualizar(registro: Registro) = dao.actualizar(registro)

    suspend fun borrar(registro: Registro) = dao.borrar(registro)

    /** Temperatura actual en grados Celsius (coordenadas fijas de Madrid). */
    suspend fun obtenerTemperatura(): Double =
        api.obtenerClima(LATITUD, LONGITUD).current.temperatura

    private companion object {
        const val LATITUD = 40.42
        const val LONGITUD = -3.70
    }
}
