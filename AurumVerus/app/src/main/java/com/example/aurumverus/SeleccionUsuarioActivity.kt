package com.example.aurumverus

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.aurumverus.Cliente.LoginClienteActivity
import com.example.aurumverus.Vendedor.LoginVendedorActivity
import com.example.aurumverus.databinding.ActivitySeleccionUsuarioBinding

class SeleccionUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeleccionUsuarioBinding // permite acceder a elementos del XML fácilmente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cambia el color de la barra superior si la versión de Android lo permite
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        // Se infla (carga) la vista con los botones de selección
        binding = ActivitySeleccionUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si el usuario presiona "Cliente", lo lleva al login del cliente
        binding.btnSeleccionarCliente.setOnClickListener {
            startActivity(Intent(this, LoginClienteActivity::class.java))
        }

        // Si el usuario presiona "Vendedor", lo lleva al login del vendedor
        binding.btnSeleccionarVendedor.setOnClickListener {
            startActivity(Intent(this, LoginVendedorActivity::class.java))
        }
    }
}