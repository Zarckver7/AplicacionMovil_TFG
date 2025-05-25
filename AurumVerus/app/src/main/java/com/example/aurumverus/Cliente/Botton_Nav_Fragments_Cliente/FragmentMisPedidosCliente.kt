// Paquete donde se encuentra este fragmento
package com.example.aurumverus.Cliente.Botton_Nav_Fragments_Cliente

// Importaciones necesarias
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

// Fragmento que muestra los pedidos que hizo el cliente
class FragmentMisPedidosCliente : Fragment() {

    // Elementos de la interfaz
    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: AdaptadorPedidoCliente

    // Lista para almacenar los pedidos del cliente
    private val listaPedidos = ArrayList<Pedido>()

    // Instancia de autenticación para identificar al usuario actual
    private val auth = FirebaseAuth.getInstance()

    // Se ejecuta al crear la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Se infla la vista desde el XML
        val view = inflater.inflate(R.layout.fragment_mis_pedidos_cliente, container, false)

        // Configura el RecyclerView (lista de elementos)
        recyclerView = view.findViewById(R.id.recyclerPedidosCliente)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // disposición vertical

        // Crea el adaptador con la lista vacía al inicio
        adaptador = AdaptadorPedidoCliente(listaPedidos)
        recyclerView.adapter = adaptador

        // Llama a la función para cargar los pedidos desde Firebase
        cargarPedidos()

        return view
    }

    // Función que consulta los pedidos del usuario actual en Firebase
    private fun cargarPedidos() {
        val uid = auth.uid ?: return // Obtiene el ID del usuario logueado

        // Referencia a la base de datos donde están los pedidos
        val ref = FirebaseDatabase.getInstance().getReference("Pedidos")

        // Filtra solo los pedidos que pertenecen a este cliente
        ref.orderByChild("uidCliente").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                // Se ejecuta cuando se obtienen los datos
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaPedidos.clear() // Limpia lista previa
                    for (pedidoSnap in snapshot.children) {
                        val pedido = pedidoSnap.getValue(Pedido::class.java)
                        pedido?.let { listaPedidos.add(it) } // Agrega a la lista si no es null
                    }
                    adaptador.notifyDataSetChanged() // Actualiza la vista con los nuevos datos
                }

                // Se ejecuta si ocurre un error al leer los datos
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar pedidos", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
