package com.example.proyectocalculadora.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectocalculadora.R

/**
 * Pantalla de la calculadora: una línea gris arriba con la operación pendiente (por ejemplo "5 +")
 * y abajo el número grande o, si hay un error, su mensaje.
 * @param expression Texto de la línea de arriba; vacío si no hay operación pendiente.
 * @param result Número que se muestra en grande.
 * @param errorMessage Mensaje de error ya traducido a texto, o null si no hay error.
 */
@Composable
fun CalculatorDisplay(
    expression: String,
    result: String,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
    ) {
        Text(
            text = expression,
            fontSize = 40.sp,
            color = colorResource(R.color.secondary_text),
            textAlign = TextAlign.End,
            softWrap = false,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage != null) {
            // El error reemplaza al número y se permite en dos líneas porque el mensaje es largo.
            Text(
                text = errorMessage,
                fontSize = 24.sp,
                color = colorResource(R.color.error_text),
                textAlign = TextAlign.End,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = result,
                fontSize = resultFontSize(result.length),
                color = colorResource(R.color.primary_text),
                textAlign = TextAlign.End,
                softWrap = false,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Reduce el tamaño de la letra a medida que el número crece, para que siempre quepa en una línea. */
private fun resultFontSize(length: Int): TextUnit = when {
    length <= 7 -> 75.sp
    length <= 9 -> 60.sp
    length <= 11 -> 48.sp
    length <= 14 -> 38.sp
    length <= 18 -> 30.sp
    else -> 22.sp
}