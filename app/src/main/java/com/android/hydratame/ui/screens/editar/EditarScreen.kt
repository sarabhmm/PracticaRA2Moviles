package com.android.hydratame.ui.screens.editar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.hydratame.R
import com.android.hydratame.data.local.TIPOS_REGISTRO
import com.android.hydratame.util.Fechas

@Composable
fun EditarScreen(
    id: Int,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditarViewModel = viewModel()
) {
    LaunchedEffect(id) { viewModel.cargar(id) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = viewModel.ml,
            onValueChange = viewModel::cambiarMl,
            label = { Text(stringResource(R.string.campo_ml)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = viewModel.errorMl,
            supportingText = if (viewModel.errorMl) {
                { Text(stringResource(R.string.error_ml)) }
            } else {
                null
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TIPOS_REGISTRO.forEach { opcion ->
                FilterChip(
                    selected = viewModel.tipo == opcion,
                    onClick = { viewModel.cambiarTipo(opcion) },
                    label = { Text(opcion) }
                )
            }
        }

        viewModel.fechaHora?.let { fecha ->
            Text(stringResource(R.string.fecha_registro, Fechas.diaYHora(fecha)))
        }

        Button(
            onClick = { viewModel.guardar(onVolver) },
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.guardar)) }
    }
}
