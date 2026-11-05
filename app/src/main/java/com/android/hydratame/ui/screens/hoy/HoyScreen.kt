package com.android.hydratame.ui.screens.hoy

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.hydratame.R
import com.android.hydratame.data.local.Registro
import com.android.hydratame.sensor.PasosSensor
import com.android.hydratame.ui.components.ConfirmarBorradoDialog
import com.android.hydratame.ui.components.RegistroCard
import com.android.hydratame.util.Fechas

@Composable
fun HoyScreen(
    onEditar: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HoyViewModel = viewModel()
) {
    val registros by viewModel.registrosHoy.collectAsStateWithLifecycle()
    val total by viewModel.totalHoy.collectAsStateWithLifecycle()
    val meta by viewModel.metaDiaria.collectAsStateWithLifecycle()
    val pasos by viewModel.pasosHoy.collectAsStateWithLifecycle()
    val clima by viewModel.clima.collectAsStateWithLifecycle()
    var registroABorrar by remember { mutableStateOf<Registro?>(null) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { TarjetaProgreso(total = total, meta = meta, ultimo = registros.firstOrNull()) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { viewModel.anadirVaso(250) },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.boton_250)) }
                OutlinedButton(
                    onClick = { viewModel.anadirVaso(500) },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.boton_500)) }
            }
        }
        item { TarjetaPasos(pasosHoy = pasos, onPasosSensor = viewModel::onPasosSensor) }
        item { TarjetaClima(clima = clima, onReintentar = viewModel::cargarClima) }
        item {
            Text(
                text = stringResource(R.string.registros_hoy),
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (registros.isEmpty()) {
            item { Text(stringResource(R.string.sin_registros_hoy)) }
        }
        items(registros, key = { it.id }) { registro ->
            RegistroCard(
                registro = registro,
                onEditar = { onEditar(registro.id) },
                onBorrar = { registroABorrar = registro }
            )
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

@Composable
private fun TarjetaProgreso(total: Int, meta: Int, ultimo: Registro?) {
    val fraccion = (total.toFloat() / meta).coerceIn(0f, 1f)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { fraccion },
                    modifier = Modifier.size(160.dp),
                    strokeWidth = 12.dp
                )
                Text(
                    text = stringResource(R.string.progreso_ml, total, meta),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (ultimo != null) {
                Text(
                    text = stringResource(
                        R.string.ultimo_registro,
                        Fechas.hora(ultimo.fechaHora),
                        Fechas.minutosDesde(ultimo.fechaHora)
                    )
                )
            }
        }
    }
}

/** Tarjeta del sensor: gestiona el permiso y registra/quita el listener según la pantalla esté visible. */
@Composable
private fun TarjetaPasos(pasosHoy: Int, onPasosSensor: (Float) -> Unit) {
    val context = LocalContext.current
    val sensor = remember { PasosSensor(context) }
    val onPasosActual by rememberUpdatedState(onPasosSensor)

    var permisoConcedido by remember { mutableStateOf(tienePermisoActividad(context)) }
    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> permisoConcedido = concedido }

    // Se activa al entrar en la pantalla (o al conceder el permiso) y se apaga al salir
    DisposableEffect(permisoConcedido) {
        if (permisoConcedido && sensor.disponible) {
            sensor.iniciar { onPasosActual(it) }
        }
        onDispose { sensor.detener() }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.pasos_titulo),
                style = MaterialTheme.typography.titleMedium
            )
            when {
                !permisoConcedido -> {
                    Text(stringResource(R.string.pasos_permiso))
                    Button(onClick = {
                        lanzadorPermiso.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                    }) { Text(stringResource(R.string.pasos_conceder)) }
                }

                !sensor.disponible -> Text(stringResource(R.string.pasos_sin_sensor))

                else -> {
                    Text(
                        text = stringResource(R.string.pasos_valor, pasosHoy),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(stringResource(R.string.pasos_meta_extra))
                }
            }
        }
    }
}

@Composable
private fun TarjetaClima(clima: EstadoClima, onReintentar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.clima_titulo),
                style = MaterialTheme.typography.titleMedium
            )
            when (clima) {
                EstadoClima.Cargando -> Text(stringResource(R.string.clima_cargando))

                is EstadoClima.Exito -> {
                    Text(
                        text = stringResource(R.string.clima_valor, clima.temperatura),
                        style = MaterialTheme.typography.titleLarge
                    )
                    val extra = HoyViewModel.extraPorCalor(clima)
                    if (extra > 0) {
                        Text(
                            text = stringResource(R.string.clima_calor, extra),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                EstadoClima.Error -> {
                    Text(stringResource(R.string.clima_error))
                    OutlinedButton(onClick = onReintentar) {
                        Text(stringResource(R.string.reintentar))
                    }
                }
            }
        }
    }
}

/** Antes de Android 10 el permiso de actividad física no existe, así que se considera concedido. */
private fun tienePermisoActividad(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
