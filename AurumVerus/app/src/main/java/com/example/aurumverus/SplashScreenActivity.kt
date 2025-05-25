package com.example.aurumverus

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.aurumverus.Cliente.MainActivityCliente
import com.example.aurumverus.Vendedor.MainActivityVendedor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth // Firebase para manejar sesiones de usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Establece el color de la barra superior si la versión lo permite
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.negro_claro)
        }

        setContentView(R.layout.activity_splash_screen)

        firebaseAuth = FirebaseAuth.getInstance() // Inicializa Firebase

        verSplash() // Muestra una pantalla temporal antes de entrar a la app
    }

    private fun verSplash() {
        // Espera 3.5 segundos antes de pasar a la siguiente actividad
        object : CountDownTimer(3540, 1000){
            override fun onTick(millisUntilFinished: Long) { }

            override fun onFinish() {
                comprobarUsuario()
            }
        }.start()
    }

    private fun comprobarUsuario(){
        val firebaseUser = firebaseAuth.currentUser // Verifica si hay usuario activo
        if(firebaseUser == null){
            // Si no hay usuario, lo manda a elegir si es cliente o vendedor
            startActivity(Intent(this, SeleccionUsuarioActivity::class.java))
        } else {
            // Si hay usuario, revisa su tipo en la base de datos
            val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
            reference.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val tipoUsuario = "${snapshot.child("tipoUsuario").value}"

                        // Según el tipo, abre la actividad principal correspondiente
                        if (tipoUsuario == "Vendedor"){
                            startActivity(Intent(this@SplashScreenActivity, MainActivityVendedor::class.java))
                            finishAffinity()
                        } else if (tipoUsuario == "Cliente"){
                            startActivity(Intent(this@SplashScreenActivity, MainActivityCliente::class.java))
                            finishAffinity()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // No implementado
                    }
                })
        }
    }
}

