package com.example.inventarioapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "inventario.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE productos(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                cantidad INTEGER NOT NULL,
                precio REAL NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS productos")
        onCreate(db)
    }

    fun insertarProducto(
        nombre: String,
        cantidad: Int,
        precio: Double
    ): Long {

        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("cantidad", cantidad)
            put("precio", precio)
        }

        return writableDatabase.insert(
            "productos",
            null,
            valores
        )
    }

    fun obtenerProductos(): List<Producto> {

        val lista = mutableListOf<Producto>()

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM productos ORDER BY id DESC",
            null
        )

        while (cursor.moveToNext()) {
            lista.add(
                Producto(
                    id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                    ),
                    nombre = cursor.getString(
                        cursor.getColumnIndexOrThrow("nombre")
                    ),
                    cantidad = cursor.getInt(
                        cursor.getColumnIndexOrThrow("cantidad")
                    ),
                    precio = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("precio")
                    )
                )
            )
        }

        cursor.close()

        return lista
    }

    fun actualizarProducto(
        id: Int,
        nombre: String,
        cantidad: Int,
        precio: Double
    ): Int {

        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("cantidad", cantidad)
            put("precio", precio)
        }

        return writableDatabase.update(
            "productos",
            valores,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    fun eliminarProducto(id: Int): Int {

        return writableDatabase.delete(
            "productos",
            "id = ?",
            arrayOf(id.toString())
        )
    }
}