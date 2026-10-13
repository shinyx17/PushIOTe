package com.example.pushiote

object Config {
    private const val BASE_URL = "http://50.16.100.201/"

    const val URL_LOGIN = "${BASE_URL}login.php"
    const val URL_CREAR_USUARIO = "${BASE_URL}crear_usuario.php"
    const val URL_SOLICITAR_RECUPERACION = "${BASE_URL}solicitar_recuperacion.php"
    const val URL_VALIDAR_CODIGO = "${BASE_URL}validar_codigo.php"
    const val URL_CRUD_USUARIOS = "${BASE_URL}crud_usuarios.php"

    const val URL_SENSORES = "http://www.pnk.cl/muestra_datos.php"
}