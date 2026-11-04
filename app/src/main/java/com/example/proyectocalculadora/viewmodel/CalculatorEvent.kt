package com.example.proyectocalculadora.viewmodel

import com.example.proyectocalculadora.model.Operation

/**
 * Todo lo que el usuario puede hacer en la calculadora.
 * La interfaz solo envía estos eventos al ViewModel; quien decide qué significan es el ViewModel.
 */
sealed interface CalculatorEvent {

    /** Se presionó un dígito del 0 al 9. */
    data class Digit(val value: Int) : CalculatorEvent

    /** Se eligió una operación (+, −, ×, ÷). */
    data class OperationSelected(val operation: Operation) : CalculatorEvent

    /** Se presionó el punto decimal. */
    data object Decimal : CalculatorEvent

    /** Se presionó el botón =. */
    data object Equals : CalculatorEvent

    /** Se presionó AC: limpia la calculadora pero conserva el historial. */
    data object Clear : CalculatorEvent

    /** Se presionó +/−: cambia el signo del número en pantalla. */
    data object ToggleSign : CalculatorEvent

    /** Se presionó %: divide el número en pantalla entre 100. */
    data object Percent : CalculatorEvent

    /** Se presionó ≪: borra el último dígito escrito. */
    data object Backspace : CalculatorEvent
}