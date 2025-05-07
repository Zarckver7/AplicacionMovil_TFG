package com.example.aurumverus.Cliente.Nav_Fragment_Cliente

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aurumverus.Cliente.Botton_Nav_Fragments_Cliente.FragmentMisPedidosCliente
import com.example.aurumverus.Cliente.Botton_Nav_Fragments_Cliente.FragmentTiendaCliente
import com.example.aurumverus.R
import com.example.aurumverus.databinding.FragmentInicioClienteBinding


class FragmentInicioCliente : Fragment() {

    private lateinit var binding: FragmentInicioClienteBinding

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = FragmentInicioClienteBinding.inflate(inflater, container, false)

    binding.bottonNavigation.setOnItemSelectedListener {
        when(it.itemId){
            R.id.op_tienda_c -> {
                replaceFragment(FragmentTiendaCliente())
            }
            R.id.op_mis_pedidos_c -> {
                replaceFragment(FragmentMisPedidosCliente())
            }
        }
        true
    }
        replaceFragment(FragmentTiendaCliente())
        binding.bottonNavigation.selectedItemId = R.id.op_tienda_c

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment, fragment)
            .commit()
    }


}