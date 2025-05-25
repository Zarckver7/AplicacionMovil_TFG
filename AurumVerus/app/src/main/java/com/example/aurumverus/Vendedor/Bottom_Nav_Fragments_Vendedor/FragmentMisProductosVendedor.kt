package com.example.aurumverus.Vendedor.Bottom_Nav_Fragments_Vendedor

// Importaciones necesarias
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aurumverus.R
import com.example.aurumverus.modelos.Producto
import com.example.aurumverus.Adaptadores.AdaptadorProductosVendedor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// Fragmento que muestra todos los productos del vendedor actual
class FragmentMisProductosVendedor : Fragment() {

    // Elementos y variables necesarias
    private lateinit var recyclerView: RecyclerView
    private lateinit var productosList: ArrayList<Producto>
    private lateinit var adapter: AdaptadorProductosVendedor
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Carga el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_mis_productos_vendedor, container, false)

        // Configura el RecyclerView
        recyclerView = view.findViewById(R.id.productosRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        productosList = ArrayList()
        adapter = AdaptadorProductosVendedor(productosList)
        recyclerView.adapter = adapter

        firebaseAuth = FirebaseAuth.getInstance()
        cargarMisProductos()

        return view
    }

    // Carga desde Firebase solo los productos creados por este vendedor
    private fun cargarMisProductos() {
        val uid = firebaseAuth.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("Productos")

        // Filtra los productos por el UID del vendedor
        ref.orderByChild("uid").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productosList.clear()
                    for (ds in snapshot.children) {
                        val producto = ds.getValue(Producto::class.java)
                        producto?.let { productosList.add(it) }
                    }
                    adapter.notifyDataSetChanged() // Refresca la vista
                }

                override fun onCancelled(error: DatabaseError) {
                    // No implementado, pero puede usarse para mostrar un mensaje de error
                }
            })
    }
}
