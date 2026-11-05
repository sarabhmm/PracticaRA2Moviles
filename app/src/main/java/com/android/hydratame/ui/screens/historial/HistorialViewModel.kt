package com.android.hydratame.ui.screens.historial

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.hydratame.data.RegistroRepository
import com.android.hydratame.data.local.AppDatabase
import com.android.hydratame.data.local.Registro
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        RegistroRepository(AppDatabase.getInstance(application).registroDao())

    val registros: StateFlow<List<Registro>> = repositorio.registros
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun borrar(registro: Registro) {
        viewModelScope.launch { repositorio.borrar(registro) }
    }
}
