package com.example.proyectocalculadora.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectocalculadora.model.CalculationResult
import com.example.proyectocalculadora.model.CalculatorError
import com.example.proyectocalculadora.model.CalculatorModel
import com.example.proyectocalculadora.model.HistoryEntry
import com.example.proyectocalculadora.model.Operation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

/**
 * Capa ViewModel: recibe los eventos de la interfaz, decide qué hacer, usa el Model para calcular
 * y publica el resultado como un estado observable.
 * No usa recursos (stringResource, colorResource) ni Context: solo datos y errores semánticos.
 * Comportamiento definido:
 * - Las operaciones consecutivas se resuelven de izquierda a derecha.
 * - Dos operadores seguidos: el segundo reemplaza al primero.
 * - "=" repetido repite la última operación.
 * - AC limpia todo menos el historial.
 */
class CalculatorViewModel : ViewModel() {

    private val model = CalculatorModel()

    // Estado mutable privado; hacia afuera solo se expone la versión de solo lectura.
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    // true cuando el próximo dígito debe empezar un número nuevo (después de un operador o de "=").
    private var startsNewNumber = false

    // Última operación y segundo número, guardados para repetirlos al presionar "=" otra vez.
    private var repeatOperation: Operation? = null
    private var repeatOperand: BigDecimal? = null

    private val hasError: Boolean
        get() = _uiState.value.error != null

    private val currentInput: String
        get() = _uiState.value.currentInput

