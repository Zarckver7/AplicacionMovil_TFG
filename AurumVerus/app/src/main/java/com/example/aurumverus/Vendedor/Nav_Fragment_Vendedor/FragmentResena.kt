package com.example.aurumverus.Vendedor.Nav_Fragment_Vendedor

// Importaciones necesarias
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aurumverus.R

// Fragmento para mostrar o recibir reseñas (aún sin lógica implementada)
class FragmentResena : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Carga el diseño XML (actualmente vacío o solo de presentación)
        return inflater.inflate(R.layout.fragment_resena, container, false)
    }
}
