package com.android.hydratame.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.android.hydratame.R
import com.android.hydratame.data.local.Registro

/** Diálogo que pide confirmación antes de borrar un registro. */
@Composable
fun ConfirmarBorradoDialog(
    registro: Registro,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(stringResource(R.string.borrar_titulo)) },
        text = { Text(stringResource(R.string.borrar_texto, registro.ml)) },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text(stringResource(R.string.borrar_confirmar))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text(stringResource(R.string.cancelar))
            }
        }
    )
}
