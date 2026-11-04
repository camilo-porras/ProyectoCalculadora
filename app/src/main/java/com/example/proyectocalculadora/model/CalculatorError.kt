package com.example.proyectocalculadora.model

/**
 * Errores controlados de la calculadora.
 * El ViewModel los publica como estado y la interfaz los traduce a texto con stringResource().
 */
enum class CalculatorError {
    /** Se intentó dividir entre cero. */
    DivisionByZero,
    /** El texto escrito no se pudo interpretar como un número. */
    InvalidOperation
}