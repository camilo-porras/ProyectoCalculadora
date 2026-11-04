package com.example.proyectocalculadora.viewmodel

import com.example.proyectocalculadora.model.CalculatorError
import com.example.proyectocalculadora.model.HistoryEntry
import com.example.proyectocalculadora.model.Operation

/**
 * Estado completo de lo que muestra la pantalla. El ViewModel lo publica y la interfaz lo dibuja.
 *
 * @property currentInput Número grande en pantalla: lo que se está escribiendo o el último resultado.
 * @property previousOperand Primer número de la operación pendiente. Junto con [pendingOperation]
 * forma la línea gris de arriba, por ejemplo "5 +".
 * @property pendingOperation Operación elegida que todavía espera su segundo número.
 * @property error Error actual, o null si no hay. La interfaz lo traduce a un texto.
 * @property history Operaciones completadas, en el orden en que se hicieron.
 */
data class CalculatorUiState(
    val currentInput: String = INITIAL_INPUT,
    val previousOperand: String? = null,
    val pendingOperation: Operation? = null,
    val error: CalculatorError? = null,
    val history: List<HistoryEntry> = emptyList()
) {
    companion object {
        /** Valor que se muestra al iniciar o después de limpiar. */
        const val INITIAL_INPUT = "0"
    }
}