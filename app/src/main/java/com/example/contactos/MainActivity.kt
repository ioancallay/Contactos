package com.example.contactos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.android.volley.Request
import org.json.JSONException

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
    lateinit var listaPersona: ListView
    val codigos = ArrayList<String>()

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
        consultar()

        btnCancelar.setOnClickListener {
            limpiaCajas()
            desactivarBotones()
        }

        btnNuevo.setOnClickListener {
            consultar()
            activarBotones()
            activarCajas()
        }

        listaPersona.setOnItemClickListener{adapterView, view, i, l->
            Toast.makeText(applicationContext, codigos[i], Toast.LENGTH_SHORT).show()
            editar(codigos[i])
            editarBotones()
            activarCajas()
        }
        btnGuardar.setOnClickListener {
            when {
                txtNombre.text.isEmpty() -> {
                    Toast.makeText(applicationContext, "Ingrese su nombre", Toast.LENGTH_SHORT).show()
                }
                txtApellido.text.isEmpty() -> {
                    Toast.makeText(applicationContext, "Ingrese su apellido", Toast.LENGTH_SHORT).show()
                }
                txtCedula.text.isEmpty() -> {
                    Toast.makeText(applicationContext, "Ingrese su cédula", Toast.LENGTH_SHORT).show()
                }
                txtEmail.text.isEmpty() -> {
                    Toast.makeText(applicationContext, "Ingrese su email", Toast.LENGTH_SHORT).show()
                }
                txtPassword.text.isEmpty() -> {
                    Toast.makeText(applicationContext, "Ingrese su contraseña", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(applicationContext, "Guardado", Toast.LENGTH_SHORT).show()
                    limpiaCajas()
                    desactivarCajas()
                    desactivarBotones()
                }
            }
        }

    }

    fun mapeo(){

        txtNombre = findViewById(R.id.txt_nombre)
        txtApellido = findViewById(R.id.txt_apellido)
        txtCedula = findViewById(R.id.txt_cedula)
        txtEmail = findViewById(R.id.txt_email)
        txtPassword = findViewById(R.id.txt_password)
        listaPersona = findViewById(R.id.lista_personas)

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

        btnNuevo.isVisible = true
        btnGuardar.isVisible = false
        btnEditar.isVisible = false
        btnBorrar.isVisible = false
        btnCancelar.isVisible = false
    }

    fun activarBotones(){
        btnNuevo.isEnabled = false
        btnGuardar.isEnabled = true
        btnEditar.isEnabled = true
        btnBorrar.isEnabled = true
        btnCancelar.isEnabled = true

        btnNuevo.isVisible = false
        btnGuardar.isVisible = true
        btnEditar.isVisible = false
        btnBorrar.isVisible = false
        btnCancelar.isVisible = true
    }

    fun editarBotones(){
        btnNuevo.isEnabled = false
        btnGuardar.isEnabled = false
        btnEditar.isEnabled = true
        btnBorrar.isEnabled = true
        btnCancelar.isEnabled = true

        btnNuevo.isVisible = false
        btnGuardar.isVisible = false
        btnEditar.isVisible = true
        btnBorrar.isVisible = true
        btnCancelar.isVisible = true
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

    fun guardar(){
        if(txtNombre.equals("")){
            Toast.makeText(applicationContext, "Ingrese su nombre", Toast.LENGTH_SHORT).show()
        }

        if(txtApellido.equals("")){
            Toast.makeText(applicationContext, "Ingrese su apellido", Toast.LENGTH_SHORT).show()
        }

        if(txtCedula.equals("")){
            Toast.makeText(applicationContext, "Ingrese su número de cedula", Toast.LENGTH_SHORT).show()
        }

        if(txtEmail.equals("")){
            Toast.makeText(applicationContext, "Ingrese su correo", Toast.LENGTH_SHORT).show()
        }

        if(txtPassword.equals("")){
            Toast.makeText(applicationContext, "Ingrese su contraseña", Toast.LENGTH_SHORT).show()
        }

        Toast.makeText(applicationContext, "Guardado", Toast.LENGTH_SHORT).show()
        limpiaCajas()
        desactivarCajas()
        desactivarBotones()
    }

    private fun consultar(){
        val intrapersonal = ArrayList<String>()
        val apis = "http://10.0.2.2/ws_agenda/persona.php"
        val campos = JSONObject()
        campos.put("accion", "consultar")
        val rq = Volley.newRequestQueue(this)
        val jsoresp = JsonObjectRequest(Request.Method.POST, apis, campos,
            {
                s->
                try {
                    val obj = (s)
                    if(obj.getBoolean("estado")){
                        val array = obj.getJSONArray("data")
                        for(i in 0..<array.length()){
                            val fila = array.getJSONObject(i)
                            intrapersonal.add(fila.getString("cedula") + " " + fila.getString("nombre") + " " + fila.getString("apellido"))
                            codigos.add(fila.getString("codigo"))
                        }
                        val adapterList = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, intrapersonal)
                        listaPersona.adapter = adapterList
                        adapterList.notifyDataSetChanged()
                    }
                    else{
                        Toast.makeText(applicationContext, obj.getBoolean("response").toString(), Toast.LENGTH_SHORT).show()
                    }

                } catch (e: JSONException){
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            { volleyError-> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() })
        rq.add(jsoresp)
    }

    private fun editar(codigo:String){
        val intrapersonal = ArrayList<String>()
        val apis = "http://10.0.2.2/ws_agenda/persona.php"
        val campos = JSONObject()
        campos.put("accion", "dato")
        campos.put("codigo", codigo)
        val rq = Volley.newRequestQueue(this)
        val jsoresp = JsonObjectRequest(Request.Method.POST, apis, campos,
            {
                    s->
                try {
                    val obj = (s)
                    if(obj.getBoolean("estado")){
                        val array = obj.getJSONArray("data")
                        val dato = array.getJSONObject(0)
                        //val dato = obj.getJSONObject(0)
                        txtNombre.setText(dato.getString("nombre"))
                        txtApellido.setText(dato.getString("apellido"))
                        txtCedula.setText(dato.getString("cedula"))
                        txtEmail.setText(dato.getString("correo"))
                        txtPassword.setText(dato.getString("clave"))

                    }
                    else{
                        Toast.makeText(applicationContext, obj.getBoolean("response").toString(), Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException){
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
                }
            },
            { volleyError-> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() })
        rq.add(jsoresp)
    }

    private fun insertar(){}
}