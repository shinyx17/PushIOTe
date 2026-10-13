package com.example.pushiote

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog
import org.json.JSONObject

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
                alerta.dismissWithAnimation()

                if (posicionUsuario >= 0 && posicionUsuario < UsuarioRepository.usuarios.size) {
                    val usuario = UsuarioRepository.usuarios[posicionUsuario]
                    eliminarUsuarioEnAWS(usuario.email)
                } else {
                    finish()
                }
            }
            .show()
    }

    private fun eliminarUsuarioEnAWS(email: String) {
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Eliminando en AWS..."
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
                        if (posicionUsuario >= 0 && posicionUsuario < UsuarioRepository.usuarios.size) {
                            UsuarioRepository.usuarios.removeAt(posicionUsuario)
                        }

                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Usuario eliminado")
                            .setContentText(message.ifEmpty { "El usuario fue eliminado correctamente." })
                            .setConfirmText("Aceptar")
                            .setConfirmClickListener {
                                it.dismissWithAnimation()
                                finish()
                            }
                            .show()
                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText(message)
                            .show()
                    }
                } catch (_: Exception) {
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
                params["accion"] = "eliminar"
                params["email"] = email
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}
