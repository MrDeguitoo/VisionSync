package com.duoc.visionsyncc1.util

fun rutValido(rut: String): Boolean {
    val limpio = rut.replace(".", "").replace("-", "").uppercase()
    if (limpio.length <  2) return false
    val cuerpo = limpio.dropLast(1)
    val dv = limpio.last()
    if (!cuerpo.all { it.isDigit() }) return false
    var suma = 0
    var multiplicador = 2
    for (d in cuerpo.reversed()) {
        suma += d.digitToInt() * multiplicador
        multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
    }
    val resto = 11 - (suma % 11)
    val esperado = when (resto) { 11 -> '0'; 10 -> 'K'; else -> resto.digitToChar() }
    return dv == esperado
}