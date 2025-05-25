package com.example.aurumverus.ImagenSeleccionada

import android.net.Uri

// Clase que representa una imagen seleccionada para un producto
class ImagenSeleccionada {

    // Identificador único de la imagen (puede ser la hora o posición)
    var id = ""

    // URI local de la imagen (si viene del dispositivo del usuario)
    var imageUri: Uri? = null

    // URL de la imagen (si ya está almacenada en internet/Firebase)
    var imagenUrl: String? = null

    // Indica si esta imagen ya viene de internet (true) o del dispositivo (false)
    var deInternet = false

    // Indica si esta imagen es la "portada" (la principal del producto)
    var esPortada = false

    // Constructor vacío necesario para Firebase y para uso por defecto
    constructor()

    // Constructor con todos los parámetros
    constructor(
        id: String,
        imageUri: Uri?,
        imagenUrl: String?,
        deInternet: Boolean,
        esPortada: Boolean
    ) {
        this.id = id
        this.imageUri = imageUri
        this.imagenUrl = imagenUrl
        this.deInternet = deInternet
        this.esPortada = esPortada
    }
}
