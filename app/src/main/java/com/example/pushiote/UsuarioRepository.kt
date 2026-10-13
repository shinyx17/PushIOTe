package com.example.pushiote

data class Usuario(
    var nombres: String,
    var apellidos: String,
    var email: String
)

object UsuarioRepository {

    val usuarios = mutableListOf(
        Usuario("Erik", "Lopez", "erik@gmail.com"),
        Usuario("María", "González", "maria@gmail.com"),
        Usuario("Pedro", "Ramírez", "pedro@gmail.com"),
        Usuario("Camila", "Soto", "camila@gmail.com")
    )
}