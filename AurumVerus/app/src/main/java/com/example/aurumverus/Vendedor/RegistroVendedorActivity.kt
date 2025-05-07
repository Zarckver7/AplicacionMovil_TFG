package com.example.aurumverus.Vendedor

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aurumverus.Constantes
import com.example.aurumverus.R
import com.example.aurumverus.databinding.ActivityRegistroVendedorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroVendedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroVendedorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarV.setOnClickListener {
            validarInformacion()
        }
    }
    private var nombre = ""
    private var correo = ""
    private var contraseña = ""
    private var cContraseña = ""

    private fun validarInformacion() {
        nombre = binding.edtxNombreV.text.toString().trim()
        correo = binding.edtxCorreoV.text.toString().trim()
        contraseña = binding.edtxContraseAV.text.toString()
            .trim()/*se sustituye el "ña" por "A" porque no reconoce la ñ*/
        cContraseña = binding.edtxConContraseAV.text.toString()
            .trim()/*se sustituye el "ña" por "A" porque no reconoce la ñ*/

        if (nombre.isEmpty()) {
            binding.edtxNombreV.error = "Ingrese su nombre"
            binding.edtxNombreV.requestFocus()
        } else if (correo.isEmpty()) {
            binding.edtxCorreoV.error = "Ingrese su correo"
            binding.edtxCorreoV.requestFocus()
        }else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.edtxCorreoV.error = "Ingrese un correo válido"
            binding.edtxCorreoV.requestFocus()
        }else if (contraseña.isEmpty()) {
            binding.edtxContraseAV.error = "Ingrese su contraseña"
            binding.edtxContraseAV.requestFocus()
        }else if (contraseña.length < 8){
            binding.edtxContraseAV.error = "La contraseña debe tener al menos 8 caracteres"
            binding.edtxContraseAV.requestFocus()
        }else if (cContraseña.isEmpty()) {
            binding.edtxConContraseAV.error = "Confirme su contraseña"
            binding.edtxConContraseAV.requestFocus()
        }else if (contraseña != cContraseña) {
            binding.edtxConContraseAV.error = "Las contraseñas no coinciden"
            binding.edtxConContraseAV.requestFocus()
        } else {
            registrarVendedor()
        }

    }



    private fun registrarVendedor() {
        progressDialog.setMessage("Registrando...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(correo, contraseña)
            .addOnSuccessListener {
                insertarInfoBD()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Ocurrió un error en el registro, código de error: ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }

    private fun insertarInfoBD() {
        progressDialog.setMessage("Guardando información...")

        val uidBD = firebaseAuth.uid
        val nombreBD = nombre
        val correoBD = correo
        val horaBD = Constantes().tiempoD()
        val datosV = HashMap<String, Any>()

        datosV["uid"] = "$uidBD"
        datosV["nombre"] = "$nombreBD"
        datosV["correo"] = "$correoBD"
        datosV["tipoUsuario"] = "Vendedor"
        datosV["hora"] = horaBD

        val referenciaBD = FirebaseDatabase.getInstance().getReference("Usuarios")
        referenciaBD.child("$uidBD")
            .setValue(datosV)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityVendedor::class.java))
                finish()
            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Ocurrió un error en la base de datos, código de error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}