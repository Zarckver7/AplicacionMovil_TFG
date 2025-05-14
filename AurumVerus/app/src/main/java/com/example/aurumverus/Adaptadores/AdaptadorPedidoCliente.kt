package com.example.aurumverus.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aurumverus.R
import com.example.aurumverus.modelos.Pedido
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.app.AlertDialog

class AdaptadorPedidoCliente(private val listaPedidos: List<Pedido>) :
    RecyclerView.Adapter<AdaptadorPedidoCliente.PedidoViewHolder>() {

    inner class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.txtNombreProducto)
        val txtPrecio: TextView = view.findViewById(R.id.txtPrecioProducto)
        val txtFecha: TextView = view.findViewById(R.id.txtFechaPedido)
        val iconEstado: ImageView = view.findViewById(R.id.iconEstadoPedido)
        val txtEstado: TextView = view.findViewById(R.id.txtEstadoTexto)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_cliente, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = listaPedidos[position]
        holder.txtNombre.text = pedido.nombreProducto
        holder.txtPrecio.text = "Precio: ${pedido.precio} €"

        val fecha = pedido.timestamp ?: 0
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.txtFecha.text = "Fecha de pedido: ${formato.format(Date(fecha))}"

        holder.txtEstado.text = if (pedido.confirmado) "Estado: Confirmado" else "Estado: Pendiente de confirmación"

        holder.iconEstado.setImageResource(
            if (pedido.confirmado) R.drawable.ico_confirmado else R.drawable.ico_sin_confirmar
        )

        holder.itemView.setOnClickListener {
            val contexto = holder.itemView.context
            val mensaje = """
        Producto: ${pedido.nombreProducto}
        Precio: ${pedido.precio} €
        Fecha de pedido: ${formato.format(Date(fecha))}
        Dirección: ${pedido.direccion}
        Comentario: ${pedido.comentario ?: "Sin comentario"}
        Estado: ${if (pedido.confirmado) "Confirmado" else "Pendiente"}
        """.trimIndent()

            AlertDialog.Builder(contexto)
                .setTitle("Detalles del pedido")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show()
        }


    }

    override fun getItemCount(): Int = listaPedidos.size
}
