package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuPrincipalActivity : AppCompatActivity() {

    private lateinit var btnGestionUsuarios: Button
    private lateinit var btnSensores: Button
    private lateinit var btnDesarrollador: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        btnGestionUsuarios = findViewById(R.id.btnGestionUsuarios)
        btnSensores = findViewById(R.id.btnSensores)
        btnDesarrollador = findViewById(R.id.btnDesarrollador)

        btnGestionUsuarios.setOnClickListener {
            val intent = Intent(this, GestionUsuariosActivity::class.java)
            startActivity(intent)
        }

        btnSensores.setOnClickListener {
            val intent = Intent(this, SensoresActivity::class.java)
            startActivity(intent)
        }

        btnDesarrollador.setOnClickListener {
            val intent = Intent(this, DesarrolladorActivity::class.java)
            startActivity(intent)
        }
    }
}