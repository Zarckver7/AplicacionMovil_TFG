package com.example.aurumverus.Vendedor

// Importaciones necesarias
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

        // Cambia el color de la barra de estado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere...")
        progressDialog.setCanceledOnTouchOutside(false)

        // Botón para registrar
        binding.btnRegistrarV.setOnClickListener { validarInformacion() }
    }

    private var nombre = ""
    private var correo = ""
    private var contraseña = ""
    private var cContraseña = ""

    private fun validarInformacion() {
        nombre = binding.edtxNombreV.text.toString().trim()
        correo = binding.edtxCorreoV.text.toString().trim()
        contraseña = binding.edtxContraseAV.text.toString().trim()
        cContraseña = binding.edtxConContraseAV.text.toString().trim()

        when {
            nombre.isEmpty() -> binding.edtxNombreV.error = "Ingrese el nombre de la empresa"
            correo.isEmpty() -> binding.edtxCorreoV.error = "Ingrese su correo"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> binding.edtxCorreoV.error = "Correo no válido"
            contraseña.isEmpty() -> binding.edtxContraseAV.error = "Ingrese su contraseña"
            contraseña.length < 8 -> binding.edtxContraseAV.error = "Mínimo 8 caracteres"
            cContraseña.isEmpty() -> binding.edtxConContraseAV.error = "Confirme su contraseña"
            contraseña != cContraseña -> binding.edtxConContraseAV.error = "No coinciden"
            else -> registrarVendedor()
        }
    }

    // Crea usuario en Firebase
    private fun registrarVendedor() {
        progressDialog.setMessage("Registrando...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(correo, contraseña)
            .addOnSuccessListener { insertarInfoBD() }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error en el registro: " +
                        "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Guarda datos adicionales en la base de datos
    private fun insertarInfoBD() {
        progressDialog.setMessage("Guardando información...")

        val uidBD = firebaseAuth.uid
        val horaBD = Constantes().tiempoD()

        val datosV = hashMapOf(
            "uid" to uidBD!!,
            "nombre" to nombre,
            "correo" to correo,
            "tipoUsuario" to "Vendedor",
            "hora" to horaBD
        )

        val referenciaBD = FirebaseDatabase.getInstance().getReference("Usuarios")
        referenciaBD.child(uidBD).setValue(datosV)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityVendedor::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error en la base de datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
