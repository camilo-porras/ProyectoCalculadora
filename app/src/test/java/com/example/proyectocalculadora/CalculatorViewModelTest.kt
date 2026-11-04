package com.example.proyectocalculadora

import com.example.proyectocalculadora.model.CalculatorError
import com.example.proyectocalculadora.model.HistoryEntry
import com.example.proyectocalculadora.model.Operation
import com.example.proyectocalculadora.viewmodel.CalculatorEvent
import com.example.proyectocalculadora.viewmodel.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CalculatorViewModelTest {

    private lateinit var viewModel: CalculatorViewModel

    private val state get() = viewModel.uiState.value

    @Before
    fun setUp() {
        viewModel = CalculatorViewModel()
    }

    private fun type(number: String) {
        number.forEach { character ->
            if (character == '.') {
                viewModel.onEvent(CalculatorEvent.Decimal)
            } else {
                viewModel.onEvent(CalculatorEvent.Digit(character.digitToInt()))
            }
        }
    }

    private fun pressOperation(operation: Operation) {
        viewModel.onEvent(CalculatorEvent.OperationSelected(operation))
    }

    private fun pressEquals() {
        viewModel.onEvent(CalculatorEvent.Equals)
    }

    @Test
    fun addsTwoNumbers() {
        type("5")
        pressOperation(Operation.Add)
        type("3")
        pressEquals()

        assertEquals("8", state.currentInput)
    }

    @Test
    fun repeatedEqualsRepeatsLastOperation() {
        type("5")
        pressOperation(Operation.Add)
        type("3")
        pressEquals()
        pressEquals()

        assertEquals("11", state.currentInput)
    }

    @Test
    fun decimalsAreExact() {
        type("0.1")
        pressOperation(Operation.Add)
        type("0.2")
        pressEquals()

        assertEquals("0.3", state.currentInput)
    }

    @Test
    fun consecutiveOperationsAreEvaluatedLeftToRight() {
        type("5")
        pressOperation(Operation.Add)
        type("3")
        pressOperation(Operation.Multiply)
        type("2")
        pressEquals()

        assertEquals("16", state.currentInput)
    }

    @Test
    fun secondOperatorReplacesTheFirst() {
        type("5")
        pressOperation(Operation.Add)
        pressOperation(Operation.Multiply)
        type("3")
        pressEquals()

        assertEquals("15", state.currentInput)
    }

    @Test
    fun divisionByZeroShowsErrorWithoutCrashing() {
        type("5")
        pressOperation(Operation.Divide)
        type("0")
        pressEquals()

        assertEquals(CalculatorError.DivisionByZero, state.error)
    }

    @Test
    fun typingADigitAfterAnErrorRecovers() {
        type("5")
        pressOperation(Operation.Divide)
        type("0")
        pressEquals()
        type("7")

        assertNull(state.error)
        assertEquals("7", state.currentInput)
    }

    @Test
    fun percentDividesByOneHundred() {
        type("50")
        viewModel.onEvent(CalculatorEvent.Percent)

        assertEquals("0.5", state.currentInput)
    }

    @Test
    fun toggleSignFlipsTheSign() {
        type("5")
        viewModel.onEvent(CalculatorEvent.ToggleSign)
        assertEquals("-5", state.currentInput)

        viewModel.onEvent(CalculatorEvent.ToggleSign)
        assertEquals("5", state.currentInput)
    }

    @Test
    fun backspaceRemovesTheLastDigit() {
        type("123")
        viewModel.onEvent(CalculatorEvent.Backspace)

        assertEquals("12", state.currentInput)
    }

    @Test
    fun inputIsLimitedToTwelveDigits() {
        type("9999999999999")

        assertEquals(12, state.currentInput.length)
    }

    @Test
    fun clearResetsTheCalculatorButKeepsHistory() {
        type("5")
        pressOperation(Operation.Add)
        type("3")
        pressEquals()
        viewModel.onEvent(CalculatorEvent.Clear)

        assertEquals("0", state.currentInput)
        assertEquals(1, state.history.size)
    }

    @Test
    fun historyRecordsOnlyValidOperations() {
        type("5")
        pressOperation(Operation.Add)
        type("3")
        pressEquals()
        type("5")
        pressOperation(Operation.Divide)
        type("0")
        pressEquals()

        assertEquals(
            listOf(HistoryEntry("5", Operation.Add, "3", "8")),
            state.history
        )
    }
}