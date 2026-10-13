package com.example.pushiote

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog
import org.json.JSONObject

class SensoresActivity : AppCompatActivity() {

    private lateinit var tvEstadoConexion: TextView
    private lateinit var tvTemperatura: TextView
    private lateinit var tvHumedad: TextView
    private lateinit var tvDetalleSensors: TextView
    private lateinit var btnActualizarSensores: Button
    private lateinit var btnVolverMenu: Button
    private lateinit var btnLinterna: Button
    private lateinit var btnAmpolleta: Button

    private var isTorchOn = false
    private var isBulbOn = false
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensores)

        tvEstadoConexion = findViewById(R.id.tvEstadoConexion)
        tvTemperatura = findViewById(R.id.tvTemperatura)
        tvHumedad = findViewById(R.id.tvHumedad)
        tvDetalleSensors = findViewById(R.id.tvDetalleSensors)
        btnActualizarSensores = findViewById(R.id.btnActualizarSensores)
        btnVolverMenu = findViewById(R.id.btnVolverMenu)
        btnLinterna = findViewById(R.id.btnLinterna)
        btnAmpolleta = findViewById(R.id.btnAmpolleta)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList.firstOrNull()
        } catch (_: Exception) {}

        btnLinterna.setOnClickListener {
            toggleLinterna()
        }

        btnAmpolleta.setOnClickListener {
            toggleAmpolleta()
        }

        btnActualizarSensores.setOnClickListener {
            obtenerDatosSensores()
        }

        btnVolverMenu.setOnClickListener {
            finish()
        }

        // Cargar primera lectura al iniciar
        obtenerDatosSensores()
    }

    private fun toggleLinterna() {
        if (cameraId == null) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Sin soporte")
                .setContentText("Este dispositivo no cuenta con flash de cámara.")
                .show()
            return
        }

        try {
            isTorchOn = !isTorchOn
            cameraManager.setTorchMode(cameraId!!, isTorchOn)
            if (isTorchOn) {
                btnLinterna.text = "LINTERNA: ON"
                btnLinterna.setBackgroundColor(android.graphics.Color.parseColor("#2EF29B"))
            } else {
                btnLinterna.text = "LINTERNA: OFF"
                btnLinterna.setBackgroundColor(android.graphics.Color.parseColor("#FFC107"))
            }
        } catch (e: Exception) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error de Hardware")
                .setContentText("No se pudo encender la linterna: ${e.localizedMessage}")
                .show()
            isTorchOn = !isTorchOn
        }
    }

    private fun toggleAmpolleta() {
        isBulbOn = !isBulbOn
        if (isBulbOn) {
            btnAmpolleta.text = "AMPOLLETA: ENCENDIDA"
            btnAmpolleta.setBackgroundColor(android.graphics.Color.parseColor("#FFEB3B"))
            btnAmpolleta.setTextColor(android.graphics.Color.parseColor("#000000"))
        } else {
            btnAmpolleta.text = "AMPOLLETA: APAGADA"
            btnAmpolleta.setBackgroundColor(android.graphics.Color.parseColor("#000000"))
            btnAmpolleta.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        }
    }

    private fun obtenerDatosSensores() {
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Obteniendo datos..."
        pDialog.setCancelable(false)
        pDialog.show()

        val stringRequest = StringRequest(
            Request.Method.GET, Config.URL_SENSORES,
            { response ->
                pDialog.dismissWithAnimation()
                tvEstadoConexion.text = "Estado: Conectado"

                try {
                    val jsonResponse = JSONObject(response)

                    val temp = jsonResponse.optString("temperatura", jsonResponse.optString("temp", "N/A"))
                    val hum = jsonResponse.optString("humedad", jsonResponse.optString("hum", "N/A"))

                    tvTemperatura.text = if (temp != "N/A") "$temp °C" else "N/A"
                    tvHumedad.text = if (hum != "N/A") "$hum %" else "N/A"
                    tvDetalleSensors.text = jsonResponse.toString(2)

                } catch (_: Exception) {
                    // Si no es un JSON puro y la API retorna texto plano / HTML
                    tvTemperatura.text = "OK"
                    tvHumedad.text = "OK"
                    tvDetalleSensors.text = response.trim()
                }
            },
            { _ ->
                pDialog.dismissWithAnimation()
                tvEstadoConexion.text = "Estado: Error de conexión"
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de red")
                    .setContentText("No se pudo obtener respuesta de la API pnk.cl")
                    .show()
            }
        )

        Volley.newRequestQueue(this).add(stringRequest)
    }
}
