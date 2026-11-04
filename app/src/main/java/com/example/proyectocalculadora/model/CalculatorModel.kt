package com.example.proyectocalculadora.model

import java.math.BigDecimal
import java.math.MathContext

/**
 * Capa Model: contiene únicamente la lógica de cálculo.
 * No conoce botones, pantallas ni textos, por eso se puede probar de forma aislada.
 *
 * Usa BigDecimal en lugar de Double para que los decimales sean exactos: 0.1 + 0.2 da 0.3.
 */
class CalculatorModel {
    /**
     * Resuelve la operación indicada entre los dos números.
     * Devuelve Success con el valor, o Failure(DivisionByZero) si se divide entre cero.
     */

    fun calculate(
        firstOperand: BigDecimal,
        secondOperand: BigDecimal,
        operation: Operation
    ): CalculationResult {
        val value = when (operation) {
            Operation.Add -> firstOperand + secondOperand
            Operation.Subtract -> firstOperand - secondOperand
            Operation.Multiply -> firstOperand * secondOperand
            Operation.Divide -> {
                // signum() detecta cualquier cero (0, 0.0, 0.00). Comparar con BigDecimal.ZERO no
                // serviría porque BigDecimal también compara la escala.
                if (secondOperand.signum() == 0) {
                    return CalculationResult.Failure(CalculatorError.DivisionByZero)
                }
                // DECIMAL64 limita la división a 16 dígitos, así 1 ÷ 3 no se vuelve infinito.
                firstOperand.divide(secondOperand, MathContext.DECIMAL64)
            }
        }
        // stripTrailingZeros quita los ceros sobrantes: 8.0 queda como 8.
        return CalculationResult.Success(value.stripTrailingZeros())
    }

    /** El porcentaje es el valor dividido entre 100: movePointLeft(2) corre el punto dos lugares. */
    fun toPercentage(value: BigDecimal): BigDecimal = value.movePointLeft(2)

    /** Cambia el signo del número: positivo pasa a negativo y viceversa. */
    fun toggleSign(value: BigDecimal): BigDecimal = value.negate()
}