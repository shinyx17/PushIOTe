package com.example.pushiote

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DesarrolladorActivity : AppCompatActivity() {

    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desarrollador)

        btnVolver = findViewById(R.id.btnDevVolver)
        btnVolver.setOnClickListener {
            finish()
        }
    }
}