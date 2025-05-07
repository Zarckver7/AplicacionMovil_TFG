package com.example.aurumverus.Vendedor.Nav_Fragment_Vendedor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.aurumverus.R
import com.example.aurumverus.Vendedor.Bottom_Nav_Fragments_Vendedor.FragmentMisProductosVendedor
import com.example.aurumverus.Vendedor.Bottom_Nav_Fragments_Vendedor.FragmentPedidosVendedor
import com.example.aurumverus.Vendedor.Productos.AgregarProductoActivity
import com.example.aurumverus.databinding.FragmentInicioVendedorBinding


class FragmentInicioVendedor : Fragment() {

    private lateinit var binding : FragmentInicioVendedorBinding
    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentInicioVendedorBinding.inflate(inflater, container, false)
        binding.bottonNavigation.setOnItemSelectedListener  {
            when(it.itemId) {
                R.id.op_mis_productos_v -> {
                    replaceFragment(FragmentMisProductosVendedor())
                }

                R.id.op_pedidos_v -> {
                    replaceFragment(FragmentPedidosVendedor())
                }
            }
            true
        }

        replaceFragment(FragmentMisProductosVendedor())
        binding.bottonNavigation.selectedItemId = R.id.op_mis_productos_v

        binding.addFab.setOnClickListener {
            startActivity(Intent(context, AgregarProductoActivity::class.java))
        }

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment, fragment)
            .commit()
    }

}