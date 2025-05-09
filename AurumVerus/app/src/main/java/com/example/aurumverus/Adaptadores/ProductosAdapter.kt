package com.example.aurumverus.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aurumverus.R
import com.example.aurumverus.modelos.Producto

class ProductosAdapter(private val productos: List<Producto>) :
    RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProducto: ImageView = view.findViewById(R.id.imgProducto)
        val tvNombre: TextView = view.findViewById(R.id.tvNombreProducto)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecioProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.tvNombre.text = producto.nombre ?: ""
        holder.tvPrecio.text = "€${producto.precio ?: "0.00"}"

        // Si más adelante tienes imagenes, puedes cargarlas aquí con Glide
        // Glide.with(holder.itemView.context)
        //     .load(producto.imagenUrl)
        //     .placeholder(R.drawable.ic_placeholder)
        //     .into(holder.imgProducto)
    }

    override fun getItemCount(): Int = productos.size
}
