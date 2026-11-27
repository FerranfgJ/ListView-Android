package com.example.agenda

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ContactoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle)

        val contacto = IntentCompat.getSerializableExtra(intent, EXTRA_CONTACTO, Contacto::class.java)
        val posicion = intent.getIntExtra(EXTRA_POSICION, -1)
        val nombre = findViewById<EditText>(R.id.edtNombre)
        val apellido = findViewById<EditText>(R.id.edtApellido)
        val telefono = findViewById<EditText>(R.id.edtTelefono)
        val correo = findViewById<EditText>(R.id.edtEmail)
        val direccion = findViewById<EditText>(R.id.edtDireccion)
        val fecha = findViewById<EditText>(R.id.edtFechaNacimiento)
        val eliminar = findViewById<Button>(R.id.btnEliminar)

        if (contacto == null) {
            findViewById<TextView>(R.id.txtDatosContacto).setText(R.string.agregar_contacto)
            eliminar.visibility = View.GONE
        } else {
            if (savedInstanceState == null) {
                nombre.setText(contacto.nombre)
                apellido.setText(contacto.apellido)
                telefono.setText(contacto.telefono)
                correo.setText(contacto.correo)
                direccion.setText(contacto.direccion)
                fecha.setText(contacto.fechaN)
            }
            eliminar.setOnClickListener { confirmarEliminacion(contacto, posicion) }
        }

        findViewById<Button>(R.id.btnAceptar).setOnClickListener {
            val nombreIntroducido = nombre.text.toString().trim()
            if (nombreIntroducido.isEmpty()) {
                nombre.error = getString(R.string.nombre_obligatorio)
                nombre.requestFocus()
            } else {
                val datos = Contacto(
                    nombre = nombreIntroducido,
                    apellido = apellido.text.toString().trim(),
                    telefono = telefono.text.toString().trim(),
                    correo = correo.text.toString().trim(),
                    direccion = direccion.text.toString().trim(),
                    fechaN = fecha.text.toString().trim()
                )
                val resultado = Intent()
                resultado.putExtra(EXTRA_CONTACTO, datos)
                resultado.putExtra(EXTRA_POSICION, posicion)
                setResult(RESULT_OK, resultado)
                finish()
            }
        }

        findViewById<Button>(R.id.btnCancelar).setOnClickListener { finish() }

        findViewById<ImageButton>(R.id.btnLlamar).setOnClickListener {
            abrirAplicacion(telefono, Intent.ACTION_DIAL, "tel", R.string.telefono_vacio, R.string.sin_marcador)
        }
        findViewById<ImageButton>(R.id.btnEnviarCorreo).setOnClickListener {
            abrirAplicacion(correo, Intent.ACTION_SENDTO, "mailto", R.string.email_vacio, R.string.sin_app_email)
        }

        ajustarMargenes()
    }

    private fun ajustarMargenes() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.detalle)) { vista, insets ->
            val margenes = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or
                    WindowInsetsCompat.Type.ime()
            )
            vista.setPadding(margenes.left, margenes.top, margenes.right, margenes.bottom)
            insets
        }
    }

    private fun confirmarEliminacion(contacto: Contacto, posicion: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.eliminar_contacto)
            .setMessage(getString(R.string.confirmar_eliminar, contacto.nombreCompleto()))
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                val resultado = Intent()
                resultado.putExtra(EXTRA_POSICION, posicion)
                resultado.putExtra(EXTRA_ELIMINAR, true)
                setResult(RESULT_OK, resultado)
                finish()
            }
            .show()
    }

    private fun abrirAplicacion(campo: EditText, accion: String, esquema: String, campoVacio: Int, sinApp: Int) {
        val destinatario = campo.text.toString().trim()
        if (destinatario.isEmpty()) {
            campo.error = getString(campoVacio)
            campo.requestFocus()
            return
        }

        try {
            val direccion = Uri.fromParts(esquema, destinatario, null)
            val intent = Intent(accion, direccion)
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, sinApp, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_CONTACTO = "com.example.agenda.CONTACTO"
        const val EXTRA_POSICION = "com.example.agenda.POSICION"
        const val EXTRA_ELIMINAR = "com.example.agenda.ELIMINAR"
    }
}
