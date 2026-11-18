package com.sigeco.contactos.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contactos",
    indices = [Index(value = ["telefono"], unique = true)]
)
data class Contacto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val telefono: String,
    val nombre: String,
    val apellido: String,
    val email: String
)
