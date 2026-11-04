package com.example.proyectocalculadora.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectocalculadora.ui.screens.ShowCalculator
import com.example.proyectocalculadora.ui.screens.ShowHistory
import com.example.proyectocalculadora.viewmodel.CalculatorViewModel

/** Nombres de las dos pantallas de la app. Centralizados para no repetir textos sueltos. */
private object Routes {
    const val CALCULATOR = "calculator"
    const val HISTORY = "history"
}

/** Define qué pantallas existen y cómo se navega entre ellas. */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // El ViewModel se crea aquí, fuera del NavHost, para que las dos pantallas compartan el mismo.
    // Si cada pantalla creara el suyo, el historial siempre se vería vacío.
    val calculatorViewModel: CalculatorViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.CALCULATOR
    ) {
        composable(Routes.CALCULATOR) {
            ShowCalculator(
                viewModel = calculatorViewModel,
                onOpenHistory = { navController.navigate(Routes.HISTORY) }
            )
        }
        composable(Routes.HISTORY) {
            ShowHistory(
                viewModel = calculatorViewModel,
                // popBackStack regresa a la calculadora sin crear una pantalla nueva.
                onBack = { navController.popBackStack() }
            )
        }
    }
}