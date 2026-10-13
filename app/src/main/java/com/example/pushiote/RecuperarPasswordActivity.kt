package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog
import org.json.JSONObject

class RecuperarPasswordActivity : AppCompatActivity() {

    private lateinit var etEmailRecuperar: EditText
    private lateinit var etCodigo: EditText
    private lateinit var btnEnviarCodigo: Button
    private lateinit var btnValidarCodigo: Button
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_password)

        etEmailRecuperar = findViewById(R.id.etEmailRecuperar)
        etCodigo = findViewById(R.id.etCodigo)
        btnEnviarCodigo = findViewById(R.id.btnEnviarCodigo)
        btnValidarCodigo = findViewById(R.id.btnValidarCodigo)

        btnEnviarCodigo.setOnClickListener {
            val email = etEmailRecuperar.text.toString().trim()
            if (email.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Atención")
                    .setContentText("Ingresa tu correo electrónico registrado.")
                    .show()
            } else {
                solicitarCodigo(email)
            }
        }

        btnValidarCodigo.setOnClickListener {
            val email = etEmailRecuperar.text.toString().trim()
            val codigo = etCodigo.text.toString().trim()

            if (email.isEmpty() || codigo.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Campos incompletos")
                    .setContentText("Debes ingresar el correo y el código recibido.")
                    .show()
            } else {
                validarCodigoServidor(email, codigo)
            }
        }
    }

    private fun solicitarCodigo(email: String) {
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Enviando código..."
        pDialog.setCancelable(false)
        pDialog.show()

        val url = "${Config.URL_SOLICITAR_RECUPERACION}?email=$email&correo=$email&usuario=$email"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                pDialog.dismissWithAnimation()
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.optString("status")
                    val message = jsonResponse.optString("message")

                    if (status == "success") {
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Código enviado")
                            .setContentText(message)
                            .show()

                        iniciarTemporizador()
                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText(message)
                            .show()
                    }
                } catch (_: Exception) {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error de Respuesta")
                        .setContentText("Respuesta no válida del servidor:\n$response")
                        .show()
                }
            },
            {
                pDialog.dismissWithAnimation()
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de red")
                    .setContentText("No se pudo conectar con el servidor.")
                    .show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["correo"] = email
                params["usuario"] = email
                params["user"] = email
                params["mail"] = email
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun iniciarTemporizador() {
        btnEnviarCodigo.isEnabled = false

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val segundos = millisUntilFinished / 1000
                btnEnviarCodigo.text = "Reenviar en ${segundos}s"
            }

            override fun onFinish() {
                btnEnviarCodigo.isEnabled = true
                btnEnviarCodigo.text = "Reenviar Código"
            }
        }.start()
    }

    private fun validarCodigoServidor(email: String, codigo: String) {
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Validando..."
        pDialog.setCancelable(false)
        pDialog.show()

        val url = "${Config.URL_VALIDAR_CODIGO}?email=$email&correo=$email&usuario=$email&codigo=$codigo&code=$codigo"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                pDialog.dismissWithAnimation()
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.optString("status")
                    val message = jsonResponse.optString("message")

                    if (status == "success") {
                        val intent = Intent(this, CrearPasswordActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("codigo", codigo)
                        startActivity(intent)
                        finish()
                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Código incorrecto")
                            .setContentText(message)
                            .show()
                    }
                } catch (_: Exception) {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error de Respuesta")
                        .setContentText("Respuesta no válida del servidor:\n$response")
                        .show()
                }
            },
            {
                pDialog.dismissWithAnimation()
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Error de comunicación.")
                    .show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["correo"] = email
                params["usuario"] = email
                params["user"] = email
                params["mail"] = email
                params["codigo"] = codigo
                params["code"] = codigo
                params["pin"] = codigo
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
