package com.example.aurumverus.Vendedor

// Importaciones
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.aurumverus.R
import com.example.aurumverus.SeleccionUsuarioActivity
import com.example.aurumverus.Vendedor.Bottom_Nav_Fragments_Vendedor.FragmentMisProductosVendedor
import com.example.aurumverus.Vendedor.Nav_Fragment_Vendedor.FragmentInicioVendedor
import com.example.aurumverus.Vendedor.Nav_Fragment_Vendedor.FragmentResena
import com.example.aurumverus.databinding.ActivityMainVendedorBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

// Actividad principal del vendedor
class MainActivityVendedor : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainVendedorBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        binding = ActivityMainVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        // Configura el menú lateral
        binding.navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        replaceFragment(FragmentInicioVendedor())
        binding.navigationView.setCheckedItem(R.id.op_inicio_v)
    }

    // Verifica si hay sesión activa
    private fun comprobarSesion() {
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, SeleccionUsuarioActivity::class.java))
            finishAffinity()
        } else {
            Toast.makeText(applicationContext, "Bienvenido", Toast.LENGTH_SHORT).show()
        }
    }

    // Cierra la sesión y vuelve al menú de selección
    private fun cerrarSeseion() {
        firebaseAuth.signOut()
        startActivity(Intent(this, SeleccionUsuarioActivity::class.java))
        finishAffinity()
        Toast.makeText(applicationContext, "La sesión ha cerrado", Toast.LENGTH_SHORT).show()
    }

    // Cambia entre fragmentos según el ítem del menú
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.op_inicio_v -> replaceFragment(FragmentInicioVendedor())
            R.id.op_resena_v -> replaceFragment(FragmentResena())
            R.id.op_soporte_v -> enviarCorreoSoporte()
            R.id.op_cerrar_seseion_v -> cerrarSeseion()
            R.id.op_mis_productos_v -> replaceFragment(FragmentMisProductosVendedor())
            R.id.op_pedidos_v -> replaceFragment(FragmentPedidosVendedor())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Abre una app de correo para enviar soporte
    private fun enviarCorreoSoporte() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:aurumverus.info@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "Consulta desde la app (vendedor)")
        }
        try {
            startActivity(Intent.createChooser(intent, "Enviar correo con..."))
        } catch (e: Exception) {
            Toast.makeText(this, "No se encontró una app de correo",
                Toast.LENGTH_SHORT).show()
        }
    }

    // Reemplaza el contenido central por otro fragmento
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.navFragment, fragment).commit()
    }
}
