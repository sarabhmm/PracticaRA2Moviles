package com.android.hydratame.ui.screens.editar

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.hydratame.data.RegistroRepository
import com.android.hydratame.data.local.AppDatabase
import com.android.hydratame.data.local.Registro
import kotlinx.coroutines.launch

/** Sirve para crear un registro nuevo (id = -1) o para editar uno existente. */
class EditarViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        RegistroRepository(AppDatabase.getInstance(application).registroDao())

    var ml by mutableStateOf("250")
        private set
    var tipo by mutableStateOf("Vaso")
        private set
    var errorMl by mutableStateOf(false)
        private set
    var fechaHora by mutableStateOf<Long?>(null)
        private set

    private var registroActual: Registro? = null
    private var cargado = false

    fun cargar(id: Int) {
        if (cargado || id == NUEVO) return
        cargado = true
        viewModelScope.launch {
            repositorio.obtenerPorId(id)?.let { registro ->
                registroActual = registro
                ml = registro.ml.toString()
                tipo = registro.tipo
                fechaHora = registro.fechaHora
            }
        }
    }

    fun cambiarMl(nuevo: String) {
        ml = nuevo.filter { it.isDigit() }
        errorMl = false
    }

    fun cambiarTipo(nuevo: String) {
        tipo = nuevo
    }

    fun guardar(onListo: () -> Unit) {
        val cantidad = ml.toIntOrNull()
        if (cantidad == null || cantidad <= 0) {
            errorMl = true
            return
        }
        viewModelScope.launch {
            val actual = registroActual
            if (actual == null) {
                repositorio.insertar(
                    Registro(ml = cantidad, fechaHora = System.currentTimeMillis(), tipo = tipo)
                )
            } else {
                repositorio.actualizar(actual.copy(ml = cantidad, tipo = tipo))
            }
            onListo()
        }
    }

    companion object {
        const val NUEVO = -1
    }
}
