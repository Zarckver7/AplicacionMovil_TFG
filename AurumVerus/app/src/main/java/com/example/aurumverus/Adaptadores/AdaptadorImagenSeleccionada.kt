package com.example.aurumverus.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.aurumverus.ImagenSeleccionada.ImagenSeleccionada
import com.example.aurumverus.R
import com.example.aurumverus.databinding.ImagenesElegidasBinding

class AdaptadorImagenSeleccionada(
    // Adaptador para mostrar imágenes seleccionadas por el usuario (para subir o editar)
    private val context : Context,
    private val imagenesSeleccionasArrayList : ArrayList<ImagenSeleccionada>
): Adapter<AdaptadorImagenSeleccionada.HolderImagenSeleccionada>() {
    // Se vincula la vista del XML con Kotlin usando view binding
    private lateinit var binding : ImagenesElegidasBinding

    // Crea el contenedor visual para una imagen en la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSeleccionada {
        binding = ImagenesElegidasBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderImagenSeleccionada(binding.root)
    }
    //Devuelve el tamaño del listado
    override fun getItemCount(): Int {
        return imagenesSeleccionasArrayList.size
    }
    // Muestra la imagen en pantalla
    override fun onBindViewHolder(holder: HolderImagenSeleccionada, position: Int) {
        val modelo = imagenesSeleccionasArrayList[position]
        val imagenUri = modelo.imageUri

        //Leyendo imagenes
        try {
            // Intenta mostrar la imagen con Glide
            Glide.with(context)
                .load(imagenUri)
                .placeholder(R.drawable.galeria)
                .into(holder.subirImg)
        }catch (e: Exception){

        }

        // Si se presiona el botón de cancelar, elimina la imagen de la lista
        holder.cancelar.setOnClickListener{
            imagenesSeleccionasArrayList.remove(modelo)
            // Se vuelve a mostrar la lista sin la imagen que se acaba de eliminar
            notifyDataSetChanged()
        }

    }

    // Clase que representa cada ítem (imagen) en la lista
    inner class HolderImagenSeleccionada(itenView : View) : RecyclerView.ViewHolder(itenView){
        var subirImg = binding.subirImg
        var cancelar = binding.cancelar
    }


}