package com.example.aurumverus.modelos

data class Pedido(
    val idPedido: String? = "",
    val idProducto: String? = "",
    val uidCliente: String? = "",
    val nombreCliente: String? = "",
    val direccion: String? = "",
    val comentario: String? = "",
    val idVendedor: String? = "",
    val nombreProducto: String? = "",
    val precio: String? = "",
    val timestamp: Long? = 0,
    val confirmado: Boolean = false
)
