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
import com.google.firebase.storage.FirebaseStorage

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

        val adapter = ArrayAdapter(this, R.layout.spinner_item_dropdown, categorias)
        binding.autoCategoria.setAdapter(adapter)
        binding.autoCategoria.setOnTouchListener { _, _ ->
            binding.autoCategoria.showDropDown()
            false
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
            .createIntent { intent -> resultadoImagen.launch(intent) }
    }

    private val resultadoImagen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == Activity.RESULT_OK) {
                val data = resultado.data
                imagenUri = data?.data
                val tiempo = Constantes().tiempoD()

                val modelo = ImagenSeleccionada(tiempo, imagenUri, null, false)
                imagenSeleccionadaArrayList.add(modelo)
                cargarImagenes()
            } else {
                Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }

    private fun validarInformacion() {
        nombreP = binding.edtxNombreProd.text.toString().trim()
        descripcionP = binding.edtxDescripcionProd.text.toString().trim()
        precioP = binding.edtxPrecio.text.toString().trim()
        categoriaP = binding.autoCategoria.text.toString().trim()

        if (nombreP.isEmpty()) {
            binding.edtxNombreProd.error = "Ingrese el nombre del producto"
        } else if (descripcionP.isEmpty()) {
            binding.edtxDescripcionProd.error = "Ingrese la descripción"
        } else if (precioP.isEmpty()) {
            binding.edtxPrecio.error = "Ingrese el precio"
        } else if (categoriaP.isEmpty()) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show()
        } else {
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
                    subirImagenesYGuardarProducto(uid)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AgregarProductoActivity, "Error al obtener datos del vendedor", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun subirImagenesYGuardarProducto(uid: String) {
        progressDialog.setMessage("Subiendo imágenes...")
        progressDialog.show()

        val refStorage = FirebaseStorage.getInstance().reference.child("Productos")
        val refDB = FirebaseDatabase.getInstance().getReference("Productos")
        val idProducto = refDB.push().key ?: return

        val uploadTasks = mutableListOf<com.google.android.gms.tasks.Task<Uri>>()

        imagenSeleccionadaArrayList.forEachIndexed { index, imagen ->
            val uri = imagen.imageUri ?: return@forEachIndexed

            val nombreImagen = "${idProducto}_${index}_${System.currentTimeMillis()}.jpg"
            val refImg = refStorage.child(nombreImagen)

            val uploadTask = refImg.putFile(uri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception ?: Exception("Error al subir imagen")
                    }
                    refImg.downloadUrl
                }

            uploadTasks.add(uploadTask)
        }

        com.google.android.gms.tasks.Tasks.whenAllSuccess<Uri>(uploadTasks)
            .addOnSuccessListener { urls ->
                val imagenesUrls = urls.map { it.toString() }
                val imagenPrincipal = imagenesUrls.firstOrNull() ?: ""

                val hashMap = HashMap<String, Any>()
                hashMap["idProducto"] = idProducto
                hashMap["nombre"] = nombreP
                hashMap["descripcion"] = descripcionP
                hashMap["precio"] = precioP
                hashMap["categoria"] = categoriaP
                hashMap["uid"] = uid
                hashMap["nombreVendedor"] = nombreVendedor
                hashMap["horaCreacion"] = horaBD
                hashMap["imagenPrincipal"] = imagenPrincipal
                hashMap["imagenes"] = imagenesUrls

                refDB.child(idProducto).setValue(hashMap)
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
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error al subir las imágenes: ${e.message}", Toast.LENGTH_SHORT).show()
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
