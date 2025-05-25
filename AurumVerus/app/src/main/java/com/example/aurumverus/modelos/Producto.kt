package com.example.aurumverus.modelos

import java.io.Serializable

// Modelo de datos para un producto ofrecido por un vendedor
data class Producto(
    val idProducto: String? = "",           // ID único del producto
    val uid: String? = "",                  // UID del vendedor que lo publica
    val nombre: String? = "",               // Nombre del producto
    val descripcion: String? = "",          // Descripción del producto
    val precio: String? = "",               // Precio del producto
    val categoria: String? = "",            // Categoría (Ej: Ropa, Electrónica...)
    val nombreVendedor: String? = "",       // Nombre del vendedor
    val imagenPrincipal: String? = "",      // URL de la imagen principal
    val timestamp: Long? = null,            // Fecha/hora de creación del producto
    val imagenes: List<String>? = null      // Lista de URLs de imágenes del producto
) : Serializable // Permite enviar este objeto entre actividades con Intents
