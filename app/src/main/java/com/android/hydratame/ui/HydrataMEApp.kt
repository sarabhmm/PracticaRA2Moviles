package com.android.hydratame.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.hydratame.R
import com.android.hydratame.ui.navigation.AppNavigation
import com.android.hydratame.ui.navigation.Rutas
import com.android.hydratame.ui.screens.editar.EditarViewModel

private data class Pestana(
    val ruta: String,
    @StringRes val etiqueta: Int,
    val icono: ImageVector
)

private val pestanas = listOf(
    Pestana(Rutas.HOY, R.string.titulo_hoy, Icons.Default.Home),
    Pestana(Rutas.HISTORIAL, R.string.titulo_historial, Icons.AutoMirrored.Filled.List)
)

/** Estructura general de la app: Scaffold con barra superior, barra inferior, botón flotante y navegación. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrataMEApp() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val esPestana = pestanas.any { it.ruta == rutaActual }
    val idEditado = entradaActual?.arguments?.getInt(Rutas.ARG_ID) ?: EditarViewModel.NUEVO

    val titulo = when (rutaActual) {
        Rutas.HISTORIAL -> R.string.titulo_historial
        Rutas.EDITAR -> if (idEditado == EditarViewModel.NUEVO) R.string.titulo_nuevo else R.string.titulo_editar
        else -> R.string.titulo_hoy
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(titulo)) },
                navigationIcon = {
                    if (!esPestana) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.volver)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            if (esPestana) {
                NavigationBar {
                    pestanas.forEach { pestana ->
                        NavigationBarItem(
                            selected = rutaActual == pestana.ruta,
                            onClick = {
                                navController.navigate(pestana.ruta) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(pestana.icono, contentDescription = null) },
                            label = { Text(stringResource(pestana.etiqueta)) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (rutaActual == Rutas.HOY) {
                FloatingActionButton(
                    onClick = { navController.navigate(Rutas.editar(EditarViewModel.NUEVO)) },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.anadir_registro)
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
