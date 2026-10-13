package com.example.pushiote

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class ModificarUsuarioActivity : AppCompatActivity() {

    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button

    private var posicionUsuario = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_usuario)

        etNombres = findViewById(R.id.etModificarNombres)
        etApellidos = findViewById(R.id.etModificarApellidos)
        etEmail = findViewById(R.id.etModificarEmail)

        btnGuardar = findViewById(R.id.btnGuardarCambios)
        btnEliminar = findViewById(R.id.btnEliminarUsuario)

        posicionUsuario =
            intent.getIntExtra("posicion", -1)

        cargarUsuario()

        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        btnEliminar.setOnClickListener {
            confirmarEliminacion()
        }
    }

    private fun cargarUsuario() {

        if (
            posicionUsuario < 0 ||
            posicionUsuario >= UsuarioRepository.usuarios.size
        ) {
            finish()
            return
        }

        val usuario =
            UsuarioRepository.usuarios[posicionUsuario]

        etNombres.setText(usuario.nombres)
        etApellidos.setText(usuario.apellidos)
        etEmail.setText(usuario.email)
    }

    private fun guardarCambios() {

        val nombres = etNombres.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val email = etEmail.text.toString().trim()

        if (
            nombres.isEmpty() ||
            apellidos.isEmpty() ||
            email.isEmpty()
        ) {
            SweetAlertDialog(
                this,
                SweetAlertDialog.WARNING_TYPE
            )
                .setTitleText("Campos obligatorios")
                .setContentText("Debe completar todos los campos.")
                .setConfirmText("Aceptar")
                .show()

            return
        }

        val soloLetras =
            Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")

        if (
            !soloLetras.matches(nombres) ||
            !soloLetras.matches(apellidos)
        ) {
            SweetAlertDialog(
                this,
                SweetAlertDialog.ERROR_TYPE
            )
                .setTitleText("Formato inválido")
                .setContentText(
                    "Nombres y apellidos solo pueden contener letras y espacios."
                )
                .setConfirmText("Aceptar")
                .show()

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

            return
        }

        val usuario =
            UsuarioRepository.usuarios[posicionUsuario]

        usuario.nombres = nombres
        usuario.apellidos = apellidos
        usuario.email = email

        SweetAlertDialog(
            this,
            SweetAlertDialog.SUCCESS_TYPE
        )
            .setTitleText("Usuario actualizado")
            .setContentText(
                "Los cambios fueron guardados correctamente."
            )
            .setConfirmText("Aceptar")
            .setConfirmClickListener {
                it.dismissWithAnimation()
                finish()
            }
            .show()
    }

    private fun confirmarEliminacion() {

        SweetAlertDialog(
            this,
            SweetAlertDialog.WARNING_TYPE
        )
            .setTitleText("Eliminar usuario")
            .setContentText(
                "¿Está seguro de que desea eliminar este usuario?"
            )
            .setConfirmText("Sí, eliminar")
            .setCancelText("Cancelar")
            .setConfirmClickListener { alerta ->

                UsuarioRepository.usuarios.removeAt(
                    posicionUsuario
                )

                alerta.changeAlertType(
                    SweetAlertDialog.SUCCESS_TYPE
                )

                alerta.titleText = "Usuario eliminado"

                alerta.contentText =
                    "El usuario fue eliminado correctamente."

                alerta.setConfirmText("Aceptar")

                alerta.setConfirmClickListener {
                    it.dismissWithAnimation()
                    finish()
                }
            }
            .show()
    }
}