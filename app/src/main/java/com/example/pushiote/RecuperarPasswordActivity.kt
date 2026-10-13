package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class RecuperarPasswordActivity : AppCompatActivity() {

    private lateinit var etEmailRecuperar: EditText
    private lateinit var btnRecuperar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_password)

        etEmailRecuperar = findViewById(R.id.etEmailRecuperar)
        btnRecuperar = findViewById(R.id.btnRecuperar)

        btnRecuperar.setOnClickListener {
            validarRecuperacion()
        }
    }

    private fun validarRecuperacion() {

        val email = etEmailRecuperar.text.toString().trim()

        if (email.isEmpty()) {

            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Campo obligatorio")
                .setContentText("Debe ingresar su correo electrónico.")
                .setConfirmText("Aceptar")
                .show()

            etEmailRecuperar.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Correo inválido")
                .setContentText("Ingrese un correo electrónico válido.")
                .setConfirmText("Aceptar")
                .show()

            etEmailRecuperar.requestFocus()
            return
        }

        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Correo validado")
            .setContentText("Ahora puede crear una nueva contraseña.")
            .setConfirmText("Continuar")
            .setConfirmClickListener { sweetAlert ->

                sweetAlert.dismissWithAnimation()

                val intent = Intent(
                    this,
                    CrearPasswordActivity::class.java
                )

                startActivity(intent)
            }
            .show()
    }
}