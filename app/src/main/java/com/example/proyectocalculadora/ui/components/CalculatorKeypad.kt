package com.example.proyectocalculadora.ui.components

import androidx.annotation.ColorRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.proyectocalculadora.R
import com.example.proyectocalculadora.model.Operation
import com.example.proyectocalculadora.viewmodel.CalculatorEvent

// Separación entre botones, tanto horizontal como vertical.
private val KeySpacing = 10.dp

// El teclado tiene 5 filas y 4 columnas de botones.
private const val ROW_COUNT = 5
private const val COLUMN_COUNT = 4

/**
 * Teclado completo de la calculadora. No calcula nada: cada botón solo envía un [CalculatorEvent]
 * al ViewModel a través de [onEvent].
 *
 * Se adapta al espacio disponible. Como los botones son círculos, un teclado muy ancho también
 * sería muy alto; por eso su ancho se limita según el alto que haya, y así cabe tanto en
 * vertical como en horizontal.
 */
@Composable
fun CalculatorKeypad(
    onEvent: (CalculatorEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Ancho máximo que permite el alto disponible: se reparte el alto entre las 5 filas y se
        // multiplica por las 4 columnas (más los espacios entre ellas).
        val widthAllowedByHeight =
            (maxHeight - KeySpacing * (ROW_COUNT - 1)) / ROW_COUNT * COLUMN_COUNT +
                    KeySpacing * (COLUMN_COUNT - 1)
        val keypadWidth = minOf(maxWidth, widthAllowedByHeight)

        Column(
            modifier = Modifier.width(keypadWidth),
            verticalArrangement = Arrangement.spacedBy(KeySpacing)
        ) {
            KeypadRow {
                Key(stringResource(R.string.backspace), R.color.control_button, CalculatorEvent.Backspace, onEvent)
                Key(stringResource(R.string.clear_all), R.color.control_button, CalculatorEvent.Clear, onEvent)
                Key(stringResource(R.string.percent_symbol), R.color.control_button, CalculatorEvent.Percent, onEvent)
                OperatorKey(Operation.Divide, onEvent)
            }
            KeypadRow {
                DigitKey(7, onEvent)
                DigitKey(8, onEvent)
                DigitKey(9, onEvent)
                OperatorKey(Operation.Multiply, onEvent)
            }
            KeypadRow {
                DigitKey(4, onEvent)
                DigitKey(5, onEvent)
                DigitKey(6, onEvent)
                OperatorKey(Operation.Subtract, onEvent)
            }
            KeypadRow {
                DigitKey(1, onEvent)
                DigitKey(2, onEvent)
                DigitKey(3, onEvent)
                OperatorKey(Operation.Add, onEvent)
            }
            KeypadRow {
                Key(stringResource(R.string.toggle_sign), R.color.number_button, CalculatorEvent.ToggleSign, onEvent)
                DigitKey(0, onEvent)
                Key(stringResource(R.string.decimal_separator), R.color.number_button, CalculatorEvent.Decimal, onEvent)
                Key(stringResource(R.string.equals_symbol), R.color.operator_button, CalculatorEvent.Equals, onEvent)
            }
        }
    }
}

/** Una fila del teclado: reparte el ancho en partes iguales entre sus botones. */
@Composable
private fun KeypadRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(KeySpacing),
        content = content
    )
}

/** Botón genérico del teclado: al tocarlo envía [event] al ViewModel. */
@Composable
private fun RowScope.Key(
    label: String,
    @ColorRes containerColorRes: Int,
    event: CalculatorEvent,
    onEvent: (CalculatorEvent) -> Unit
) {
    CalculatorButton(
        label = label,
        containerColor = colorResource(containerColorRes),
        onClick = { onEvent(event) },
        modifier = Modifier.weight(1f)
    )
}

/** Botón de un dígito del 0 al 9. */
@Composable
private fun RowScope.DigitKey(digit: Int, onEvent: (CalculatorEvent) -> Unit) {
    Key(digit.toString(), R.color.number_button, CalculatorEvent.Digit(digit), onEvent)
}

/** Botón naranja de una operación (+, −, ×, ÷); el símbolo sale de strings.xml. */
@Composable
private fun RowScope.OperatorKey(operation: Operation, onEvent: (CalculatorEvent) -> Unit) {
    Key(operation.symbol(), R.color.operator_button, CalculatorEvent.OperationSelected(operation), onEvent)
}