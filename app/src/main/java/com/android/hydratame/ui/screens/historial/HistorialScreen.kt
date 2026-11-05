package com.android.hydratame.ui.screens.historial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.hydratame.R
import com.android.hydratame.data.local.Registro
import com.android.hydratame.ui.components.ConfirmarBorradoDialog
import com.android.hydratame.ui.components.RegistroCard
import com.android.hydratame.util.Fechas

@Composable
fun HistorialScreen(
    onEditar: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistorialViewModel = viewModel()
) {
    val registros by viewModel.registros.collectAsStateWithLifecycle()
    var registroABorrar by remember { mutableStateOf<Registro?>(null) }

    if (registros.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.sin_historial))
        }
    } else {
        // Agrupamos por día; cada día lleva su cabecera con el total bebido
        val porDia = registros.groupBy { Fechas.dia(it.fechaHora) }

        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            porDia.forEach { (dia, lista) ->
                item(key = dia) {
                    Text(
                        text = stringResource(R.string.total_dia, dia, lista.sumOf { it.ml }),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(lista, key = { it.id }) { registro ->
                    RegistroCard(
                        registro = registro,
                        onEditar = { onEditar(registro.id) },
                        onBorrar = { registroABorrar = registro }
                    )
                }
            }
        }
    }

    registroABorrar?.let { registro ->
        ConfirmarBorradoDialog(
            registro = registro,
            onConfirmar = {
                viewModel.borrar(registro)
                registroABorrar = null
            },
            onCancelar = { registroABorrar = null }
        )
    }
}
