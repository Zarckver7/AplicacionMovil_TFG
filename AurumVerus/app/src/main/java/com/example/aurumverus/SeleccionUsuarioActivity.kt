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

    private lateinit var binding: ActivitySeleccionUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        binding = ActivitySeleccionUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSeleccionarCliente.setOnClickListener {
            startActivity(Intent(this@SeleccionUsuarioActivity, LoginClienteActivity::class.java))
        }

        binding.btnSeleccionarVendedor.setOnClickListener {
            startActivity(Intent(this@SeleccionUsuarioActivity, LoginVendedorActivity::class.java))
        }
    }
}