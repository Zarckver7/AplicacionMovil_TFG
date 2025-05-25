// Paquete donde está este fragmento
package com.example.aurumverus.Cliente.Dialogs

// Importaciones necesarias
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.aurumverus.Adaptadores.AdaptadorCarruselImagenes
import com.example.aurumverus.R
import com.example.aurumverus.modelos.Producto
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.viewpager2.widget.ViewPager2
import java.util.*

// Clase que muestra un panel inferior para realizar un pedido
class BottomSheetPedidoDialog(private val producto: Producto) : BottomSheetDialogFragment() {

    // Elementos visuales
    private lateinit var viewPager: ViewPager2
    private lateinit var txtNombre: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var txtPrecio: TextView
    private lateinit var edtComentario: EditText
    private lateinit var btnRealizarPedido: Button

    // Instancias de Firebase
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    // Se ejecuta al crear la vista del diálogo
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Infla el diseño XML del bottom sheet
        val view = inflater.inflate(R.layout.bottomsheet_realizar_pedido, container, false)

        // Asocia variables con elementos visuales
        viewPager = view.findViewById(R.id.viewPagerImagenes)
        txtNombre = view.findViewById(R.id.txtNombreDetalle)
        txtDescripcion = view.findViewById(R.id.txtDescripcionDetalle)
        txtPrecio = view.findViewById(R.id.txtPrecioDetalle)
        edtComentario = view.findViewById(R.id.edtComentario)
        btnRealizarPedido = view.findViewById(R.id.btnRealizarPedido)

        // Muestra los datos del producto
        txtNombre.text = producto.nombre
        txtDescripcion.text = producto.descripcion
        txtPrecio.text = "${producto.precio} €"

        // Crea el carrusel de imágenes (si hay)
        val imagenes = producto.imagenes ?: listOf()
        viewPager.adapter = AdaptadorCarruselImagenes(imagenes)

        // Cuando el usuario hace clic en "Realizar Pedido"
        btnRealizarPedido.setOnClickListener {
            val comentario = edtComentario.text.toString().trim()
            guardarPedidoEnFirebase(comentario)
        }

        return view
    }

    // Guarda el pedido en la base de datos
    private fun guardarPedidoEnFirebase(comentario: String) {
        val uidCliente = auth.uid ?: return // ID del cliente actual

        // Obtiene los datos del cliente desde Firebase
        val refUsuario = database.getReference("Usuarios").child(uidCliente)

        refUsuario.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Lee el nombre y dirección del cliente (si existen)
                val nombreCliente = snapshot.child("nombre").getValue(String::class.java) ?: "Sin nombre"
                val direccion = snapshot.child("direccion").getValue(String::class.java) ?: "Sin dirección"

                // Crea un ID único para el pedido
                val pedidoId = database.getReference("Pedidos").push().key ?: UUID.randomUUID().toString()

                // Crea un mapa con los datos del pedido
                val pedidoMap = mapOf(
                    "idPedido" to pedidoId,
                    "idProducto" to producto.idProducto,
                    "uidCliente" to uidCliente,
                    "direccion" to direccion,
                    "comentario" to comentario,
                    "idVendedor" to producto.uid,
                    "nombreProducto" to producto.nombre,
                    "precio" to producto.precio,
                    "timestamp" to System.currentTimeMillis()
                )

                // Guarda el pedido en la base de datos
                database.getReference("Pedidos").child(pedidoId).setValue(pedidoMap)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Pedido realizado con éxito", Toast.LENGTH_SHORT).show()
                        dismiss() // Cierra el panel inferior
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al realizar el pedido", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "No se pudo obtener datos del cliente", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
