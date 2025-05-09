package com.example.aurumverus.Vendedor.Bottom_Nav_Fragments_Vendedor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aurumverus.R
import com.example.aurumverus.modelos.Producto
import com.example.aurumverus.Adaptadores.ProductosAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentMisProductosVendedor : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productosList: ArrayList<Producto>
    private lateinit var adapter: ProductosAdapter
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_productos_vendedor, container, false)

        recyclerView = view.findViewById(R.id.productosRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        productosList = ArrayList()
        adapter = ProductosAdapter(productosList)
        recyclerView.adapter = adapter

        firebaseAuth = FirebaseAuth.getInstance()
        cargarMisProductos()

        return view
    }

    private fun cargarMisProductos() {
        val uid = firebaseAuth.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("Productos")

        ref.orderByChild("uid").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productosList.clear()
                    for (ds in snapshot.children) {
                        val producto = ds.getValue(Producto::class.java)
                        producto?.let { productosList.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
