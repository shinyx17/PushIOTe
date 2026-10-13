package com.example.pushiote

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog
import org.json.JSONObject

class RegistroUsuarioActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)

        etNombre = findViewById(R.id.etRegistroNombre)
        etEmail = findViewById(R.id.etRegistroEmail)
        etPassword = findViewById(R.id.etRegistroPassword)
        btnRegistrar = findViewById(R.id.btnRegistroGuardar)
        btnVolver = findViewById(R.id.btnRegistroVolver)

        btnRegistrar.setOnClickListener {
            registrarEnAWS()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun registrarEnAWS() {
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Atención")
                .setContentText("Por favor completa todos los campos.")
                .show()
            return
        }

        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Registrando en AWS..."
        pDialog.setCancelable(false)
        pDialog.show()

        val stringRequest = object : StringRequest(
            Request.Method.POST, Config.URL_CREAR_USUARIO,
            { response ->
                pDialog.dismissWithAnimation()
                try {
                    val json = JSONObject(response)
                    val status = json.optString("status")
                    val message = json.optString("message")

                    if (status == "success") {
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Registro Exitoso!")
                            .setContentText(message)
                            .setConfirmClickListener { dialog ->
                                dialog.dismissWithAnimation()
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
                        .setContentText("Respuesta no válida del servidor.")
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
                params["nombre"] = nombre
                params["email"] = email
                params["password"] = password
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}