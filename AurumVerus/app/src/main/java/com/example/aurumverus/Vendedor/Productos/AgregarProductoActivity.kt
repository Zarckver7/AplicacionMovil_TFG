package com.example.aurumverus.Vendedor.Productos

import android.app.Activity
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.aurumverus.Adaptadores.AdaptadorImagenSeleccionada
import com.example.aurumverus.Constantes
import com.example.aurumverus.ImagenSeleccionada.ImagenSeleccionada
import com.example.aurumverus.R
import com.example.aurumverus.databinding.ActivityAgregarProductoBinding
import com.example.aurumverus.databinding.ActivityRegistroVendedorBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.FirebaseDatabase

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarProductoBinding
    private lateinit var progressDialog: ProgressDialog
    private var imagenUri : Uri?=null

    private lateinit var imagenSeleccionadaArrayList : ArrayList<ImagenSeleccionada>
    private lateinit var adaptadorImagenSeleccionada : AdaptadorImagenSeleccionada


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagenSeleccionadaArrayList = ArrayList()

        binding.imgAgregarProd.setOnClickListener {
            seleccionarImagen()
        }

        cargarImagenes()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.edtxPrecioDesc.visibility = View.GONE
        binding.edtxNotaDesc.visibility = View.GONE

        binding.switchDesc.setOnCheckedChangeListener {buttonView, isChecked ->
            if (isChecked) {
                binding.edtxPrecioDesc.visibility = View.VISIBLE
                binding.edtxNotaDesc.visibility = View.VISIBLE
            } else {
                binding.edtxPrecioDesc.visibility = View.GONE
                binding.edtxNotaDesc.visibility = View.GONE
            }
        }

        binding.btnAgregarProd.setOnClickListener {
            validarInformacion()
        }

    }

    private fun cargarImagenes() {
        adaptadorImagenSeleccionada = AdaptadorImagenSeleccionada(this, imagenSeleccionadaArrayList)
        binding.imagenesProductos.adapter = adaptadorImagenSeleccionada
    }

    private var nombreP = ""
    private var descripcionP = ""
    private var precioP = ""
    private var switchDescuento = false
    private var precioDesc = ""
    private var notaDesc = ""

    private fun seleccionarImagen() {
        ImagePicker.with(this)
            .crop()
            .compress(maxSize = 1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent->
                resultadoImagen.launch(intent)
            }
    }

    private val resultadoImagen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == Activity.RESULT_OK) {
                val data = resultado.data
                imagenUri = data?.data
                val tiempo = "${Constantes().tiempoD()}"

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
        switchDescuento = binding.switchDesc.isChecked


        if (nombreP.isEmpty()) {
            binding.edtxNombreProd.error = "Ingrese el nombre del producto"
            binding.edtxNombreProd.requestFocus()
        } else if (descripcionP.isEmpty()) {
            binding.edtxDescripcionProd.error = "Ingrese la descripción del producto"
            binding.edtxDescripcionProd.requestFocus()
        } else if (precioP.isEmpty()) {
            binding.edtxPrecio.error = "Ingrese el precio del producto"
            binding.edtxPrecio.requestFocus()
        } else {
            if (switchDescuento) {
                precioDesc = binding.edtxPrecioDesc.text.toString().trim()
                notaDesc = binding.edtxNotaDesc.text.toString().trim()
                if (precioDesc.isEmpty()) {
                    binding.edtxPrecioDesc.error = "Ingrese el precio del descuento"
                    binding.edtxPrecioDesc.requestFocus()
                } else if (notaDesc.isEmpty()) {
                    binding.edtxNotaDesc.error = "Ingrese la nota del descuento"
                    binding.edtxNotaDesc.requestFocus()
                } else {
                    agregarProducto()
                }
            }else {
                precioDesc = "0"
                notaDesc = ""
                agregarProducto()
            }
        }
        }

    private fun agregarProducto() {
        progressDialog.setMessage("Agregando producto")
        progressDialog.show()

        var ref = FirebaseDatabase.getInstance().getReference("Productos")
        val idProducto = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["idProducto"] = "$idProducto"
        hashMap["nombre"] = "$nombreP"
        hashMap["descripcion"] = "$descripcionP"
        hashMap["precio"] = "$precioP"
        hashMap["precioDescuento"] = "$precioDesc"
        hashMap["notaDescuento"] = "$notaDesc"

        ref.child("$idProducto")
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
        binding.edtxPrecioDesc.text.clear()
        binding.edtxNotaDesc.text.clear()
        binding.switchDesc.isChecked = false

    }

}
