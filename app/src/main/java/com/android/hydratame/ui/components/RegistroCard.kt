package com.android.hydratame.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.hydratame.R
import com.android.hydratame.data.local.Registro
import com.android.hydratame.util.Fechas

/** Tarjeta de un registro de agua, con botones para editarlo y borrarlo. */
@Composable
fun RegistroCard(
    registro: Registro,
    onEditar: () -> Unit,
    onBorrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${registro.ml} ml",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${registro.tipo} · ${Fechas.hora(registro.fechaHora)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.editar))
            }
            IconButton(onClick = onBorrar) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.borrar))
            }
        }
    }
}
