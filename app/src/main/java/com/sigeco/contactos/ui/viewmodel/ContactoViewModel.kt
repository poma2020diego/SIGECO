package com.sigeco.contactos.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sigeco.contactos.data.AppDatabase
import com.sigeco.contactos.data.Contacto
import com.sigeco.contactos.data.ContactoDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListaUiState(
    val seleccionados: Set<Int> = emptySet(),
    val mensaje: String? = null,
    val esError: Boolean = false
)

data class FormularioUiState(
    val telefono: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefonoError: String? = null,
    val nombreError: String? = null,
    val apellidoError: String? = null,
    val emailError: String? = null,
    val guardadoExitoso: Boolean = false,
    val errorGeneral: String? = null,
    val esEdicion: Boolean = false,
    val contactoId: Int = 0
)

class ContactoViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: ContactoDao = AppDatabase.getDatabase(application).contactoDao()
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val contactos: StateFlow<List<Contacto>> = _busqueda
        .flatMapLatest { texto ->
            if (texto.isBlank()) dao.obtenerTodos()
            else dao.buscar(texto)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private val _listaState = MutableStateFlow(ListaUiState())
    val listaState: StateFlow<ListaUiState> = _listaState.asStateFlow()


    private val _formularioState = MutableStateFlow(FormularioUiState())
    val formularioState: StateFlow<FormularioUiState> = _formularioState.asStateFlow()


    fun actualizarBusqueda(texto: String) {
        _busqueda.value = texto
    }

    fun toggleSeleccion(contactoId: Int) {
        _listaState.update { state ->
            val nuevaSeleccion = state.seleccionados.toMutableSet()
            if (contactoId in nuevaSeleccion) nuevaSeleccion.remove(contactoId)
            else nuevaSeleccion.add(contactoId)
            state.copy(seleccionados = nuevaSeleccion)
        }
    }

    fun limpiarSeleccion() {
        _listaState.update { it.copy(seleccionados = emptySet()) }
    }

    fun limpiarMensaje() {
        _listaState.update { it.copy(mensaje = null) }
    }

    fun iniciarNuevoContacto() {
        _formularioState.value = FormularioUiState()
    }

    fun cargarContactoParaEditar(contactoId: Int) {
        viewModelScope.launch {
            val contacto = dao.obtenerPorId(contactoId)
            if (contacto != null) {
                _formularioState.value = FormularioUiState(
                    telefono = contacto.telefono,
                    nombre = contacto.nombre,
                    apellido = contacto.apellido,
                    email = contacto.email,
                    esEdicion = true,
                    contactoId = contacto.id
                )
            }
        }
    }

    fun actualizarTelefono(valor: String) {
        val filtrado = valor.filter { it.isDigit() }.take(9)
        _formularioState.update { it.copy(telefono = filtrado, telefonoError = null) }
    }

    fun actualizarNombre(valor: String) {
        _formularioState.update { it.copy(nombre = valor.take(200), nombreError = null) }
    }

    fun actualizarApellido(valor: String) {
        _formularioState.update { it.copy(apellido = valor.take(200), apellidoError = null) }
    }

    fun actualizarEmail(valor: String) {
        _formularioState.update { it.copy(email = valor, emailError = null) }
    }

    fun limpiarGuardadoExitoso() {
        _formularioState.update { it.copy(guardadoExitoso = false) }
    }

    private fun validarFormulario(): Boolean {
        val state = _formularioState.value
        var esValido = true

        if (state.telefono.isBlank()) {
            _formularioState.update { it.copy(telefonoError = "El teléfono es obligatorio") }
            esValido = false
        } else if (state.telefono.length != 9) {
            _formularioState.update { it.copy(telefonoError = "El teléfono debe tener 9 dígitos") }
            esValido = false
        }

        if (state.nombre.isBlank()) {
            _formularioState.update { it.copy(nombreError = "El nombre es obligatorio") }
            esValido = false
        }

        if (state.apellido.isBlank()) {
            _formularioState.update { it.copy(apellidoError = "El apellido es obligatorio") }
            esValido = false
        }

        if (state.email.isBlank()) {
            _formularioState.update { it.copy(emailError = "El correo es obligatorio") }
            esValido = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _formularioState.update { it.copy(emailError = "Formato inválido (use xxx@yyyy.uuuu)") }
            esValido = false
        }

        return esValido
    }

    fun guardarContacto() {
        if (!validarFormulario()) return

        viewModelScope.launch {
            try {
                val state = _formularioState.value
                val contacto = Contacto(
                    id = if (state.esEdicion) state.contactoId else 0,
                    telefono = state.telefono,
                    nombre = state.nombre.trim(),
                    apellido = state.apellido.trim(),
                    email = state.email.trim()
                )

                if (state.esEdicion) {
                    val duplicado = dao.buscarTelefonoDuplicado(contacto.telefono, contacto.id)
                    if (duplicado != null) {
                        _formularioState.update {
                            it.copy(telefonoError = "Este teléfono ya pertenece a otro contacto")
                        }
                        return@launch
                    }
                    dao.actualizar(contacto)
                } else {
                    val existente = dao.buscarPorTelefono(contacto.telefono)
                    if (existente != null) {
                        _formularioState.update {
                            it.copy(telefonoError = "Este teléfono ya está registrado")
                        }
                        return@launch
                    }
                    dao.insertar(contacto)
                }

                _formularioState.update { it.copy(guardadoExitoso = true) }
                _listaState.update {
                    it.copy(
                        mensaje = if (state.esEdicion) "Contacto actualizado correctamente"
                        else "Contacto registrado correctamente",
                        esError = false
                    )
                }
            } catch (e: Exception) {
                _formularioState.update {
                    it.copy(errorGeneral = "Error al guardar: ${e.message}")
                }
            }
        }
    }

    fun eliminarSeleccionados() {
        val ids = _listaState.value.seleccionados.toList()
        if (ids.isEmpty()) return

        viewModelScope.launch {
            try {
                dao.eliminarPorIds(ids)
                val cantidad = ids.size
                _listaState.update {
                    it.copy(
                        seleccionados = emptySet(),
                        mensaje = if (cantidad == 1) "1 contacto eliminado"
                        else "$cantidad contactos eliminados",
                        esError = false
                    )
                }
            } catch (e: Exception) {
                _listaState.update {
                    it.copy(
                        mensaje = "Error al eliminar: ${e.message}",
                        esError = true
                    )
                }
            }
        }
    }
}
