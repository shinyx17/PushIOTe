package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog
import org.json.JSONObject
import com.example.pushiote.R

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRecuperar: TextView
    private lateinit var tvRegistrarse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnIngresar)
        tvRecuperar = findViewById(R.id.tvRecuperarPassword)
        tvRegistrarse = findViewById(R.id.tvRegistrarse)

        btnLogin.setOnClickListener {
            validarEIniciarSesion()
        }

        tvRecuperar.setOnClickListener {
            startActivity(Intent(this, RecuperarPasswordActivity::class.java))
        }

        tvRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistroUsuarioActivity::class.java))
        }
    }

    private fun validarEIniciarSesion() {
        val email = etEmail.text.toString().trim()
        val password = etPass.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Atención")
                .setContentText("Por favor ingresa tu correo y contraseña.")
                .show()
            return
        }

        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Verificando credenciales..."
        pDialog.setCancelable(false)
        pDialog.show()

        val stringRequest = object : StringRequest(
            Request.Method.POST, Config.URL_LOGIN,
            { response ->
                pDialog.dismissWithAnimation()
                try {
                    val json = JSONObject(response)
                    val status = json.optString("status")
                    val message = json.optString("message")

                    if (status == "success") {
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Bienvenido!")
                            .setContentText(message)
                            .setConfirmClickListener { dialog ->
                                dialog.dismissWithAnimation()
                                val intent = Intent(this, MenuPrincipalActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .show()
                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Acceso Denegado")
                            .setContentText(message.ifEmpty { "Credenciales incorrectas." })
                            .show()
                    }
                } catch (e: Exception) {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error de Respuesta")
                        .setContentText("Respuesta del servidor no válida:\n$response")
                        .show()
                }
            },
            { error ->
                pDialog.dismissWithAnimation()
                val errorMsg = when (error) {
                    is com.android.volley.TimeoutError -> "Tiempo de espera agotado (Timeout). AWS tardó demasiado en responder."
                    is com.android.volley.NoConnectionError -> "Sin conexión de red o el emulador no puede alcanzar la IP pública."
                    is com.android.volley.ServerError -> "Error del servidor (5xx). Revisa el código PHP."
                    is com.android.volley.NetworkError -> "Error de red. Verifica la conexión a internet."
                    else -> "Error de red / conexión: ${error.localizedMessage ?: error.toString()}"
                }
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de Conexión AWS")
                    .setContentText(errorMsg)
                    .show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["usuario"] = email
                params["correo"] = email
                params["password"] = password
                params["pass"] = password
                params["clave"] = password
                return params
            }
        }

        // Aumentar el timeout a 10 segundos para evitar cortes prematuros en AWS
        stringRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        Volley.newRequestQueue(this).add(stringRequest)
    }
}
