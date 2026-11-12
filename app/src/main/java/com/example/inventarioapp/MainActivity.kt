package com.example.inventarioapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.inventarioapp.ui.theme.InventarioAppTheme

data class Producto(
    val id: Int,
    val nombre: String,
    val cantidad: Int,
    val precio: Double
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            InventarioAppTheme {
                AplicacionInventario()
            }
        }
    }
}

@Composable
fun AplicacionInventario() {

    var sesionIniciada by remember {
        mutableStateOf(false)
    }

    if (sesionIniciada) {
        PantallaInventario()
    } else {
        PantallaLogin(
            onLoginCorrecto = {
                sesionIniciada = true
            }
        )
    }
}

@Composable
fun PantallaLogin(
    onLoginCorrecto: () -> Unit
) {

    var usuario by remember {
        mutableStateOf("")
    }

    var contrasena by remember {
        mutableStateOf("")
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "InventarioApp",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        OutlinedTextField(
            value = usuario,
            onValueChange = {
                usuario = it
            },
            label = {
                Text("Usuario")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
            },
            label = {
                Text("Contraseña")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {

                if (
                    usuario == "admin" &&
                    contrasena == "1234"
                ) {
                    mensaje = ""
                    onLoginCorrecto()
                } else {
                    mensaje =
                        "Usuario o contraseña incorrectos"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Iniciar sesión")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (mensaje.isNotEmpty()) {
            Text(mensaje)
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text("Usuario de prueba: admin")
        Text("Contraseña: 1234")
    }
}
@Composable
fun PantallaInventario() {

    val context = androidx.compose.ui.platform.LocalContext.current

    val database = remember {
        DatabaseHelper(context)
    }

    val productos = remember {
        mutableStateListOf<Producto>().apply {
            addAll(database.obtenerProductos())
        }
    }

    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var editandoId by remember { mutableStateOf<Int?>(null) }

    fun recargarProductos() {
        productos.clear()
        productos.addAll(database.obtenerProductos())
    }

    fun limpiarCampos() {
        nombre = ""
        cantidad = ""
        precio = ""
        editandoId = null
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "Sistema de Inventario",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {

                    val cantidadNumero = cantidad.toIntOrNull()
                    val precioNumero = precio.toDoubleOrNull()

                    if (
                        nombre.isNotBlank() &&
                        cantidadNumero != null &&
                        cantidadNumero >= 0 &&
                        precioNumero != null &&
                        precioNumero >= 0
                    ) {

                        if (editandoId == null) {

                            database.insertarProducto(
                                nombre.trim(),
                                cantidadNumero,
                                precioNumero
                            )

                        } else {

                            database.actualizarProducto(
                                editandoId!!,
                                nombre.trim(),
                                cantidadNumero,
                                precioNumero
                            )
                        }

                        limpiarCampos()
                        recargarProductos()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    if (editandoId == null) {
                        "Registrar producto"
                    } else {
                        "Guardar cambios"
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Productos registrados",
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn {

                items(
                    productos,
                    key = { it.id }
                ) { producto ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {

                            Text("Producto: ${producto.nombre}")
                            Text("Cantidad: ${producto.cantidad}")
                            Text("Precio: \$${producto.precio}")

                            Row {

                                TextButton(
                                    onClick = {
                                        editandoId = producto.id
                                        nombre = producto.nombre
                                        cantidad = producto.cantidad.toString()
                                        precio = producto.precio.toString()
                                    }
                                ) {
                                    Text("Editar")
                                }

                                TextButton(
                                    onClick = {

                                        database.eliminarProducto(
                                            producto.id
                                        )

                                        recargarProductos()

                                        if (editandoId == producto.id) {
                                            limpiarCampos()
                                        }
                                    }
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}