package com.example.proyectocalculadora.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectocalculadora.R
import com.example.proyectocalculadora.model.HistoryEntry
import com.example.proyectocalculadora.model.Operation
import com.example.proyectocalculadora.ui.components.HistoryItem
import com.example.proyectocalculadora.viewmodel.CalculatorViewModel

/**
 * Pantalla del historial conectada al ViewModel.
 * Recibe el mismo ViewModel que la calculadora, por eso ve las mismas operaciones.
 */
@Composable
fun ShowHistory(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HistoryContent(
        history = uiState.history,
        onBack = onBack
    )
}

/** Dibuja el título, el botón de volver y la lista (o el mensaje de lista vacía). */
@Composable
private fun HistoryContent(
    history: List<HistoryEntry>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.screen_background))
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text(
                text = stringResource(R.string.back_description),
                color = colorResource(R.color.operator_button),
                fontSize = 18.sp
            )
        }
        Text(
            text = stringResource(R.string.history_title),
            color = colorResource(R.color.primary_text),
            fontSize = 40.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if (history.isEmpty()) {
            EmptyHistoryMessage()
        } else {
            LazyColumn {
                // LazyColumn solo dibuja las filas visibles y permite desplazarse si la lista es larga.
                items(history) { entry ->
                    HistoryItem(entry = entry)
                }
            }
        }
    }
}

/** Mensaje centrado que se muestra mientras no hay operaciones. */
@Composable
private fun EmptyHistoryMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.history_empty),
            color = colorResource(R.color.secondary_text),
            fontSize = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryContentPreview() {
    HistoryContent(
        history = listOf(
            HistoryEntry("5", Operation.Add, "3", "8"),
            HistoryEntry("8", Operation.Multiply, "2", "16"),
            HistoryEntry("1", Operation.Divide, "3", "0.3333333333333333")
        ),
        onBack = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun EmptyHistoryContentPreview() {
    HistoryContent(history = emptyList(), onBack = {})
}