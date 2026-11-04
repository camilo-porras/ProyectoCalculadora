package com.example.proyectocalculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyectocalculadora.navigation.AppNavigation
import com.example.proyectocalculadora.ui.theme.ProyectoCalculadoraTheme

/** Punto de entrada de la app: aplica el tema y muestra la navegación. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La app dibuja detrás de las barras del sistema; cada pantalla las respeta con safeDrawingPadding.
        enableEdgeToEdge()
        setContent {
            ProyectoCalculadoraTheme {
                AppNavigation()
            }
        }
    }
}