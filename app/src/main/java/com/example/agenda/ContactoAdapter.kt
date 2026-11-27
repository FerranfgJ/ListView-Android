package com.example.agenda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ContactoAdapter(context: Context, contactos: ArrayList<Contacto>) :
    ArrayAdapter<Contacto>(context, R.layout.item_contacto, contactos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var fila = convertView
        if (fila == null) {
            fila = LayoutInflater.from(context).inflate(R.layout.item_contacto, parent, false)
        }

        val contacto = getItem(position)
        if (contacto != null) {
            val nombre = fila.findViewById<TextView>(R.id.txtNombre)
            val subtitulo = fila.findViewById<TextView>(R.id.txtTelefono)
            nombre.text = contacto.nombreCompleto()

            if (contacto.telefono.isNotEmpty()) {
                subtitulo.text = contacto.telefono
            } else if (contacto.correo.isNotEmpty()) {
                subtitulo.text = contacto.correo
            } else {
                subtitulo.setText(R.string.sin_telefono_ni_email)
            }
        }

        return fila
    }
}
