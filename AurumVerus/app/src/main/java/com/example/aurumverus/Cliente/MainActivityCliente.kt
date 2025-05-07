package com.example.aurumverus.Cliente

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.aurumverus.Cliente.Botton_Nav_Fragments_Cliente.FragmentMisPedidosCliente
import com.example.aurumverus.Cliente.Botton_Nav_Fragments_Cliente.FragmentTiendaCliente
import com.example.aurumverus.Cliente.Nav_Fragment_Cliente.FragmentInicioCliente
import com.example.aurumverus.Cliente.Nav_Fragment_Cliente.FragmentMiPerfilCliente
import com.example.aurumverus.R
import com.example.aurumverus.databinding.ActivityMainClienteBinding
import com.google.android.material.navigation.NavigationView

class MainActivityCliente : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainClienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding.navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        replaceFragment(FragmentInicioCliente())

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment, fragment)
            .commit()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.op_inicio_c -> {
                replaceFragment(FragmentInicioCliente())
            }
            R.id.op_mi_perfil_c -> {
                replaceFragment(FragmentMiPerfilCliente())
            }
            R.id.op_cerrar_seseion_c -> {
                Toast.makeText(this, "Has cerrado sesión", Toast.LENGTH_SHORT).show()
            }
            R.id.op_tienda_c -> {
                replaceFragment(FragmentTiendaCliente())
            }
            R.id.op_mis_pedidos_c -> {
                replaceFragment(FragmentMisPedidosCliente())
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}