package com.example.aurumverus.Adaptadores

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aurumverus.R
import com.example.aurumverus.Vendedor.Productos.EditarProductoActivity
import com.example.aurumverus.modelos.Producto

class ProductosAdapter(private val productos: List<Producto>) :
    RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

    var onProductoLongClick: ((Producto) -> Unit)? = null

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val precio: TextView = itemView.findViewById(R.id.tvPrecioProducto)
        val imagen: ImageView = itemView.findViewById(R.id.imgProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.nombre
        holder.precio.text = "${producto.precio}€"

        Glide.with(holder.itemView.context)
            .load(producto.imagenPrincipal)
            .placeholder(R.drawable.galeria)
            .into(holder.imagen)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditarProductoActivity::class.java)
            intent.putExtra("productoId", producto.idProducto)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = productos.size
}
