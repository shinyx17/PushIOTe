package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog
import org.json.JSONObject

class MenuPrincipalActivity : AppCompatActivity() {

    private lateinit var btnSensores: Button
    private lateinit var btnCrudUsuarios: Button
    private lateinit var btnDesarrollador: Button
    private lateinit var btnCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        btnSensores = findViewById(R.id.btnSensores)
        btnCrudUsuarios = findViewById(R.id.btnCrudUsuarios)
        btnDesarrollador = findViewById(R.id.btnDesarrollador)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        btnSensores.setOnClickListener {
            val intent = Intent(this, SensoresActivity::class.java)
            startActivity(intent)
        }

        btnCrudUsuarios.setOnClickListener {
            val intent = Intent(this, GestionUsuariosActivity::class.java)
            startActivity(intent)
        }

        btnDesarrollador.setOnClickListener {
            val intent = Intent(this, DesarrolladorActivity::class.java)
            startActivity(intent)
        }

        btnCerrarSesion.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Cerrar Sesión?")
                .setContentText("Serás redirigido a la pantalla de inicio.")
                .setConfirmText("Sí, salir")
                .setCancelText("Cancelar")
                .setConfirmClickListener {
                    it.dismissWithAnimation()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        sincronizarPendientesOffline()
    }

    private fun sincronizarPendientesOffline() {
        if (!NetworkUtils.isNetworkAvailable(this)) return

        val db = DatabaseHelper(this)
        val pendientes = db.obtenerPendientes()
        if (pendientes.isEmpty()) return

        for (item in pendientes) {
            val id = item["id"]?.toInt() ?: continue
            val accion = item["accion"] ?: ""
            val email = item["email"] ?: ""
            val nombre = item["nombre"] ?: ""
            val password = item["password"] ?: ""

            val stringRequest = object : StringRequest(
                Request.Method.POST, Config.URL_CRUD_USUARIOS,
                { response ->
                    try {
                        val json = JSONObject(response)
                        if (json.optString("status") == "success") {
                            // Confirmación del servidor: eliminamos de pendientes locales
                            db.eliminarPendiente(id)
                        }
                    } catch (_: Exception) {}
                },
                { _ -> }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["accion"] = accion
                    params["email"] = email
                    params["nombre"] = nombre
                    params["password"] = password
                    return params
                }
            }
            Volley.newRequestQueue(this).add(stringRequest)
        }
    }
}
