package com.example.aurumverus.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aurumverus.R

class AdaptadorCarruselImagenes(private val imagenes: List<String>) :
    RecyclerView.Adapter<AdaptadorCarruselImagenes.ImagenViewHolder>() {

    // Clase interna que representa una "caja" donde va cada imagen
    inner class ImagenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imageCarrusel)
    }

    // Crea la vista para cada imagen del carrusel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_imagen_carrusel, parent, false)
        return ImagenViewHolder(view)
    }

    // Coloca la imagen correspondiente en su lugar
    override fun onBindViewHolder(holder: ImagenViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imagenes[position]) // Carga la imagen desde una URL
            .placeholder(R.drawable.galeria) // Imagen temporal mientras se carga
            .into(holder.img)
    }

    override fun getItemCount(): Int = imagenes.size
}

