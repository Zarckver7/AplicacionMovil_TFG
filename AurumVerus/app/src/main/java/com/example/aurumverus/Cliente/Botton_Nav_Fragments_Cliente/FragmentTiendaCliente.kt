package com.example.aurumverus.Cliente.Botton_Nav_Fragments_Cliente

import AdaptadorProductoCliente
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aurumverus.R
import com.example.aurumverus.modelos.Producto
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*

class FragmentTiendaCliente : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: AdaptadorProductoCliente
    private val listaProductos = ArrayList<Producto>()
    private val listaProductosFiltrada = ArrayList<Producto>()

    private lateinit var searchView: SearchView
    private lateinit var btnFiltros: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tienda_cliente, container, false)

        recyclerView = view.findViewById(R.id.recyclerProductosCliente)
        searchView = view.findViewById(R.id.searchView)
        btnFiltros = view.findViewById(R.id.btnFiltros)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adaptador = AdaptadorProductoCliente(listaProductosFiltrada)
        recyclerView.adapter = adaptador

        configurarBusqueda()
        configurarBotonFiltros()
        cargarProductosDesdeFirebase()

        return view
    }

    private fun configurarBusqueda() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarPorNombre(newText ?: "")
                return true
            }
        })
    }

    private fun configurarBotonFiltros() {
        btnFiltros.setOnClickListener {
            mostrarDialogoFiltros()
        }
    }

    private fun mostrarDialogoFiltros() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottomsheet_filtros, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetView)

        val spinnerCategoria = bottomSheetView.findViewById<Spinner>(R.id.spinnerCategoria)
        val spinnerVendedor = bottomSheetView.findViewById<Spinner>(R.id.spinnerVendedor)
        val precioMin = bottomSheetView.findViewById<EditText>(R.id.precioMin)
        val precioMax = bottomSheetView.findViewById<EditText>(R.id.precioMax)
        val btnAplicar = bottomSheetView.findViewById<Button>(R.id.btnAplicarFiltros)

        val categorias = listaProductos.mapNotNull { it.categoria }.distinct()
        val vendedores = listaProductos.mapNotNull { it.nombreVendedor }.distinct()

        spinnerCategoria.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Todos") + categorias)
        spinnerVendedor.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Todos") + vendedores)

        btnAplicar.setOnClickListener {
            val categoriaSeleccionada = spinnerCategoria.selectedItem.toString()
            val vendedorSeleccionado = spinnerVendedor.selectedItem.toString()
            val min = precioMin.text.toString().toDoubleOrNull()
            val max = precioMax.text.toString().toDoubleOrNull()

            aplicarFiltros(categoriaSeleccionada, vendedorSeleccionado, min, max)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun aplicarFiltros(categoria: String, vendedor: String, precioMin: Double?, precioMax: Double?) {
        val filtrados = listaProductos.filter { producto ->
            val cumpleCategoria = (categoria == "Todos" || producto.categoria == categoria)
            val cumpleVendedor = (vendedor == "Todos" || producto.nombreVendedor == vendedor)
            val precio = producto.precio?.toDoubleOrNull() ?: 0.0
            val cumplePrecioMin = precioMin == null || precio >= precioMin
            val cumplePrecioMax = precioMax == null || precio <= precioMax

            cumpleCategoria && cumpleVendedor && cumplePrecioMin && cumplePrecioMax
        }

        listaProductosFiltrada.clear()
        listaProductosFiltrada.addAll(filtrados)
        adaptador.notifyDataSetChanged()
    }

    private fun cargarProductosDesdeFirebase() {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProductos.clear()
                for (productoSnap in snapshot.children) {
                    val producto = productoSnap.getValue(Producto::class.java)
                    producto?.let { listaProductos.add(it) }
                }

                listaProductosFiltrada.clear()
                listaProductosFiltrada.addAll(listaProductos)
                adaptador.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filtrarPorNombre(query: String) {
        val filtrados = listaProductos.filter {
            it.nombre?.contains(query, ignoreCase = true) == true
        }
        listaProductosFiltrada.clear()
        listaProductosFiltrada.addAll(filtrados)
        adaptador.notifyDataSetChanged()
    }
}
