package com.example.proyectocalculadora.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectocalculadora.R
import com.example.proyectocalculadora.model.HistoryEntry

/** Una fila del historial, con el formato "5 + 3 = 8". */
@Composable
fun HistoryItem(
    entry: HistoryEntry,
    modifier: Modifier = Modifier
) {
    // La expresión se arma aquí porque el símbolo de la operación depende de strings.xml.
    val expression = "${entry.firstOperand} ${entry.operation.symbol()} ${entry.secondOperand}"

    Text(
        text = stringResource(R.string.history_entry_format, expression, entry.result),
        fontSize = 28.sp,
        color = colorResource(R.color.primary_text),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}