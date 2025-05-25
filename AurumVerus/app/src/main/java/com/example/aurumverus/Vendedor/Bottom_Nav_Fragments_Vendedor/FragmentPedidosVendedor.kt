package com.example.aurumverus.Vendedor

// Importaciones necesarias
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aurumverus.Adaptadores.AdaptadorPedidoVendedor
import com.example.aurumverus.R
import com.example.aurumverus.modelos.Pedido
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// Fragmento que muestra los pedidos recibidos por el vendedor
class FragmentPedidosVendedor : Fragment() {

    // Variables necesarias
    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: AdaptadorPedidoVendedor
    private val listaPedidos = ArrayList<Pedido>()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Infla el layout asociado a este fragmento
        return inflater.inflate(R.layout.fragment_pedidos_vendedor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerPedidosVendedor)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adaptador = AdaptadorPedidoVendedor(listaPedidos)
        recyclerView.adapter = adaptador

        // Carga los pedidos del vendedor actual
        cargarPedidosDelVendedor()
    }

    private fun cargarPedidosDelVendedor() {
        val uidVendedor = auth.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("Pedidos")

        // Filtra los pedidos por el ID del vendedor
        ref.orderByChild("idVendedor").equalTo(uidVendedor)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaPedidos.clear()
                    for (pedidoSnap in snapshot.children) {
                        val pedido = pedidoSnap.getValue(Pedido::class.java)
                        pedido?.let { listaPedidos.add(it) }
                    }
                    adaptador.notifyDataSetChanged() // Refresca el adaptador
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar pedidos", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
