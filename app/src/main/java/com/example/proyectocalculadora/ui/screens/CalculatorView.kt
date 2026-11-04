package com.example.proyectocalculadora.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.proyectocalculadora.R
import com.example.proyectocalculadora.model.CalculatorError
import com.example.proyectocalculadora.ui.components.CalculatorButton
import com.example.proyectocalculadora.ui.components.CalculatorDisplay
import com.example.proyectocalculadora.ui.components.CalculatorKeypad
import com.example.proyectocalculadora.ui.components.symbol
import com.example.proyectocalculadora.viewmodel.CalculatorEvent
import com.example.proyectocalculadora.viewmodel.CalculatorUiState
import com.example.proyectocalculadora.viewmodel.CalculatorViewModel

/**
 * Pantalla de la calculadora conectada al ViewModel.
 * Su único trabajo es leer el estado del ViewModel y pasarlo a la parte que dibuja.
 */
@Composable
fun ShowCalculator(
    viewModel: CalculatorViewModel,
    onOpenHistory: () -> Unit
) {
    // collectAsState convierte el StateFlow del ViewModel en un estado de Compose:
    // cuando el ViewModel cambia, la pantalla se redibuja sola.
    val uiState by viewModel.uiState.collectAsState()

    CalculatorContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onOpenHistory = onOpenHistory
    )
}

/**
 * Dibuja la calculadora a partir de un estado. No conoce el ViewModel, por eso se puede
 * previsualizar. Elige el diseño según la orientación de la pantalla.
 */
@Composable
private fun CalculatorContent(
    uiState: CalculatorUiState,
    onEvent: (CalculatorEvent) -> Unit,
    onOpenHistory: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.screen_background))
            .safeDrawingPadding()
            .padding(10.dp)
    ) {
        // Más ancho que alto significa pantalla horizontal.
        if (maxWidth > maxHeight) {
            LandscapeLayout(uiState, onEvent, onOpenHistory)
        } else {
            PortraitLayout(uiState, onEvent, onOpenHistory)
        }
    }
}

/** Diseño vertical: botón de historial, pantalla y teclado, apilados abajo. */
@Composable
private fun PortraitLayout(
    uiState: CalculatorUiState,
    onEvent: (CalculatorEvent) -> Unit,
    onOpenHistory: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        HistoryButton(onClick = onOpenHistory, size = 85.dp)
        DisplayArea(uiState)
        CalculatorKeypad(onEvent = onEvent)
    }
}

/** Diseño horizontal: a la izquierda historial y pantalla, a la derecha el teclado. */
@Composable
private fun LandscapeLayout(
    uiState: CalculatorUiState,
    onEvent: (CalculatorEvent) -> Unit,
    onOpenHistory: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HistoryButton(onClick = onOpenHistory, size = 60.dp)
            DisplayArea(uiState)
        }
        CalculatorKeypad(
            onEvent = onEvent,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

/** Botón azul que abre el historial. */
@Composable
private fun HistoryButton(onClick: () -> Unit, size: Dp) {
    CalculatorButton(
        label = stringResource(R.string.history_button_label),
        containerColor = colorResource(R.color.history_button),
        onClick = onClick,
        modifier = Modifier.size(size)
    )
}

/** Arma el texto de la operación pendiente (por ejemplo "5 +") y muestra la pantalla. */
@Composable
private fun DisplayArea(uiState: CalculatorUiState) {
    val previousOperand = uiState.previousOperand
    val pendingOperation = uiState.pendingOperation
    val expression = if (previousOperand != null && pendingOperation != null) {
        "$previousOperand ${pendingOperation.symbol()}"
    } else {
        ""
    }

    CalculatorDisplay(
        expression = expression,
        result = uiState.currentInput,
        errorMessage = uiState.error?.message()
    )
}

/** Traduce el error del ViewModel al texto que ve el usuario (strings.xml). */
@Composable
private fun CalculatorError.message(): String = stringResource(
    when (this) {
        CalculatorError.DivisionByZero -> R.string.error_division_by_zero
        CalculatorError.InvalidOperation -> R.string.error_invalid_operation
    }
)

@Preview(showBackground = true)
@Composable
private fun CalculatorPortraitPreview() {
    CalculatorContent(
        uiState = CalculatorUiState(),
        onEvent = {},
        onOpenHistory = {}
    )
}

@Preview(showBackground = true, widthDp = 800, heightDp = 360)
@Composable
private fun CalculatorLandscapePreview() {
    CalculatorContent(
        uiState = CalculatorUiState(),
        onEvent = {},
        onOpenHistory = {}
    )
}