    /** Único punto de entrada: la interfaz envía aquí cada acción del usuario. */
    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.Digit -> enterDigit(event.value)
            is CalculatorEvent.OperationSelected -> selectOperation(event.operation)
            CalculatorEvent.Decimal -> enterDecimal()
            CalculatorEvent.Equals -> calculateResult()
            CalculatorEvent.Clear -> clearAll()
            CalculatorEvent.ToggleSign -> toggleSign()
            CalculatorEvent.Percent -> applyPercent()
            CalculatorEvent.Backspace -> deleteLastCharacter()
        }
    }

    /** Agrega un dígito al número en pantalla, respetando el límite de dígitos. */
    private fun enterDigit(digit: Int) {
        beginInputIfNeeded()
        val input = currentInput
        if (input.count { it.isDigit() } >= MAX_INPUT_DIGITS) return
        val updatedInput = if (input == CalculatorUiState.INITIAL_INPUT) digit.toString() else input + digit
        updateInput(updatedInput)
    }

    /** Agrega el punto decimal, solo si el número todavía no tiene uno. */
    private fun enterDecimal() {
        beginInputIfNeeded()
        if (DECIMAL_POINT in currentInput) return
        updateInput(currentInput + DECIMAL_POINT)
    }

    /** Guarda la operación elegida; si ya había una pendiente con su segundo número, la resuelve primero. */
    private fun selectOperation(operation: Operation) {
        if (hasError) return
        val state = _uiState.value
        val pendingOperation = state.pendingOperation

        // Operador presionado dos veces seguidas: solo se cambia por el nuevo.
        if (pendingOperation != null && startsNewNumber) {
            _uiState.update { it.copy(pendingOperation = operation) }
            return
        }

        val currentValue = parseOrFail(currentInput) ?: return
        val firstOperand = if (pendingOperation == null) {
            currentValue
        } else {
            // Operación encadenada: primero se resuelve la pendiente (5 + 3 × ... calcula 8).
            val previousValue = parseOrFail(state.previousOperand) ?: return
            compute(previousValue, currentValue, pendingOperation) ?: return
        }

        repeatOperation = null
        repeatOperand = null
        startsNewNumber = true
        _uiState.update {
            it.copy(
                currentInput = firstOperand.toDisplayString(),
                previousOperand = firstOperand.toDisplayString(),
                pendingOperation = operation
            )
        }
    }

    /** Resuelve la operación pendiente; si no hay ninguna, repite la última (= repetido). */
    private fun calculateResult() {
        if (hasError) return
        val pendingOperation = _uiState.value.pendingOperation

        if (pendingOperation != null) {
            val firstOperand = parseOrFail(_uiState.value.previousOperand) ?: return
            // Si no se escribió un segundo número (5 + =), se usa el que está en pantalla.
            val secondOperand = parseOrFail(currentInput) ?: return
            val result = compute(firstOperand, secondOperand, pendingOperation) ?: return
            repeatOperation = pendingOperation
            repeatOperand = secondOperand
            showResult(result)
        } else {
            val operation = repeatOperation ?: return
            val secondOperand = repeatOperand ?: return
            val firstOperand = parseOrFail(currentInput) ?: return
            val result = compute(firstOperand, secondOperand, operation) ?: return
            showResult(result)
        }
    }

    /** Convierte el número en pantalla a su porcentaje (lo divide entre 100). */
    private fun applyPercent() {
        if (hasError) return
        val value = parseOrFail(currentInput) ?: return
        updateInput(model.toPercentage(value).toDisplayString())
    }

    /** Cambia el signo del número en pantalla. */
    private fun toggleSign() {
        if (hasError) return
        val value = parseOrFail(currentInput) ?: return
        // Si el usuario acaba de escribir el punto ("5."), se conserva para poder seguir escribiendo.
        val keepsDecimalPoint = currentInput.endsWith(DECIMAL_POINT)
        val negated = model.toggleSign(value).toPlainString()
        updateInput(if (keepsDecimalPoint) negated + DECIMAL_POINT else negated)
    }

    /** Borra el último carácter; si no queda nada, vuelve a "0". No actúa sobre resultados. */
    private fun deleteLastCharacter() {
        if (hasError || startsNewNumber) return
        val shortened = currentInput.dropLast(1)
        val isEmptyNumber = shortened.isEmpty() || shortened == NEGATIVE_SIGN
        updateInput(if (isEmptyNumber) CalculatorUiState.INITIAL_INPUT else shortened)
    }

    /** AC: reinicia la calculadora, pero conserva el historial. */
    private fun clearAll() {
        startsNewNumber = false
        repeatOperation = null
        repeatOperand = null
        _uiState.update { CalculatorUiState(history = it.history) }
    }

    /**
     * Prepara la pantalla antes de escribir: si había un error lo limpia, y si el número anterior
     * ya estaba terminado (resultado u operador), empieza uno nuevo desde "0".
     */
    private fun beginInputIfNeeded() {
        if (hasError) clearAll()
        if (!startsNewNumber) return

        // Escribir un número después de "=" inicia un cálculo nuevo, así que se olvida lo repetible.
        if (_uiState.value.pendingOperation == null) {
            repeatOperation = null
            repeatOperand = null
        }
        startsNewNumber = false
        updateInput(CalculatorUiState.INITIAL_INPUT)
    }

    /**
     * Pide el cálculo al Model. Si sale bien, registra la operación en el historial y devuelve el
     * valor; si falla, muestra el error y devuelve null.
     */
    private fun compute(first: BigDecimal, second: BigDecimal, operation: Operation): BigDecimal? {
        return when (val result = model.calculate(first, second, operation)) {
            is CalculationResult.Success -> {
                addToHistory(first, operation, second, result.value)
                result.value
            }

            is CalculationResult.Failure -> {
                showError(result.error)
                null
            }
        }
    }

    /** Convierte el texto en número. Si no es válido, muestra un error en lugar de cerrar la app. */
    private fun parseOrFail(text: String?): BigDecimal? {
        val value = text?.toBigDecimalOrNull()
        if (value == null) showError(CalculatorError.InvalidOperation)
        return value
    }

    /** Deja el resultado en pantalla y limpia la operación pendiente. */
    private fun showResult(result: BigDecimal) {
        startsNewNumber = true
        _uiState.update {
            it.copy(
                currentInput = result.toDisplayString(),
                previousOperand = null,
                pendingOperation = null
            )
        }
    }

    /** Publica el error en el estado y deja la calculadora lista para recuperarse. */
    private fun showError(error: CalculatorError) {
        repeatOperation = null
        repeatOperand = null
        _uiState.update {
            it.copy(
                currentInput = CalculatorUiState.INITIAL_INPUT,
                previousOperand = null,
                pendingOperation = null,
                error = error
            )
        }
    }

    /** Agrega una operación válida al final del historial, conservando el orden. */
    private fun addToHistory(first: BigDecimal, operation: Operation, second: BigDecimal, result: BigDecimal) {
        val entry = HistoryEntry(
            firstOperand = first.toDisplayString(),
            operation = operation,
            secondOperand = second.toDisplayString(),
            result = result.toDisplayString()
        )
        _uiState.update { it.copy(history = it.history + entry) }
    }

    private fun updateInput(newInput: String) {
        _uiState.update { it.copy(currentInput = newInput) }
    }

    /** Texto limpio de un número: sin ceros sobrantes ni notación científica (8.0 → "8"). */
    private fun BigDecimal.toDisplayString(): String = stripTrailingZeros().toPlainString()

    private companion object {
        const val DECIMAL_POINT = "."
        const val NEGATIVE_SIGN = "-"

        // Máximo de dígitos por número para que se mantenga legible en pantalla.
        const val MAX_INPUT_DIGITS = 12
    }
}