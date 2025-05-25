package com.example.aurumverus

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Constantes {

    // Función que devuelve la fecha y la hora actual en formato "dd/MM/yyyy HH:mm:ss"
    fun tiempoD(): String {
        val millis = System.currentTimeMillis() // obtiene el tiempo actual en milisegundos

        // Se convierten los milisegundos a horas, minutos y segundos
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24

        // Se formatea la hora como texto (ej. "14:23:08")
        val hora = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        // Se formatea la fecha como texto (ej. "21/05/2025")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fecha = dateFormat.format(Date(millis))

        // Se devuelve fecha y hora juntas
        return "$fecha $hora"
    }
}

