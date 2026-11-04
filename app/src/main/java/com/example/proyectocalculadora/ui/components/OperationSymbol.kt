package com.example.proyectocalculadora.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.proyectocalculadora.R
import com.example.proyectocalculadora.model.Operation

/**
 * Símbolo visible de una operación (+, −, ×, ÷), tomado de strings.xml.
 * El ViewModel solo conoce [Operation]; esta función es la que lo traduce a texto para la pantalla.
 */
@Composable
fun Operation.symbol(): String = stringResource(
    when (this) {
        Operation.Add -> R.string.add_symbol
        Operation.Subtract -> R.string.subtract_symbol
        Operation.Multiply -> R.string.multiply_symbol
        Operation.Divide -> R.string.divide_symbol
    }
)