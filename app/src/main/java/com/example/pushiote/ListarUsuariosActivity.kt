package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.text.Normalizer

class ListarUsuariosActivity : AppCompatActivity() {

    private lateinit var etBuscarUsuario: EditText
    private lateinit var listViewUsuarios: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private var usuariosFiltrados = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_usuarios)

        etBuscarUsuario = findViewById(R.id.etBuscarUsuario)
        listViewUsuarios = findViewById(R.id.listViewUsuarios)

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            mutableListOf()
        )

        listViewUsuarios.adapter = adapter

        etBuscarUsuario.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                actualizarLista(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        listViewUsuarios.setOnItemClickListener { _, _, position, _ ->

            val usuario = usuariosFiltrados[position]

            val posicionReal =
                UsuarioRepository.usuarios.indexOf(usuario)

            val intent = Intent(
                this,
                ModificarUsuarioActivity::class.java
            )

            intent.putExtra("posicion", posicionReal)

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        actualizarLista(
            etBuscarUsuario.text.toString()
        )
    }

    private fun actualizarLista(busqueda: String) {

        val textoBuscado = normalizar(busqueda)

        usuariosFiltrados =
            UsuarioRepository.usuarios.filter { usuario ->

                val nombreCompleto = normalizar(
                    "${usuario.nombres} ${usuario.apellidos}"
                )

                nombreCompleto.contains(textoBuscado)

            }.toMutableList()

        val textos = usuariosFiltrados.map { usuario ->
            "${usuario.nombres} ${usuario.apellidos} - ${usuario.email}"
        }

        adapter.clear()
        adapter.addAll(textos)
        adapter.notifyDataSetChanged()
    }

    private fun normalizar(texto: String): String {

        return Normalizer.normalize(
            texto.lowercase(),
            Normalizer.Form.NFD
        ).replace("\\p{M}+".toRegex(), "")
    }
}