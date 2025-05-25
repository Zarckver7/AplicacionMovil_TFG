// Paquete donde se encuentra este archivo
package com.example.aurumverus.Cliente

// Importaciones necesarias
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.aurumverus.R
import com.example.aurumverus.databinding.ActivityLoginClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// Actividad para iniciar sesión como cliente
class LoginClienteActivity : AppCompatActivity() {

    // View binding para acceder al diseño XML
    private lateinit var binding: ActivityLoginClienteBinding

    // Autenticación con Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // Diálogo de carga (muestra "Espere por favor...")
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cambia el color de la barra de estado si es compatible
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        // Inicializa Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configura el cuadro de diálogo de carga
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        // Asocia el archivo XML con esta clase
        binding = ActivityLoginClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botón para iniciar sesión
        binding.btnLoginV.setOnClickListener {
            validarInfo()  // Verifica que los datos ingresados sean correctos
        }

        // Texto para ir al registro si no tiene cuenta
        binding.tvRegistrarseC.setOnClickListener {
            startActivity(Intent(this, RegistroClienteActivity::class.java))
        }
    }

    // Variables para almacenar los datos ingresados por el usuario
    private var correo = ""
    private var contraseña = ""

    // Función para validar el correo y contraseña ingresados
    private fun validarInfo() {
        correo = binding.edtxCorreoC.text.toString().trim()
        contraseña = binding.edtxContraseAC.text.toString().trim()

        // Verifica que el correo no esté vacío
        if (correo.isEmpty()) {
            binding.edtxCorreoC.error = "Ingrese su correo electrónico"
            binding.edtxCorreoC.requestFocus()
        }
        // Verifica que el correo tenga formato válido
        else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.edtxCorreoC.error = "Correo electrónico no válido"
            binding.edtxCorreoC.requestFocus()
        }
        // Verifica que la contraseña no esté vacía
        else if (contraseña.isEmpty()) {
            binding.edtxContraseAC.error = "Ingrese su contraseña"
            binding.edtxContraseAC.requestFocus()
        }
        // Si todo es válido, intenta iniciar sesión
        else {
            loginCliente()
        }
    }

    // Función que usa Firebase para iniciar sesión
    private fun loginCliente() {
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(correo, contraseña)
            .addOnSuccessListener {
                val uid = firebaseAuth.currentUser?.uid ?: return@addOnSuccessListener
                val ref = FirebaseDatabase.getInstance().getReference("Usuarios")

                ref.child(uid).get().addOnSuccessListener { snapshot ->
                    progressDialog.dismiss()
                    val tipo = snapshot.child("tipoUsuario").value?.toString()

                    if (tipo == "Cliente") {
                        startActivity(Intent(applicationContext, MainActivityCliente::class.java))
                        finishAffinity()
                    } else {
                        firebaseAuth.signOut()
                        Toast.makeText(this, "Esta cuenta no es de tipo Cliente", Toast.LENGTH_SHORT).show()
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
