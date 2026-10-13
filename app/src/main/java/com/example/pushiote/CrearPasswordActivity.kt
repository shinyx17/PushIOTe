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

class CrearPasswordActivity : AppCompatActivity() {

    private lateinit var etNuevaPassword: EditText
    private lateinit var etConfirmarPassword: EditText
    private lateinit var btnCambiarPassword: Button
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_password)

        email = intent.getStringExtra("email")

        etNuevaPassword = findViewById(R.id.etNuevaPassword)
        etConfirmarPassword = findViewById(R.id.etConfirmarPassword)
        btnCambiarPassword = findViewById(R.id.btnCambiarPassword)

        btnCambiarPassword.setOnClickListener {
            val pass1 = etNuevaPassword.text.toString().trim()
            val pass2 = etConfirmarPassword.text.toString().trim()

            if (pass1.isEmpty() || pass2.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Atención")
                    .setContentText("Completa ambos campos.")
                    .show()
                return@setOnClickListener
            }

            if (pass1 != pass2) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Las contraseñas no coinciden.")
                    .show()
                return@setOnClickListener
            }

            actualizarPasswordServidor(email ?: "", pass1)
        }
    }

    private fun actualizarPasswordServidor(email: String, nuevaPass: String) {
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Actualizando..."
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
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Éxito!")
                            .setContentText("Contraseña actualizada correctamente.")
                            .setConfirmClickListener {
                                it.dismissWithAnimation()
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
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
                        .setContentText("Respuesta del servidor no válida.")
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
                params["accion"] = "cambiar_password"
                params["email"] = email
                params["password"] = nuevaPass
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}