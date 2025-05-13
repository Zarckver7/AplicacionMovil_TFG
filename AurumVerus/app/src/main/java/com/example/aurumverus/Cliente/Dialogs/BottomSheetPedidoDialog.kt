package com.example.aurumverus.Cliente.Dialogs

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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.viewpager2.widget.ViewPager2
import java.util.*

class BottomSheetPedidoDialog(private val producto: Producto) : BottomSheetDialogFragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var txtNombre: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var txtPrecio: TextView
    private lateinit var edtComentario: EditText
    private lateinit var btnRealizarPedido: Button

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottomsheet_realizar_pedido, container, false)

        viewPager = view.findViewById(R.id.viewPagerImagenes)
        txtNombre = view.findViewById(R.id.txtNombreDetalle)
        txtDescripcion = view.findViewById(R.id.txtDescripcionDetalle)
        txtPrecio = view.findViewById(R.id.txtPrecioDetalle)
        edtComentario = view.findViewById(R.id.edtComentario)
        btnRealizarPedido = view.findViewById(R.id.btnRealizarPedido)

        txtNombre.text = producto.nombre
        txtDescripcion.text = producto.descripcion
        txtPrecio.text = "${producto.precio} €"

        val imagenes = producto.imagenes ?: listOf()
        viewPager.adapter = AdaptadorCarruselImagenes(imagenes)

        btnRealizarPedido.setOnClickListener {
            val comentario = edtComentario.text.toString().trim()
            guardarPedidoEnFirebase(comentario)
        }

        return view
    }

    private fun guardarPedidoEnFirebase(comentario: String) {
        val uidCliente = auth.uid ?: return
        val refUsuario = database.getReference("Usuarios").child(uidCliente)

        refUsuario.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombreCliente = snapshot.child("nombre").getValue(String::class.java) ?: "Sin nombre"
                val direccion = snapshot.child("direccion").getValue(String::class.java) ?: "Sin dirección"

                val pedidoId = database.getReference("Pedidos").push().key ?: UUID.randomUUID().toString()
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

                database.getReference("Pedidos").child(pedidoId).setValue(pedidoMap)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Pedido realizado con éxito", Toast.LENGTH_SHORT).show()
                        dismiss()
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
