package com.example.aurumverus.Cliente.Nav_Fragment_Cliente

// Importaciones necesarias
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aurumverus.Cliente.Botton_Nav_Fragments_Cliente.FragmentMisPedidosCliente
import com.example.aurumverus.Cliente.Botton_Nav_Fragments_Cliente.FragmentTiendaCliente
import com.example.aurumverus.R
import com.example.aurumverus.databinding.FragmentInicioClienteBinding

// Fragmento que actúa como página principal del cliente (inicio)
class FragmentInicioCliente : Fragment() {

    // View Binding para acceder a elementos del layout XML
    private lateinit var binding: FragmentInicioClienteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Carga el diseño y lo enlaza con binding
        binding = FragmentInicioClienteBinding.inflate(inflater, container, false)

        // Configura la barra de navegación inferior
        binding.bottonNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.op_tienda_c -> {
                    replaceFragment(FragmentTiendaCliente()) // Muestra productos
                }
                R.id.op_mis_pedidos_c -> {
                    replaceFragment(FragmentMisPedidosCliente()) // Muestra pedidos
                }
            }
            true
        }

        // Carga por defecto la tienda
        replaceFragment(FragmentTiendaCliente())
        binding.bottonNavigation.selectedItemId = R.id.op_tienda_c

        return binding.root
    }

    // Reemplaza el fragmento actual por el indicado
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment, fragment)
            .commit()
    }
}
