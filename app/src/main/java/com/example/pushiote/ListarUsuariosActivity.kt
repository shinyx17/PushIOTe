package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
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

            val intent = Intent(
                this,
                GestionUsuariosActivity::class.java
            )

            intent.putExtra("email", usuario.email)
            intent.putExtra("nombre", usuario.nombres)

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        obtenerUsuariosDesdeAWS()
    }

    private fun obtenerUsuariosDesdeAWS() {
        val stringRequest = object : StringRequest(
            Request.Method.POST, Config.URL_CRUD_USUARIOS,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.optString("status")
                    if (status == "success") {
                        val jsonArray = jsonResponse.getJSONArray("usuarios")
                        UsuarioRepository.usuarios.clear()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val nombre = obj.optString("nombre")
                            val email = obj.optString("email")
                            UsuarioRepository.usuarios.add(Usuario(nombre, "", email))
                        }
                        actualizarLista(etBuscarUsuario.text.toString())
                    }
                } catch (_: Exception) {
                    // Si falla el parseo, se mantiene la lista local
                }
            },
            { _ ->
                // Si falla la red, se mantiene la lista local
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = "listar"
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
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
            if (usuario.apellidos.isNotEmpty()) {
                "${usuario.nombres} ${usuario.apellidos} - ${usuario.email}"
            } else {
                "${usuario.nombres} - ${usuario.email}"
            }
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
