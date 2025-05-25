package com.example.aurumverus.Cliente.Botton_Nav_Fragments_Cliente

// Importaciones necesarias
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

    // Elementos visuales
    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: AdaptadorProductoCliente

    // Listas de productos: completa y filtrada
    private val listaProductos = ArrayList<Producto>()
    private val listaProductosFiltrada = ArrayList<Producto>()

    // Barra de búsqueda y botón de filtros
    private lateinit var searchView: SearchView
    private lateinit var btnFiltros: ImageButton

    // Se ejecuta al crear la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tienda_cliente, container, false)

        // Enlaza elementos visuales con variables
        recyclerView = view.findViewById(R.id.recyclerProductosCliente)
        searchView = view.findViewById(R.id.searchView)
        btnFiltros = view.findViewById(R.id.btnFiltros)

        // Configura el RecyclerView (lista vertical)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adaptador = AdaptadorProductoCliente(listaProductosFiltrada)
        recyclerView.adapter = adaptador

        // Configura funciones interactivas
        configurarBusqueda()
        configurarBotonFiltros()
        cargarProductosDesdeFirebase()

        return view
    }

    // Configura la barra de búsqueda para filtrar por nombre
    private fun configurarBusqueda() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarPorNombre(newText ?: "")
                return true
            }
        })
    }

    // Configura el botón de filtros para mostrar el panel inferior
    private fun configurarBotonFiltros() {
        btnFiltros.setOnClickListener {
            mostrarDialogoFiltros()
        }
    }

    // Muestra el panel inferior (BottomSheet) con los filtros
    private fun mostrarDialogoFiltros() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottomsheet_filtros, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetView)

        // Elementos del diálogo
        val spinnerCategoria = bottomSheetView.findViewById<Spinner>(R.id.spinnerCategoria)
        val spinnerVendedor = bottomSheetView.findViewById<Spinner>(R.id.spinnerVendedor)
        val precioMin = bottomSheetView.findViewById<EditText>(R.id.precioMin)
        val precioMax = bottomSheetView.findViewById<EditText>(R.id.precioMax)
        val btnAplicar = bottomSheetView.findViewById<Button>(R.id.btnAplicarFiltros)

        // Llena los spinners con categorías y vendedores únicos
        val categorias = listaProductos.mapNotNull { it.categoria }.distinct()
        val vendedores = listaProductos.mapNotNull { it.nombreVendedor }.distinct()

        spinnerCategoria.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Todos") + categorias
        )
        spinnerVendedor.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Todos") + vendedores
        )

        // Aplica filtros cuando el usuario hace clic
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

    // Aplica los filtros seleccionados a la lista
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

    // Carga todos los productos desde Firebase Realtime Database
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

    // Filtra productos por nombre desde la barra de búsqueda
    private fun filtrarPorNombre(query: String) {
        val filtrados = listaProductos.filter {
            it.nombre?.contains(query, ignoreCase = true) == true
        }

        listaProductosFiltrada.clear()
        listaProductosFiltrada.addAll(filtrados)
        adaptador.notifyDataSetChanged()
    }
}
