package com.example.aurumverus.modelos

import java.io.Serializable

data class Producto(
    val idProducto: String? = "",
    val uid: String? = "",
    val nombre: String? = "",
    val descripcion: String? = "",
    val precio: String? = "",
    val categoria: String? = "",
    val nombreVendedor: String? = "",
    val imagenPrincipal: String? = "",
    val timestamp: Long? = null,
    val imagenes: List<String>? = null
): Serializable
