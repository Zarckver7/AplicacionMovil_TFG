package com.example.aurumverus.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.aurumverus.Constantes
import com.example.aurumverus.R
import com.example.aurumverus.databinding.ActivityRegistroClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroClienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarC.setOnClickListener {
            validarInformacion()

        }
    }

    private var nombre = ""
    private var correo = ""
    private var contraseña = ""
    private var cContraseña = ""

    private fun validarInformacion() {
        nombre = binding.edtxNombreC.text.toString().trim()
        correo = binding.edtxCorreoC.text.toString().trim()
        contraseña = binding.edtxContraseAC.text.toString().trim()
        cContraseña = binding.edtxConContraseAC.text.toString().trim()

        if (nombre.isEmpty()) {
            binding.edtxNombreC.error = "Ingrese su nombre"
            binding.edtxNombreC.requestFocus()
        } else if (correo.isEmpty()) {
            binding.edtxCorreoC.error = "Ingrese su correo"
            binding.edtxCorreoC.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.edtxCorreoC.error = "Ingrese un correo válido"
            binding.edtxCorreoC.requestFocus()
        } else if (contraseña.isEmpty()) {
            binding.edtxContraseAC.error = "Ingrese su contraseña"
            binding.edtxCorreoC.requestFocus()
        } else if (contraseña.length < 8) {
            binding.edtxContraseAC.error = "La contraseña debe tener al menos 8 caracteres"
            binding.edtxContraseAC.requestFocus()
        } else if (cContraseña.isEmpty()) {
            binding.edtxConContraseAC.error = "Confirme su contraseña"
            binding.edtxConContraseAC.requestFocus()
        } else if (contraseña != cContraseña) {
            binding.edtxConContraseAC.error = "Las contraseñas no coinciden"
            binding.edtxConContraseAC.requestFocus()
        } else {
            registrarCliente()
        }
    }

    private fun registrarCliente() {
        progressDialog.setMessage("Registrando datos...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(correo, contraseña)
            .addOnSuccessListener {
                insertarInfoBD()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Ocurrió un error en el registro, código de error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun insertarInfoBD() {
        progressDialog.setMessage("Guardando información...")

        val uidBD = firebaseAuth.uid
        val nombreBD = nombre
        val correoBD = correo
        val horaBD = Constantes().tiempoD()

        val datosC = HashMap<String, Any>()

        datosC["uid"] = "$uidBD"
        datosC["nombre"] = "$nombreBD"
        datosC["correo"] = "$correoBD"
        datosC["tipoUsuario"] = "Cliente"
        datosC["imagen"] = ""
        datosC["hora"] = horaBD

        val referenciaBD = FirebaseDatabase.getInstance().getReference("Usuarios")
        referenciaBD.child("$uidBD")
            .setValue(datosC)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this@RegistroClienteActivity, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Ocurrió un error en la base de datos, código de error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}