package com.example.pushiote

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class RegistroUsuarioActivity : AppCompatActivity() {

    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etEmailRegistro: EditText
    private lateinit var etPasswordRegistro: EditText
    private lateinit var etConfirmarPasswordRegistro: EditText
    private lateinit var btnRegistrarUsuario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)

        etNombres = findViewById(R.id.etNombres)
        etApellidos = findViewById(R.id.etApellidos)
        etEmailRegistro = findViewById(R.id.etEmailRegistro)
        etPasswordRegistro = findViewById(R.id.etPasswordRegistro)
        etConfirmarPasswordRegistro =
            findViewById(R.id.etConfirmarPasswordRegistro)
        btnRegistrarUsuario = findViewById(R.id.btnRegistrarUsuario)

        btnRegistrarUsuario.setOnClickListener {
            validarRegistro()
        }
    }

    private fun validarRegistro() {

        val nombres = etNombres.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val email = etEmailRegistro.text.toString().trim()
        val password = etPasswordRegistro.text.toString()
        val confirmarPassword =
            etConfirmarPasswordRegistro.text.toString()

        // 1. Validar campos vacíos
        if (
            nombres.isEmpty() ||
            apellidos.isEmpty() ||
            email.isEmpty() ||
            password.isEmpty() ||
            confirmarPassword.isEmpty()
        ) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Campos obligatorios")
                .setContentText("Debe completar todos los campos.")
                .setConfirmText("Aceptar")
                .show()

            return
        }

        // 2. Validar nombres y apellidos
        val soloLetras = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")

        if (!soloLetras.matches(nombres)) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Nombre inválido")
                .setContentText(
                    "Los nombres solo pueden contener letras y espacios."
                )
                .setConfirmText("Aceptar")
                .show()

            etNombres.requestFocus()
            return
        }

        if (!soloLetras.matches(apellidos)) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Apellido inválido")
                .setContentText(
                    "Los apellidos solo pueden contener letras y espacios."
                )
                .setConfirmText("Aceptar")
                .show()

            etApellidos.requestFocus()
            return
        }

        // 3. Validar correo
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Correo inválido")
                .setContentText(
                    "Ingrese un correo electrónico válido."
                )
                .setConfirmText("Aceptar")
                .show()

            etEmailRegistro.requestFocus()
            return
        }

        // 4. Validar contraseña robusta
        if (!esPasswordRobusta(password)) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Contraseña débil")
                .setContentText(
                    "Debe tener mínimo 8 caracteres, una mayúscula, " +
                            "una minúscula, un número y un carácter especial."
                )
                .setConfirmText("Aceptar")
                .show()

            etPasswordRegistro.requestFocus()
            return
        }

        // 5. Validar que las contraseñas coincidan
        if (password != confirmarPassword) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Las contraseñas no coinciden")
                .setContentText(
                    "Debe ingresar la misma contraseña en ambos campos."
                )
                .setConfirmText("Aceptar")
                .show()

            etConfirmarPasswordRegistro.requestFocus()
            return
        }

        // 6. Agregar usuario a la lista del CRUD
        UsuarioRepository.usuarios.add(
            Usuario(
                nombres = nombres,
                apellidos = apellidos,
                email = email
            )
        )

        // 7. Mostrar registro exitoso
        mostrarRegistroExitoso()
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

    private fun mostrarRegistroExitoso() {

        btnRegistrarUsuario.isEnabled = false

        SweetAlertDialog(
            this,
            SweetAlertDialog.SUCCESS_TYPE
        )
            .setTitleText("Usuario registrado")
            .setContentText(
                "El usuario fue registrado correctamente."
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
}