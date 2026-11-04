package com.example.proyectocalculadora.model

/**
 * Una operación completada que queda guardada en el historial.
 * Las partes van separadas para que la interfaz arme la expresión con los símbolos de strings.xml.
 */
data class HistoryEntry(
    val firstOperand: String,
    val operation: Operation,
    val secondOperand: String,
    val result: String
)