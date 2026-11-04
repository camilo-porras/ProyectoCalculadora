package com.example.proyectocalculadora.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectocalculadora.R


/**
 * Botón circular genérico de la calculadora.
 * Es un componente "tonto" a propósito: solo recibe qué mostrar, de qué color y qué hacer al tocarlo.
 * @param label Texto que muestra el botón.
 * @param containerColor Color de fondo.
 * @param onClick Acción al tocarlo.
 * @param modifier Quien lo usa define su tamaño; aquí solo se fuerza a que sea cuadrado.
 */
@Composable
fun CalculatorButton(
    label: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = colorResource(R.color.primary_text)
        ),
        // Sin relleno interno, para que textos como "AC" quepan dentro del círculo.
        contentPadding = PaddingValues(0.dp),
        // Cuadrado + CircleShape = círculo perfecto.
        modifier = modifier.aspectRatio(1f)
    ) {
        Text(text = label, fontSize = 28.sp)
    }
}