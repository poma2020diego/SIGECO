package com.sigeco.contactos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sigeco.contactos.ui.screens.FormularioContactoScreen
import com.sigeco.contactos.ui.screens.ListaContactosScreen
import com.sigeco.contactos.ui.viewmodel.ContactoViewModel

object Rutas {
    const val LISTA = "lista"
    const val FORMULARIO = "formulario"
}


@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: ContactoViewModel
) {
    NavHost(navController = navController, startDestination = Rutas.LISTA) {
        composable(Rutas.LISTA) {
            ListaContactosScreen(
                viewModel = viewModel,
                onAgregarClick = { navController.navigate(Rutas.FORMULARIO) },
                onEditarClick = { navController.navigate(Rutas.FORMULARIO) }
            )
        }

        composable(Rutas.FORMULARIO) {
            FormularioContactoScreen(
                viewModel = viewModel,
                onVolverClick = { navController.popBackStack() }
            )
        }
    }
}
