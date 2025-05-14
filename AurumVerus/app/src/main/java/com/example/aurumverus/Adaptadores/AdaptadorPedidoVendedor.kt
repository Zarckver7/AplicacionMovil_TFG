package com.example.aurumverus.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.aurumverus.R
import com.example.aurumverus.modelos.Pedido
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AdaptadorPedidoVendedor(private val listaPedidos: List<Pedido>) :
    RecyclerView.Adapter<AdaptadorPedidoVendedor.PedidoViewHolder>() {

    inner class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.txtNombreProducto)
        val txtCliente: TextView = view.findViewById(R.id.txtCliente)
        val txtDireccion: TextView = view.findViewById(R.id.txtDireccion)
        val txtComentario: TextView = view.findViewById(R.id.txtComentario)
        val txtFecha: TextView = view.findViewById(R.id.txtFecha)
        val txtEstado: TextView = view.findViewById(R.id.txtEstado)
        val btnConfirmar: Button = view.findViewById(R.id.btnConfirmar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_vendedor, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = listaPedidos[position]

        holder.txtNombre.text = pedido.nombreProducto
        holder.txtCliente.text = "Cliente: ${pedido.nombreCliente}"
        holder.txtDireccion.text = "Dirección: ${pedido.direccion}"
        holder.txtComentario.text = "Comentario: ${pedido.comentario ?: "Sin comentario"}"

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fecha = formato.format(Date(pedido.timestamp ?: 0))
        holder.txtFecha.text = "Fecha: $fecha"

        if (pedido.confirmado) {
            holder.txtEstado.text = "Estado: Confirmado"
            holder.btnConfirmar.visibility = View.GONE
        } else {
            holder.txtEstado.text = "Estado: Pendiente"
            holder.btnConfirmar.visibility = View.VISIBLE

            holder.btnConfirmar.setOnClickListener {
                val ref = FirebaseDatabase.getInstance().getReference("Pedidos").child(pedido.idPedido ?: return@setOnClickListener)
                ref.child("confirmado").setValue(true).addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Pedido confirmado", Toast.LENGTH_SHORT).show()
                    notifyItemChanged(position)
                }.addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Error al confirmar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = listaPedidos.size
}
