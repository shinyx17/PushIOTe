package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GestionUsuariosActivity : AppCompatActivity() {

    private lateinit var btnIngresarUsuario: Button
    private lateinit var btnListarUsuarios: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios)

        btnIngresarUsuario = findViewById(R.id.btnIngresarUsuario)
        btnListarUsuarios = findViewById(R.id.btnListarUsuarios)

        btnIngresarUsuario.setOnClickListener {
            val intent = Intent(this, RegistroUsuarioActivity::class.java)
            startActivity(intent)
        }

        btnListarUsuarios.setOnClickListener {
            val intent = Intent(this, ListarUsuariosActivity::class.java)
            startActivity(intent)
        }
    }
}