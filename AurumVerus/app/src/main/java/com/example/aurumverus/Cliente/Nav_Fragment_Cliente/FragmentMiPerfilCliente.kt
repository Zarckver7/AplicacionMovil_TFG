package com.example.aurumverus.Cliente.Nav_Fragment_Cliente

// Importaciones necesarias
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.aurumverus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentMiPerfilCliente : Fragment() {

    // Elementos visuales
    private lateinit var edtNombre: EditText
    private lateinit var edtDireccion: EditText
    private lateinit var edtTelefono: EditText
    private lateinit var btnGuardar: Button

    // Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("Usuarios")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Carga el layout del perfil
        return inflater.inflate(R.layout.fragment_mi_perfil_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Enlaza vistas con sus variables
        edtNombre = view.findViewById(R.id.edtNombrePerfil)
        edtDireccion = view.findViewById(R.id.edtDireccionPerfil)
        edtTelefono = view.findViewById(R.id.edtTelefonoPerfil)
        btnGuardar = view.findViewById(R.id.btnGuardarPerfil)

        // Carga los datos actuales del perfil
        cargarDatosPerfil()

        // Botón para guardar los cambios del perfil
        btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    // Carga los datos del usuario desde Firebase
    private fun cargarDatosPerfil() {
        val uid = auth.uid ?: return
        db.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                edtNombre.setText(snapshot.child("nombre").getValue(String::class.java) ?: "")
                edtDireccion.setText(snapshot.child("direccion").getValue(String::class.java) ?: "")
                edtTelefono.setText(snapshot.child("telefono").getValue(String::class.java) ?: "")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar perfil", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Guarda los cambios hechos al perfil en Firebase
    private fun guardarCambios() {
        val uid = auth.uid ?: return
        val nombre = edtNombre.text.toString().trim()
        val direccion = edtDireccion.text.toString().trim()
        val telefono = edtTelefono.text.toString().trim()

        // Validaciones básicas
        if (nombre.isEmpty()) {
            edtNombre.error = "El nombre no puede estar vacío"
            edtNombre.requestFocus()
            return
        } else if (telefono.isNotBlank() && !telefono.matches(Regex("^\\d{9}$"))) {
            edtTelefono.error = "Debe tener exactamente 9 dígitos"
            edtTelefono.requestFocus()
            return
        }

        // Datos a actualizar
        val actualizaciones = mapOf(
            "nombre" to nombre,
            "direccion" to direccion,
            "telefono" to telefono
        )

        // Actualiza en la base de datos
        db.child(uid).updateChildren(actualizaciones).addOnSuccessListener {
            Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al guardar cambios", Toast.LENGTH_SHORT).show()
        }
    }
}
