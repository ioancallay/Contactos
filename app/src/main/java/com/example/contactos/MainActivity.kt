package com.example.contactos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var btnNuevo: Button
    lateinit var btnGuardar: Button
    lateinit var btnEditar: Button
    lateinit var btnBorrar: Button
    lateinit var btnCancelar: Button

    lateinit var txtNombre: EditText
    lateinit var txtApellido: EditText
    lateinit var txtCedula: EditText
    lateinit var txtEmail: EditText
    lateinit var txtPassword: EditText
    lateinit var listPersona: ListView
    val codigo = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mapeo()
        limpiaCajas()

        btnCancelar.setOnClickListener {
            limpiaCajas()
        }

        btnNuevo.setOnClickListener {
            activarBotones()
            activarCajas()
        }
    }

    fun mapeo(){

        txtNombre = findViewById(R.id.txt_nombre)
        txtApellido = findViewById(R.id.txt_apellido)
        txtCedula = findViewById(R.id.txt_cedula)
        txtEmail = findViewById(R.id.txt_email)
        txtPassword = findViewById(R.id.txt_password)
        listPersona = findViewById(R.id.lista_personas)

        btnNuevo = findViewById(R.id.btn_nuevo)
        btnGuardar = findViewById(R.id.btn_guardar)
        btnEditar = findViewById(R.id.btn_editar)
        btnBorrar = findViewById(R.id.btn_borrar)
        btnCancelar = findViewById(R.id.btn_cancelar)
    }

    fun limpiaCajas(){
        txtCedula.setText("")
        txtNombre.setText("")
        txtApellido.setText("")
        txtEmail.setText("")
        txtPassword.setText("")
        desactivarBotones()
        desactivarCajas()
    }

    fun desactivarBotones(){
        btnNuevo.isEnabled = true
        btnGuardar.isEnabled = false
        btnEditar.isEnabled = false
        btnBorrar.isEnabled = false
        btnCancelar.isEnabled = false
    }

    fun activarBotones(){
        btnNuevo.isEnabled = false
        btnGuardar.isEnabled = true
        btnEditar.isEnabled = true
        btnBorrar.isEnabled = true
        btnCancelar.isEnabled = true
    }

    fun desactivarCajas(){
        txtNombre.isEnabled = false
        txtApellido.isEnabled = false
        txtCedula.isEnabled = false
        txtEmail.isEnabled = false
        txtPassword.isEnabled = false
    }

    fun activarCajas(){
        txtNombre.isEnabled = true
        txtApellido.isEnabled = true
        txtCedula.isEnabled = true
        txtEmail.isEnabled = true
        txtPassword.isEnabled = true
    }
}