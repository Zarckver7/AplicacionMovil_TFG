package com.example.aurumverus.Vendedor.Productos

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aurumverus.Adaptadores.AdaptadorImagenEditable
import com.example.aurumverus.Constantes
import com.example.aurumverus.ImagenSeleccionada.ImagenSeleccionada
import com.example.aurumverus.R
import com.example.aurumverus.databinding.ActivityEditarProductoBinding
import com.example.aurumverus.modelos.Producto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.github.dhaval2404.imagepicker.ImagePicker

class EditarProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarProductoBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog

    private lateinit var adapterImagenes: AdaptadorImagenEditable
    private var listaImagenes = ArrayList<ImagenSeleccionada>()

    private var idProducto: String? = null
    private var imagenPortadaUrl: String? = null
    private var productoActual: Producto? = null

    private val resultadoImagen = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data ?: return@registerForActivityResult
            val nuevaImagen = ImagenSeleccionada(
                id = Constantes().tiempoD(),
                imageUri = uri,
                imagenUrl = null,
                deInternet = false,
                esPortada = false
            )
            listaImagenes.add(nuevaImagen)
            adapterImagenes.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        binding = ActivityEditarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Productos")
        storageRef = FirebaseStorage.getInstance()
        progressDialog = ProgressDialog(this).apply {
            setTitle("Cargando")
            setCanceledOnTouchOutside(false)
        }

        idProducto = intent.getStringExtra("productoId")
        if (idProducto.isNullOrEmpty()) {
            Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        setupUI()
        cargarProducto()

        binding.btnAgregarImagen.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent -> resultadoImagen.launch(intent) }
        }

        binding.btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
        binding.btnEliminarProducto.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Estás seguro de que quieres eliminar este producto? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    eliminarProducto()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }


    }

    private fun setupUI() {
        adapterImagenes = AdaptadorImagenEditable(listaImagenes,
            onEliminar = { imagen ->
                listaImagenes.remove(imagen)
                adapterImagenes.notifyDataSetChanged()
            },
            onSeleccionarPortada = { imagen ->
                listaImagenes.forEach { it.esPortada = false }
                imagen.esPortada = true
                adapterImagenes.notifyDataSetChanged()
            }
        )

        binding.rvImagenes.adapter = adapterImagenes
        binding.rvImagenes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val categorias = listOf(
            "Ropa", "Electrónica", "Comida", "Accesorios", "Hogar",
            "Bolsos y mochilas", "Joyería", "Consolas y videojuegos", "Perfumería", "Libros", "Otros"
        )
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)
        binding.spinnerCategoria.setOnTouchListener { _, _ ->
            binding.spinnerCategoria.showDropDown()
            false
        }
        binding.spinnerCategoria.setAdapter(adapterSpinner)
    }

    private fun cargarProducto() {
        progressDialog.setMessage("Cargando producto")
        progressDialog.show()

        databaseRef.child(idProducto!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()
                val producto = snapshot.getValue(Producto::class.java) ?: return
                productoActual = producto

                binding.edNombre.setText(producto.nombre)
                binding.edDescripcion.setText(producto.descripcion)
                binding.edPrecio.setText(producto.precio)
                binding.spinnerCategoria.setText(producto.categoria, false)

                imagenPortadaUrl = producto.imagenPrincipal

                producto.imagenes?.forEachIndexed { index, url ->
                    listaImagenes.add(
                        ImagenSeleccionada(
                            id = "img_$index",
                            imageUri = null,
                            imagenUrl = url,
                            deInternet = true,
                            esPortada = url == producto.imagenPrincipal
                        )
                    )
                }

                adapterImagenes.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(this@EditarProductoActivity, "Error al cargar", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarCambios() {
        val nombre = binding.edNombre.text.toString().trim()
        val descripcion = binding.edDescripcion.text.toString().trim()
        val precio = binding.edPrecio.text.toString().trim()
        val categoria = binding.spinnerCategoria.text.toString().trim()

        if (nombre.isEmpty() || descripcion.isEmpty() || precio.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        progressDialog.setMessage("Guardando cambios...")
        progressDialog.show()

        val nuevasImagenes = listaImagenes.filter { !it.deInternet }
        val urlsAntiguas = listaImagenes.filter { it.deInternet }.mapNotNull { it.imagenUrl }

        if (nuevasImagenes.isEmpty()) {
            actualizarProducto(nombre, descripcion, precio, categoria, urlsAntiguas)
        } else {
            subirNuevasImagenes(nuevasImagenes) { nuevasUrls ->
                val totalUrls = urlsAntiguas + nuevasUrls
                actualizarProducto(nombre, descripcion, precio, categoria, totalUrls)
            }
        }
    }

    private fun subirNuevasImagenes(
        nuevasImagenes: List<ImagenSeleccionada>,
        callback: (List<String>) -> Unit
    ) {
        val urlsSubidas = mutableListOf<String>()
        val total = nuevasImagenes.size
        var subidas = 0

        nuevasImagenes.forEachIndexed { index, imagen ->
            val uri = imagen.imageUri ?: return@forEachIndexed
            val ref = storageRef.reference.child("Productos/${idProducto}_${System.currentTimeMillis()}.jpg")
            ref.putFile(uri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) throw task.exception!!
                    ref.downloadUrl
                }
                .addOnSuccessListener { url ->
                    urlsSubidas.add(url.toString())
                    subidas++
                    if (subidas == total) callback(urlsSubidas)
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error subiendo imágenes", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun actualizarProducto(
        nombre: String,
        descripcion: String,
        precio: String,
        categoria: String,
        imagenesFinales: List<String>
    ) {
        val hashMap = HashMap<String, Any?>()
        hashMap["nombre"] = nombre
        hashMap["descripcion"] = descripcion
        hashMap["precio"] = precio
        hashMap["categoria"] = categoria
        hashMap["imagenes"] = imagenesFinales
        hashMap["imagenPrincipal"] = listaImagenes.find { it.esPortada }?.imagenUrl
            ?: imagenesFinales.firstOrNull()

        databaseRef.child(idProducto!!).updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarProducto() {
        if (idProducto == null) {
            Toast.makeText(this, "ID del producto no válido", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        val refStorage = FirebaseStorage.getInstance().getReference("Productos")

        productoActual?.imagenes?.forEach { url ->
            if (url.isNotEmpty()) {
                FirebaseStorage.getInstance().getReferenceFromUrl(url).delete()
                    .addOnSuccessListener {
                        Log.d("EliminarProducto", "Imagen eliminada correctamente")
                    }
                    .addOnFailureListener {
                        Log.w("EliminarProducto", "No se pudo eliminar la imagen: ${it.message}")
                    }
            }
        }

        ref.child(idProducto!!).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
