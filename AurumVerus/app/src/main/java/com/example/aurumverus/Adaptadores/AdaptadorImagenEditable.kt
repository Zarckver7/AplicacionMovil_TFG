package com.example.aurumverus.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aurumverus.ImagenSeleccionada.ImagenSeleccionada
import com.example.aurumverus.R

class AdaptadorImagenEditable(
    private val imagenes: List<ImagenSeleccionada>,
    val onEliminar: (ImagenSeleccionada) -> Unit,
    val onSeleccionarPortada: (ImagenSeleccionada) -> Unit
) : RecyclerView.Adapter<AdaptadorImagenEditable.ImagenViewHolder>() {

    inner class ImagenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgVista: ImageView = itemView.findViewById(R.id.imgVista)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
        val btnPortada: ImageButton = itemView.findViewById(R.id.btnPortada)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_imagen_editable, parent, false)
        return ImagenViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagenViewHolder, position: Int) {
        val imagen = imagenes[position]

        val context = holder.itemView.context

        if (imagen.deInternet && imagen.imagenUrl != null) {
            Glide.with(context).load(imagen.imagenUrl).into(holder.imgVista)
        } else if (imagen.imageUri != null) {
            Glide.with(context).load(imagen.imageUri).into(holder.imgVista)
        } else {
            holder.imgVista.setImageResource(R.drawable.galeria)
        }

        // Portada visual
        if (imagen.esPortada) {
            holder.btnPortada.setImageResource(R.drawable.ico_marcador_portada)
        } else {
            holder.btnPortada.setImageResource(R.drawable.ico_marcador_no_portada)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminar(imagen)
        }

        holder.btnPortada.setOnClickListener {
            onSeleccionarPortada(imagen)
        }
    }

    override fun getItemCount(): Int = imagenes.size
}
