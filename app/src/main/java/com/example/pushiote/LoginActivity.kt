package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIngresar: Button
    private lateinit var tvRegistrarse: TextView
    private lateinit var tvRecuperarPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnIngresar = findViewById(R.id.btnIngresar)
        tvRegistrarse = findViewById(R.id.tvRegistrarse)
        tvRecuperarPassword = findViewById(R.id.tvRecuperarPassword)

        btnIngresar.setOnClickListener {
            validarLogin()
        }

        tvRegistrarse.setOnClickListener {
            val intent = Intent(
                this,
                RegistroUsuarioActivity::class.java
            )
            startActivity(intent)
        }

        tvRecuperarPassword.setOnClickListener {
            val intent = Intent(
                this,
                RecuperarPasswordActivity::class.java
            )
            startActivity(intent)
        }
    }

    private fun validarLogin() {

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            SweetAlertDialog(
                this,
                SweetAlertDialog.WARNING_TYPE
            )
                .setTitleText("Campo obligatorio")
                .setContentText(
                    "Debe ingresar su correo electrónico."
                )
                .setConfirmText("Aceptar")
                .show()

            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SweetAlertDialog(
                this,
                SweetAlertDialog.ERROR_TYPE
            )
                .setTitleText("Correo inválido")
                .setContentText(
                    "Ingrese un correo electrónico válido."
                )
                .setConfirmText("Aceptar")
                .show()

            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            SweetAlertDialog(
                this,
                SweetAlertDialog.WARNING_TYPE
            )
                .setTitleText("Campo obligatorio")
                .setContentText(
                    "Debe ingresar su contraseña."
                )
                .setConfirmText("Aceptar")
                .show()

            etPassword.requestFocus()
            return
        }

        mostrarLoginExitoso()
    }

    private fun mostrarLoginExitoso() {

        btnIngresar.isEnabled = false

        SweetAlertDialog(
            this,
            SweetAlertDialog.SUCCESS_TYPE
        )
            .setTitleText("¡Ingreso exitoso!")
            .setContentText(
                "Bienvenido a la aplicación."
            )
            .setConfirmText("Continuar")
            .setConfirmClickListener { sweetAlert ->

                sweetAlert.dismissWithAnimation()

                val intent = Intent(
                    this,
                    MenuPrincipalActivity::class.java
                )

                startActivity(intent)
                finish()
            }
            .show()
    }
}