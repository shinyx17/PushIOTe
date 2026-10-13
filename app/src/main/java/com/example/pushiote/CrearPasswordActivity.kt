package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class CrearPasswordActivity : AppCompatActivity() {

    private lateinit var etNuevaPassword: EditText
    private lateinit var etConfirmarPassword: EditText
    private lateinit var btnCrearPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_password)

        etNuevaPassword = findViewById(R.id.etNuevaPassword)
        etConfirmarPassword = findViewById(R.id.etConfirmarPassword)
        btnCrearPassword = findViewById(R.id.btnCrearPassword)

        btnCrearPassword.setOnClickListener {
            validarPassword()
        }
    }

    private fun validarPassword() {

        val password = etNuevaPassword.text.toString()
        val confirmar = etConfirmarPassword.text.toString()

        if (password.isEmpty() || confirmar.isEmpty()) {

            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Campos obligatorios")
                .setContentText("Debe completar ambos campos de contraseña.")
                .setConfirmText("Aceptar")
                .show()

            return
        }

        if (!esPasswordRobusta(password)) {

            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Contraseña débil")
                .setContentText(
                    "Debe tener mínimo 8 caracteres, una mayúscula, " +
                            "una minúscula, un número y un carácter especial."
                )
                .setConfirmText("Aceptar")
                .show()

            return
        }

        if (password != confirmar) {

            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Las contraseñas no coinciden")
                .setContentText(
                    "Debe ingresar la misma contraseña en ambos campos."
                )
                .setConfirmText("Aceptar")
                .show()

            return
        }

        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Contraseña creada")
            .setContentText(
                "La nueva contraseña fue creada correctamente."
            )
            .setConfirmText("Ir al Login")
            .setConfirmClickListener { sweetAlert ->

                sweetAlert.dismissWithAnimation()

                val intent = Intent(
                    this,
                    LoginActivity::class.java
                )

                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun esPasswordRobusta(password: String): Boolean {

        val tieneMayuscula =
            password.any { it.isUpperCase() }

        val tieneMinuscula =
            password.any { it.isLowerCase() }

        val tieneNumero =
            password.any { it.isDigit() }

        val tieneEspecial =
            password.any {
                !it.isLetterOrDigit() && !it.isWhitespace()
            }

        return password.length >= 8 &&
                tieneMayuscula &&
                tieneMinuscula &&
                tieneNumero &&
                tieneEspecial
    }
}