package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog
import org.json.JSONObject

class GestionUsuariosActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etNombre: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnCrear: Button
    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnListar: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios)

        etEmail = findViewById(R.id.etCrudEmail)
        etNombre = findViewById(R.id.etCrudNombre)
        etPassword = findViewById(R.id.etCrudPassword)

        val emailExtra = intent.getStringExtra("email")
        val nombreExtra = intent.getStringExtra("nombre")
        if (!emailExtra.isNullOrEmpty()) {
            etEmail.setText(emailExtra)
        }
        if (!nombreExtra.isNullOrEmpty()) {
            etNombre.setText(nombreExtra)
        }

        btnCrear = findViewById(R.id.btnCrudCrear)
        btnBuscar = findViewById(R.id.btnCrudBuscar)
        btnActualizar = findViewById(R.id.btnCrudActualizar)
        btnEliminar = findViewById(R.id.btnCrudEliminar)
        btnListar = findViewById(R.id.btnCrudListar)
        btnVolver = findViewById(R.id.btnCrudVolver)

        btnCrear.setOnClickListener { ejecutarCrud("crear") }
        btnBuscar.setOnClickListener { ejecutarCrud("buscar") }
        btnActualizar.setOnClickListener { ejecutarCrud("actualizar") }
        btnEliminar.setOnClickListener { ejecutarCrud("eliminar") }
        btnListar.setOnClickListener {
            startActivity(Intent(this, ListarUsuariosActivity::class.java))
        }
        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun ejecutarCrud(accion: String) {
        val email = etEmail.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Atención")
                .setContentText("Ingresa al menos el correo electrónico.")
                .show()
            return
        }

        // Detección de conectividad y modo offline SQLite (Requisitos de Pauta: Puntos 9, 10, 11, 12, 13)
        if (!NetworkUtils.isNetworkAvailable(this)) {
            val db = DatabaseHelper(this)
            db.guardarUsuarioLocal(email, nombre, password)
            db.agregarPendiente(accion, email, nombre, password)

            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Modo Offline (SQLite)")
                .setContentText("Sin conexión a Internet. La operación se guardó localmente en SQLite y se sincronizará automáticamente al recuperar la conexión.")
                .setConfirmText("Aceptar")
                .setConfirmClickListener {
                    it.dismissWithAnimation()
                    limpiarCampos()
                }
                .show()
            return
        }

        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Procesando $accion..."
        pDialog.setCancelable(false)
        pDialog.show()

        val stringRequest = object : StringRequest(
            Request.Method.POST, Config.URL_CRUD_USUARIOS,
            { response ->
                pDialog.dismissWithAnimation()
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.optString("status")
                    val message = jsonResponse.optString("message")

                    if (status == "success") {
                        if (accion == "buscar") {
                            val userObj = jsonResponse.optJSONObject("usuario")
                            if (userObj != null) {
                                etNombre.setText(userObj.optString("nombre"))
                            }
                        } else if (accion == "eliminar" || accion == "crear") {
                            limpiarCampos()
                        }

                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Éxito!")
                            .setContentText(message)
                            .show()
                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText(message)
                            .show()
                    }
                } catch (e: Exception) {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Respuesta del servidor inválida.")
                        .show()
                }
            },
            {
                pDialog.dismissWithAnimation()
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de red")
                    .setContentText("Sin conexión con AWS.")
                    .show()
            }
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

    private fun limpiarCampos() {
        etEmail.setText("")
        etNombre.setText("")
        etPassword.setText("")
    }
}
