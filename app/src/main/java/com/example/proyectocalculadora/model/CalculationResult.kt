package com.example.proyectocalculadora.model

import java.math.BigDecimal

/**
 * Resultado de un cálculo: o salió bien (Success) o falló con un motivo (Failure).
 * Así los errores viajan como valores claros y no como excepciones ni como números raros (Infinity).
 */
sealed interface CalculationResult {
    /** El cálculo se resolvió y trae el valor obtenido. */
    data class Success(val value: BigDecimal) : CalculationResult
    /** El cálculo no se pudo resolver y trae el motivo. */
    data class Failure(val error: CalculatorError) : CalculationResult
}