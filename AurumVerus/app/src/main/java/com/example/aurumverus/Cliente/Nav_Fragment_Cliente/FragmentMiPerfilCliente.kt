package com.example.aurumverus.Cliente.Nav_Fragment_Cliente

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

    private lateinit var progressDialog: ProgressDialog

    private lateinit var edtNombre: EditText
    private lateinit var edtDireccion: EditText
    private lateinit var edtTelefono: EditText
    private lateinit var btnGuardar: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("Usuarios")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_mi_perfil_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        edtNombre = view.findViewById(R.id.edtNombrePerfil)
        edtDireccion = view.findViewById(R.id.edtDireccionPerfil)
        edtTelefono = view.findViewById(R.id.edtTelefonoPerfil)
        btnGuardar = view.findViewById(R.id.btnGuardarPerfil)

        cargarDatosPerfil()

        btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

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

    private fun guardarCambios() {
        val uid = auth.uid ?: return
        val nombre = edtNombre.text.toString().trim()
        val direccion = edtDireccion.text.toString().trim()
        val telefono = edtTelefono.text.toString().trim()

        if (nombre.isEmpty()) {
            edtNombre.error = "El nombre no puede estar vacío"
            edtNombre.requestFocus()
            return
        }else if (telefono.isNotBlank()) {
            if (!telefono.matches(Regex("^\\d{9}$"))) {
                edtTelefono.error = "Debe tener exactamente 9 dígitos"
                edtTelefono.requestFocus()
                return
            }
        }



        val actualizaciones = mapOf(
            "nombre" to nombre,
            "direccion" to direccion,
            "telefono" to telefono
        )

        db.child(uid).updateChildren(actualizaciones).addOnSuccessListener {
            Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al guardar cambios", Toast.LENGTH_SHORT).show()
        }
    }

}
