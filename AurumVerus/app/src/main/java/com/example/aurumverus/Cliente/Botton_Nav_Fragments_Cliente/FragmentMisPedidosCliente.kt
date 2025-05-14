package com.example.aurumverus.Cliente.Botton_Nav_Fragments_Cliente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aurumverus.Adaptadores.AdaptadorPedidoCliente
import com.example.aurumverus.R
import com.example.aurumverus.modelos.Pedido
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentMisPedidosCliente : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: AdaptadorPedidoCliente
    private val listaPedidos = ArrayList<Pedido>()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mis_pedidos_cliente, container, false)
        recyclerView = view.findViewById(R.id.recyclerPedidosCliente)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adaptador = AdaptadorPedidoCliente(listaPedidos)
        recyclerView.adapter = adaptador

        cargarPedidos()

        return view
    }

    private fun cargarPedidos() {
        val uid = auth.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("Pedidos")
        ref.orderByChild("uidCliente").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaPedidos.clear()
                    for (pedidoSnap in snapshot.children) {
                        val pedido = pedidoSnap.getValue(Pedido::class.java)
                        pedido?.let { listaPedidos.add(it) }
                    }
                    adaptador.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar pedidos", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
