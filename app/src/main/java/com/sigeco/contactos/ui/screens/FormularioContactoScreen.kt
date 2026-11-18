package com.sigeco.contactos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sigeco.contactos.ui.viewmodel.ContactoViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioContactoScreen(
    viewModel: ContactoViewModel,
    onVolverClick: () -> Unit
) {
    val state by viewModel.formularioState.collectAsState()

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) {
            viewModel.limpiarGuardadoExitoso()
            onVolverClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.esEdicion) "Editar Contacto" else "Nuevo Contacto")
                },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.telefono,
                onValueChange = { viewModel.actualizarTelefono(it) },
                label = { Text("Número de teléfono") },
                placeholder = { Text("999999999") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.telefonoError != null,
                supportingText = {
                    if (state.telefonoError != null) Text(state.telefonoError!!)
                    else Text("${state.telefono.length}/9 dígitos")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = state.nombre,
                onValueChange = { viewModel.actualizarNombre(it) },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.nombreError != null,
                supportingText = {
                    if (state.nombreError != null) Text(state.nombreError!!)
                },
                singleLine = true
            )

            OutlinedTextField(
                value = state.apellido,
                onValueChange = { viewModel.actualizarApellido(it) },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.apellidoError != null,
                supportingText = {
                    if (state.apellidoError != null) Text(state.apellidoError!!)
                },
                singleLine = true
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.actualizarEmail(it) },
                label = { Text("Correo electrónico") },
                placeholder = { Text("ejemplo@correo.com") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = state.emailError != null,
                supportingText = {
                    if (state.emailError != null) Text(state.emailError!!)
                },
                singleLine = true
            )

            if (state.errorGeneral != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.errorGeneral!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Button(
                onClick = { viewModel.guardarContacto() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.esEdicion) "Actualizar Contacto" else "Registrar Contacto")
            }
        }
    }
}
