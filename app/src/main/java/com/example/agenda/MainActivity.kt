package com.example.agenda

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ContactoAdapter
    private lateinit var contactos: ArrayList<Contacto>

    private val editor = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        val datos = resultado.data
        if (resultado.resultCode == RESULT_OK && datos != null) {
            actualizarAgenda(datos)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val agenda = ViewModelProvider(this)[AgendaViewModel::class.java]
        contactos = agenda.contactos
        adapter = ContactoAdapter(this, contactos)

        val lista = findViewById<ListView>(R.id.listViewContactos)
        lista.adapter = adapter
        lista.emptyView = findViewById(R.id.txtAgendaVacia)
        lista.setOnItemClickListener { _, _, posicion, _ ->
            val pantalla = Intent(this, ContactoActivity::class.java)
            pantalla.putExtra(ContactoActivity.EXTRA_CONTACTO, contactos[posicion])
            pantalla.putExtra(ContactoActivity.EXTRA_POSICION, posicion)
            editor.launch(pantalla)
        }

        findViewById<Button>(R.id.btnAgregar).setOnClickListener {
            val pantalla = Intent(this, ContactoActivity::class.java)
            editor.launch(pantalla)
        }

        ajustarMargenes()
    }

    private fun actualizarAgenda(datos: Intent) {
        val posicion = datos.getIntExtra(ContactoActivity.EXTRA_POSICION, -1)
        val eliminar = datos.getBooleanExtra(ContactoActivity.EXTRA_ELIMINAR, false)
        if (eliminar) {
            if (posicion in contactos.indices) {
                contactos.removeAt(posicion)
            }
        } else {
            val contacto = IntentCompat.getSerializableExtra(
                datos, ContactoActivity.EXTRA_CONTACTO, Contacto::class.java
            )
            if (contacto != null) {
                if (posicion == -1) {
                    contactos.add(contacto)
                } else if (posicion in contactos.indices) {
                    contactos[posicion] = contacto
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun ajustarMargenes() {
        val contenido = findViewById<View>(R.id.main)
        val izquierda = contenido.paddingLeft
        val arriba = contenido.paddingTop
        val derecha = contenido.paddingRight
        val abajo = contenido.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(contenido) { vista, insets ->
            val margenes = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            vista.setPadding(
                izquierda + margenes.left,
                arriba + margenes.top,
                derecha + margenes.right,
                abajo + margenes.bottom
            )
            insets
        }
    }
}

class AgendaViewModel : ViewModel() {
    // La lista se conserva en memoria si se gira la pantalla.
    val contactos = ArrayList<Contacto>()
}
