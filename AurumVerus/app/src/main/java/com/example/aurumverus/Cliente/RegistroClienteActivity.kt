// Paquete donde se encuentra esta clase
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
import com.example.aurumverus.Constantes
import com.example.aurumverus.R
import com.example.aurumverus.databinding.ActivityRegistroClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// Pantalla donde un cliente puede registrarse en la aplicación
class RegistroClienteActivity : AppCompatActivity() {

    // View Binding para acceder a la interfaz de usuario definida en XML
    private lateinit var binding: ActivityRegistroClienteBinding

    // Firebase Authentication y base de datos
    private lateinit var firebaseAuth: FirebaseAuth

    // Cuadro de carga para mostrar progreso al usuario
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cambia el color de la barra de estado (si Android lo permite)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        // Carga la interfaz
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa Firebase
        firebaseAuth = FirebaseAuth.getInstance()

        // Configura el diálogo de progreso
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        // Botón que comienza la validación de datos para registrarse
        binding.btnRegistrarC.setOnClickListener {
            validarInformacion()
        }
    }

    // Variables para guardar temporalmente los datos ingresados
    private var nombre = ""
    private var correo = ""
    private var contraseña = ""
    private var cContraseña = ""

    // Verifica que los campos estén correctamente llenados
    private fun validarInformacion() {
        nombre = binding.edtxNombreC.text.toString().trim()
        correo = binding.edtxCorreoC.text.toString().trim()
        contraseña = binding.edtxContraseAC.text.toString().trim()
        cContraseña = binding.edtxConContraseAC.text.toString().trim()

        when {
            nombre.isEmpty() -> {
                binding.edtxNombreC.error = "Ingrese su nombre"
                binding.edtxNombreC.requestFocus()
            }
            correo.isEmpty() -> {
                binding.edtxCorreoC.error = "Ingrese su correo"
                binding.edtxCorreoC.requestFocus()
            }
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                binding.edtxCorreoC.error = "Ingrese un correo válido"
                binding.edtxCorreoC.requestFocus()
            }
            contraseña.isEmpty() -> {
                binding.edtxContraseAC.error = "Ingrese su contraseña"
                binding.edtxContraseAC.requestFocus()
            }
            contraseña.length < 8 -> {
                binding.edtxContraseAC.error = "La contraseña debe tener al menos 8 caracteres"
                binding.edtxContraseAC.requestFocus()
            }
            cContraseña.isEmpty() -> {
                binding.edtxConContraseAC.error = "Confirme su contraseña"
                binding.edtxConContraseAC.requestFocus()
            }
            contraseña != cContraseña -> {
                binding.edtxConContraseAC.error = "Las contraseñas no coinciden"
                binding.edtxConContraseAC.requestFocus()
            }
            else -> registrarCliente() //Si todo está bien, procede al registro
        }
    }

    // Registra al usuario en Firebase Authentication
    private fun registrarCliente() {
        progressDialog.setMessage("Registrando datos...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(correo, contraseña)
            .addOnSuccessListener {
                // Si el registro es exitoso, guarda la información adicional
                insertarInfoBD()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Ocurrió un error en el registro: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // Guarda los datos del usuario en la base de datos de Firebase
    private fun insertarInfoBD() {
        progressDialog.setMessage("Guardando información...")

        // Información que se guardará
        val uidBD = firebaseAuth.uid // ID único del usuario
        val nombreBD = nombre
        val correoBD = correo
        val horaBD = Constantes().tiempoD() // Fecha y hora actual

        // Mapa con los datos a guardar
        val datosC = HashMap<String, Any>()
        datosC["uid"] = "$uidBD"
        datosC["nombre"] = "$nombreBD"
        datosC["correo"] = "$correoBD"
        datosC["tipoUsuario"] = "Cliente"
        datosC["hora"] = horaBD

        // Referencia al nodo "Usuarios" en la base de datos
        val referenciaBD = FirebaseDatabase.getInstance().getReference("Usuarios")

        // Guarda los datos bajo el ID único del usuario
        referenciaBD.child("$uidBD")
            .setValue(datosC)
            .addOnSuccessListener {
                // Si todo va bien, abre la pantalla principal del cliente
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Error al guardar en la base de datos: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
