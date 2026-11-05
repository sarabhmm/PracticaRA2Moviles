package com.android.hydratame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.hydratame.ui.screens.editar.EditarScreen
import com.android.hydratame.ui.screens.editar.EditarViewModel
import com.android.hydratame.ui.screens.historial.HistorialScreen
import com.android.hydratame.ui.screens.hoy.HoyScreen

/** Rutas de la app. */
object Rutas {
    const val HOY = "hoy"
    const val HISTORIAL = "historial"
    const val ARG_ID = "id"
    const val EDITAR = "editar/{$ARG_ID}"

    fun editar(id: Int) = "editar/$id"
}

/** Mapa de pantallas de la app. */
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Rutas.HOY,
        modifier = modifier
    ) {
        composable(Rutas.HOY) {
            HoyScreen(onEditar = { id -> navController.navigate(Rutas.editar(id)) })
        }
        composable(Rutas.HISTORIAL) {
            HistorialScreen(onEditar = { id -> navController.navigate(Rutas.editar(id)) })
        }
        composable(
            route = Rutas.EDITAR,
            arguments = listOf(navArgument(Rutas.ARG_ID) { type = NavType.IntType })
        ) { entrada ->
            EditarScreen(
                id = entrada.arguments?.getInt(Rutas.ARG_ID) ?: EditarViewModel.NUEVO,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
