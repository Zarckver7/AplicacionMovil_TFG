package com.example.aurumverus.Vendedor.Productos

import android.app.Activity
import android.app.ProgressDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.aurumverus.Adaptadores.AdaptadorImagenSeleccionada
import com.example.aurumverus.Constantes
import com.example.aurumverus.ImagenSeleccionada.ImagenSeleccionada
import com.example.aurumverus.R
import com.example.aurumverus.databinding.ActivityAgregarProductoBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarProductoBinding
    private lateinit var progressDialog: ProgressDialog
    private var imagenUri: Uri? = null

    private lateinit var imagenSeleccionadaArrayList: ArrayList<ImagenSeleccionada>
    private lateinit var adaptadorImagenSeleccionada: AdaptadorImagenSeleccionada
    private lateinit var firebaseAuth: FirebaseAuth

    private var nombreP = ""
    private var descripcionP = ""
    private var precioP = ""
    private var categoriaP = ""
    private var nombreVendedor = ""
    private var horaBD = Constantes().tiempoD()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        imagenSeleccionadaArrayList = ArrayList()
        cargarImagenes()

        setupSpinner()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.imgAgregarProd.setOnClickListener {
            seleccionarImagen()
        }

        binding.btnAgregarProd.setOnClickListener {
            validarInformacion()
        }
    }

    private fun setupSpinner() {
        val categorias = listOf(
            "Ropa", "Electrónica", "Comida", "Accesorios", "Hogar",
            "Bolsos y mochilas", "Joyería", "Consolas y videojuegos", "Perfumería", "Libros", "Otros"
        )

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item_dropdown,
            categorias
        )

        binding.autoCategoria.setAdapter(adapter)

        binding.autoCategoria.setOnTouchListener { _, _ ->
            binding.autoCategoria.showDropDown()
            false // permite que el clic se propague normalmente
        }



    }

    private fun cargarImagenes() {
        adaptadorImagenSeleccionada = AdaptadorImagenSeleccionada(this, imagenSeleccionadaArrayList)
        binding.imagenesProductos.adapter = adaptadorImagenSeleccionada
    }

    private fun seleccionarImagen() {
        ImagePicker.with(this)
            .crop()
            .compress(maxSize = 1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                resultadoImagen.launch(intent)
            }
    }

    private val resultadoImagen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == Activity.RESULT_OK) {
                val data = resultado.data
                imagenUri = data?.data
                val tiempo = Constantes().tiempoD()

                val modeloImagenSeleccionada = ImagenSeleccionada(tiempo, imagenUri, null, false)
                imagenSeleccionadaArrayList.add(modeloImagenSeleccionada)
                cargarImagenes()
            } else {
                Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }

    private fun validarInformacion() {
        nombreP = binding.edtxNombreProd.text.toString().trim()
        descripcionP = binding.edtxDescripcionProd.text.toString().trim()
        precioP = binding.edtxPrecio.text.toString().trim()
        val categoriaSeleccionada = binding.autoCategoria.text.toString().trim()

        if (nombreP.isEmpty()) {
            binding.edtxNombreProd.error = "Ingrese el nombre del producto"
            binding.edtxNombreProd.requestFocus()
        } else if (descripcionP.isEmpty()) {
            binding.edtxDescripcionProd.error = "Ingrese la descripción del producto"
            binding.edtxDescripcionProd.requestFocus()
        } else if (precioP.isEmpty()) {
            binding.edtxPrecio.error = "Ingrese el precio del producto"
            binding.edtxPrecio.requestFocus()
        }else if (categoriaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show()
            return
        }
        else {
            obtenerNombreVendedorYAgregar()
        }
    }

    private fun obtenerNombreVendedorYAgregar() {
        val uid = firebaseAuth.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    nombreVendedor = snapshot.child("nombre").value?.toString() ?: "Desconocido"
                    agregarProducto(uid)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AgregarProductoActivity, "Error al obtener datos del vendedor", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun agregarProducto(uid: String) {
        progressDialog.setMessage("Agregando producto")
        progressDialog.show()

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        val idProducto = ref.push().key ?: return

        val hashMap = HashMap<String, Any>()
        hashMap["idProducto"] = idProducto
        hashMap["nombre"] = nombreP
        hashMap["descripcion"] = descripcionP
        hashMap["precio"] = precioP
        hashMap["categoria"] = categoriaP
        hashMap["uid"] = uid
        hashMap["nombreVendedor"] = nombreVendedor
        hashMap["horaCreacion"] = horaBD

        ref.child(idProducto)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Producto agregado correctamente", Toast.LENGTH_SHORT).show()
                limpiarCampos()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun limpiarCampos() {
        binding.edtxNombreProd.text.clear()
        binding.edtxDescripcionProd.text.clear()
        binding.edtxPrecio.text.clear()
        binding.autoCategoria.text.clear()
        imagenSeleccionadaArrayList.clear()
        adaptadorImagenSeleccionada.notifyDataSetChanged()
    }
}
