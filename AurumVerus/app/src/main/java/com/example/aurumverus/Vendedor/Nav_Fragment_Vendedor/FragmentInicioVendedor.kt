package com.example.aurumverus.Vendedor.Nav_Fragment_Vendedor

// Importaciones necesarias
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aurumverus.R
import com.example.aurumverus.Vendedor.Bottom_Nav_Fragments_Vendedor.FragmentMisProductosVendedor
import com.example.aurumverus.Vendedor.FragmentPedidosVendedor
import com.example.aurumverus.Vendedor.Productos.AgregarProductoActivity
import com.example.aurumverus.databinding.FragmentInicioVendedorBinding

// Fragmento que actúa como la pantalla de inicio del vendedor
class FragmentInicioVendedor : Fragment() {

    // ViewBinding y contexto local
    private lateinit var binding: FragmentInicioVendedorBinding
    private lateinit var mContext: Context

    // Guarda el contexto cuando el fragmento se adjunta a la actividad
    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Infla el layout XML y asocia con binding
        binding = FragmentInicioVendedorBinding.inflate(inflater, container, false)

        // Configura el menú inferior de navegación
        binding.bottonNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.op_mis_productos_v -> replaceFragment(FragmentMisProductosVendedor()) // Ver productos del vendedor
                R.id.op_pedidos_v -> replaceFragment(FragmentPedidosVendedor()) // Ver pedidos recibidos
            }
            true
        }

        // Selección inicial por defecto: "Mis productos"
        replaceFragment(FragmentMisProductosVendedor())
        binding.bottonNavigation.selectedItemId = R.id.op_mis_productos_v

        // Acción del botón flotante: agregar nuevo producto
        binding.addFab.setOnClickListener {
            startActivity(Intent(context, AgregarProductoActivity::class.java))
        }

        return binding.root
    }

    // Función para cambiar de fragmento dentro de este contenedor
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment, fragment)
            .commit()
    }
}
