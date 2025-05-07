package com.example.aurumverus

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Constantes {

    fun tiempoD(): String {
        val millis = System.currentTimeMillis()

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val hora = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fecha = dateFormat.format(Date(millis))

        return "$fecha $hora"
    }
}
