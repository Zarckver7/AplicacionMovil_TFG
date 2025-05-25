// Paquete donde se encuentra esta clase
package com.example.aurumverus.Cliente

// Importaciones necesarias
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
import com.example.aurumverus.*
import com.example.aurumverus.Cliente.Botton_Nav_Fragments_Cliente.*
import com.example.aurumverus.Cliente.Nav_Fragment_Cliente.*
import com.example.aurumverus.databinding.ActivityMainClienteBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

// Esta actividad es la pantalla principal para el cliente, con menú lateral
class MainActivityCliente : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Enlace al layout XML usando View Binding
    private lateinit var binding: ActivityMainClienteBinding

    // Objeto de autenticación Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // Se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cambia el color de la barra de estado si el dispositivo lo permite
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        // Configura el diseño de la pantalla
        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Usa la barra de herramientas (toolbar) definida en el layout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Inicializa Firebase Auth y verifica si hay sesión iniciada
        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        // Asocia el evento de clic al menú lateral
        binding.navigationView.setNavigationItemSelectedListener(this)

        // Crea un botón de menú lateral (hamburguesa)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        // Activa el menú lateral
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Muestra el fragmento de inicio por defecto
        replaceFragment(FragmentInicioCliente())
        binding.navigationView.setCheckedItem(R.id.op_inicio_c)
    }

    // Verifica si el usuario está logueado
    private fun comprobarSesion() {
        if (firebaseAuth.currentUser == null) {
            // Si no está logueado, vuelve a la pantalla de selección de tipo de usuario
            startActivity(Intent(this, SeleccionUsuarioActivity::class.java))
            finishAffinity()
        } else {
            Toast.makeText(applicationContext, "Bienvenido", Toast.LENGTH_SHORT).show()
        }
    }

    // Cierra la sesión y redirige al inicio
    private fun cerrarSeseion() {
        firebaseAuth.signOut()
        startActivity(Intent(this, SeleccionUsuarioActivity::class.java))
        finishAffinity()
        Toast.makeText(applicationContext, "La sesión ha cerrado", Toast.LENGTH_SHORT).show()
    }

    // Cambia el fragmento (pantalla interna) mostrado dentro de esta actividad
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment, fragment)
            .commit()
    }

    // Manejador de clics en las opciones del menú lateral
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.op_inicio_c -> {
                replaceFragment(FragmentInicioCliente())
            }
            R.id.op_mi_perfil_c -> {
                replaceFragment(FragmentMiPerfilCliente())
            }
            R.id.op_soporte -> {
                enviarCorreoSoporte()
            }
            R.id.op_cerrar_seseion_c -> {
                cerrarSeseion()
            }
            R.id.op_tienda_c -> {
                replaceFragment(FragmentTiendaCliente())
            }
            R.id.op_mis_pedidos_c -> {
                replaceFragment(FragmentMisPedidosCliente())
            }
        }

        // Cierra el menú lateral después de seleccionar una opción
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Lanza una app de correo para contactar soporte
    private fun enviarCorreoSoporte() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:aurumverus.info@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "Consulta desde la app (cliente)")
        }

        try {
            startActivity(Intent.createChooser(intent, "Enviar correo con..."))
        } catch (e: Exception) {
            Toast.makeText(this, "No se encontró una app de correo", Toast.LENGTH_SHORT).show()
        }
    }
}
