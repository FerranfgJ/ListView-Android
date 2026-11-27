package com.example.agenda

import java.io.Serializable

class Contacto(
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val correo: String,
    val direccion: String,
    val fechaN: String
) : Serializable {
    fun nombreCompleto(): String {
        if (apellido.isEmpty()) {
            return nombre
        }
        return "$nombre $apellido"
    }
}
