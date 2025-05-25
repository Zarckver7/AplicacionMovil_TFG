package com.example.aurumverus.modelos

// Modelo de datos para representar un pedido hecho por un cliente
data class Pedido(
    val idPedido: String? = "",           // ID único del pedido
    val idProducto: String? = "",         // ID del producto que se pidió
    val uidCliente: String? = "",         // UID del cliente que hizo el pedido
    val nombreCliente: String? = "",      // Nombre del cliente (opcional)
    val direccion: String? = "",          // Dirección de entrega
    val comentario: String? = "",         // Comentario opcional del cliente
    val idVendedor: String? = "",         // UID del vendedor que recibirá el pedido
    val nombreProducto: String? = "",     // Nombre del producto pedido
    val precio: String? = "",             // Precio del producto
    val timestamp: Long? = 0,             // Momento en que se creó el pedido (en milisegundos)
    val confirmado: Boolean = false       // Si el pedido ha sido confirmado por el vendedor
)
