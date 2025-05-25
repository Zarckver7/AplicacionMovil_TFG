package com.example.aurumverus.Vendedor

// Importaciones
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.aurumverus.R
import com.example.aurumverus.databinding.ActivityLoginVendedorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// Actividad para que un vendedor inicie sesión
class LoginVendedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginVendedorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cambia el color de la barra de estado si es compatible
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        binding = ActivityLoginVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Configura el diálogo de carga
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        // Botón para iniciar sesión
        binding.btnLoginV.setOnClickListener { validarInfo() }

        // Texto para registrarse si no tiene cuenta
        binding.tvRegistrarseV.setOnClickListener {
            startActivity(Intent(this, RegistroVendedorActivity::class.java))
        }
    }

    private var correo = ""
    private var contraseña = ""

    // Validación básica de campos
    private fun validarInfo() {
        correo = binding.edtxCorreoV.text.toString().trim()
        contraseña = binding.edtxContraseAV.text.toString().trim()

        when {
            correo.isEmpty() -> {
                binding.edtxCorreoV.error = "Ingrese su correo electrónico"
                binding.edtxCorreoV.requestFocus()
            }
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                binding.edtxCorreoV.error = "Correo electrónico no válido"
                binding.edtxCorreoV.requestFocus()
            }
            contraseña.isEmpty() -> {
                binding.edtxContraseAV.error = "Ingrese su contraseña"
                binding.edtxContraseAV.requestFocus()
            }
            else -> loginVendedor()
        }
    }

    // Función que intenta iniciar sesión con Firebase
    private fun loginVendedor() {
            progressDialog.setMessage("Iniciando sesión")
            progressDialog.show()

            firebaseAuth.signInWithEmailAndPassword(correo, contraseña)
                .addOnSuccessListener {
                    val uid = firebaseAuth.currentUser?.uid ?: return@addOnSuccessListener
                    val ref = FirebaseDatabase.getInstance().getReference("Usuarios")

                    ref.child(uid).get().addOnSuccessListener { snapshot ->
                        progressDialog.dismiss()
                        val tipo = snapshot.child("tipoUsuario").value?.toString()

                        if (tipo == "Vendedor") {
                            startActivity(Intent(applicationContext, MainActivityVendedor::class.java))
                            finishAffinity()
                        } else {
                            firebaseAuth.signOut()
                            Toast.makeText(this, "Esta cuenta no es de tipo Vendedor", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(this, "Error al verificar tipo de usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "No se pudo iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }


    }
