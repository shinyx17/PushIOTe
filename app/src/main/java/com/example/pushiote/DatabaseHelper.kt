package com.example.pushiote

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pushiote_local.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USUARIOS = "usuarios_local"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_SYNC = "sync_queue"
        const val COLUMN_ID = "id"
        const val COLUMN_ACCION = "accion"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsuariosTable = "CREATE TABLE $TABLE_USUARIOS (" +
                "$COLUMN_EMAIL TEXT PRIMARY KEY, " +
                "$COLUMN_NOMBRE TEXT, " +
                "$COLUMN_PASSWORD TEXT)"
        db.execSQL(createUsuariosTable)

        val createSyncTable = "CREATE TABLE $TABLE_SYNC (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_ACCION TEXT, " +
                "$COLUMN_EMAIL TEXT, " +
                "$COLUMN_NOMBRE TEXT, " +
                "$COLUMN_PASSWORD TEXT)"
        db.execSQL(createSyncTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SYNC")
        onCreate(db)
    }

    fun guardarUsuarioLocal(email: String, nombre: String, pass: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_PASSWORD, pass)
        }
        db.insertWithOnConflict(TABLE_USUARIOS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun agregarPendiente(accion: String, email: String, nombre: String, pass: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACCION, accion)
            put(COLUMN_EMAIL, email)
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_PASSWORD, pass)
        }
        db.insert(TABLE_SYNC, null, values)
        db.close()
    }

    fun obtenerPendientes(): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SYNC", null)
        if (cursor.moveToFirst()) {
            do {
                val map = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                    "accion" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCION)),
                    "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    "nombre" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    "password" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                )
                lista.add(map)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun eliminarPendiente(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_SYNC, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }
}
