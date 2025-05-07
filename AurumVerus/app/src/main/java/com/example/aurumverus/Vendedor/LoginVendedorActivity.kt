package com.example.aurumverus.Vendedor

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aurumverus.R
import com.example.aurumverus.databinding.ActivityLoginVendedorBinding
import com.google.firebase.auth.FirebaseAuth

class LoginVendedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginVendedorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnLoginV.setOnClickListener {
            validarInfo()
        }

        binding.tvRegistrarseV.setOnClickListener {
            startActivity(Intent(applicationContext, RegistroVendedorActivity::class.java))
        }
    }

    private var correo = ""
    private var contraseña = ""

    private fun validarInfo() {
        correo = binding.edtxCorreoV.text.toString().trim()
        contraseña = binding.edtxContraseAV.text.toString().trim()

        if (correo.isEmpty()) {
            binding.edtxCorreoV.error = "Ingrese su correo electrónico"
            binding.edtxCorreoV.requestFocus()
        } else if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            binding.edtxCorreoV.error = "Correo electrónico no válido"
            binding.edtxCorreoV.requestFocus()
        }else if (contraseña.isEmpty()) {
            binding.edtxContraseAV.error = "Ingrese su contraseña"
            binding.edtxContraseAV.requestFocus()
        } else{
            loginVendedor()
        }
    }

    private fun loginVendedor() {
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(correo, contraseña)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(applicationContext, MainActivityVendedor::class.java))
                finishAffinity()
            }
        .addOnFailureListener { e ->
            progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se pudo iniciar sesión debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}