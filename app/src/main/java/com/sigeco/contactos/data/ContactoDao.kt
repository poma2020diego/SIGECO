package com.sigeco.contactos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Query("SELECT * FROM contactos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Contacto>>

    @Query("""
        SELECT * FROM contactos
        WHERE nombre LIKE '%' || :texto || '%'
           OR apellido LIKE '%' || :texto || '%'
           OR telefono LIKE '%' || :texto || '%'
           OR email LIKE '%' || :texto || '%'
        ORDER BY nombre ASC
    """)
    fun buscar(texto: String): Flow<List<Contacto>>

    @Query("SELECT * FROM contactos WHERE telefono = :telefono LIMIT 1")
    suspend fun buscarPorTelefono(telefono: String): Contacto?

    @Query("SELECT * FROM contactos WHERE telefono = :telefono AND id != :excludeId LIMIT 1")
    suspend fun buscarTelefonoDuplicado(telefono: String, excludeId: Int): Contacto?

    @Query("SELECT * FROM contactos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Contacto?

    @Insert
    suspend fun insertar(contacto: Contacto)

    @Update
    suspend fun actualizar(contacto: Contacto)

    @Query("DELETE FROM contactos WHERE id IN (:ids)")
    suspend fun eliminarPorIds(ids: List<Int>)
}
