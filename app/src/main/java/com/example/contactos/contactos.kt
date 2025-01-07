package com.example.contactos

import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.android.volley.Request
import org.json.JSONArray
import org.json.JSONException

class contactos : AppCompatActivity() {

    lateinit var btnNuevoCon: Button
    lateinit var btnGuardarCont: Button
    lateinit var btnEditarCon: Button
    lateinit var btnBorrarCon: Button
    lateinit var btnCancelarCon: Button

    lateinit var txtNomContacto: EditText
    lateinit var txtApeContacto: EditText
    lateinit var txtCorreoContacto: EditText
    lateinit var txtTelefonoContacto: EditText
    lateinit var txtCodigoContacto: EditText
    lateinit var txtCodigoPersona: EditText

    lateinit var lblTituloContacto: TextView
    lateinit var listaContactos: ListView


    val codigos_contacto = ArrayList<String>()
    val apis:String = "https://agenda.ioasystem.com/api_contacto.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contactos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mapeo()

        var bundle = intent.extras
        var cod_persona = bundle?.getString("cod_persona").toString()
        var fullname = bundle?.getString("full_name").toString()
        lblTituloContacto.text = fullname
        txtCodigoPersona.setText(cod_persona)
                //logs_error()
        desactivarCajaContacto()
        desactivarBotonesContacto()
        todosContactos()

        btnNuevoCon.setOnClickListener {
            activarBotonesContacto()
            activarCajaContacto()
        }

        btnCancelarCon.setOnClickListener {
            desactivarBotonesContacto()
            desactivarCajaContacto()
            limpiarCajaContacto()
        }

        btnEditarCon.setOnClickListener {
            actualizar(txtCodigoContacto.text.toString())
            desactivarBotonesContacto()
            desactivarCajaContacto()
            limpiarCajaContacto()
            todosContactos()
        }

        listaContactos.setOnItemClickListener{adapterView, view, i, l->
            Toast.makeText(applicationContext, codigos_contacto[i], Toast.LENGTH_SHORT).show()
            editar(codigos_contacto[i])
            editarBotonesContacto()
            activarCajaContacto()
        }

        btnGuardarCont.setOnClickListener {
            when{
                txtNomContacto.text.isEmpty()->{
                    Toast.makeText(applicationContext, "Ingrese el nombre", Toast.LENGTH_SHORT).show()
                }
                txtApeContacto.text.isEmpty()->{
                    Toast.makeText(applicationContext, "Ingrese el apellido", Toast.LENGTH_SHORT).show()
                }
                txtCorreoContacto.text.isEmpty()->{
                    Toast.makeText(applicationContext, "Ingrese el correo", Toast.LENGTH_SHORT).show()
                }
                txtTelefonoContacto.text.isEmpty()->{
                    Toast.makeText(applicationContext, "Ingrese el numero", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    insertar()
                    desactivarCajaContacto()
                    desactivarBotonesContacto()
                    limpiarCajaContacto()
                    todosContactos()
                }
            }
        }

        btnBorrarCon.setOnClickListener {
            confirmaEliminar()
        }

    }

    fun mapeo(){
        btnNuevoCon = findViewById(R.id.btn_nuevo_con)
        btnGuardarCont = findViewById(R.id.btn_guarda_con)
        btnEditarCon = findViewById(R.id.btn_actualizar_con)
        btnBorrarCon = findViewById(R.id.btn_borra_con)
        btnCancelarCon = findViewById(R.id.btn_cancela_con)

        txtNomContacto = findViewById(R.id.txt_nom_contacto)
        txtApeContacto = findViewById(R.id.txt_ape_contacto)
        txtCorreoContacto = findViewById(R.id.txt_correo_contacto)
        txtTelefonoContacto = findViewById(R.id.txt_telefono)
        txtCodigoContacto = findViewById(R.id.txt_cod_contacto)
        txtCodigoPersona = findViewById(R.id.txt_cod_persona)

        lblTituloContacto = findViewById(R.id.lbl_titulo_contacto)
        listaContactos = findViewById(R.id.list_contactos)

    }

    fun activarCajaContacto(){
        txtNomContacto.isEnabled = true
        txtApeContacto.isEnabled = true
        txtCorreoContacto.isEnabled = true
        txtTelefonoContacto.isEnabled = true
        txtCodigoContacto.isVisible = false
        txtCodigoPersona.isVisible = false
    }

    fun limpiarCajaContacto(){
        txtNomContacto.setText("")
        txtApeContacto.setText("")
        txtCorreoContacto.setText("")
        txtTelefonoContacto.setText("")
    }

    fun desactivarCajaContacto(){
        txtNomContacto.isEnabled = false
        txtApeContacto.isEnabled = false
        txtCorreoContacto.isEnabled = false
        txtTelefonoContacto.isEnabled = false
        txtCodigoContacto.isVisible = false
        txtCodigoPersona.isVisible = false
    }

    fun activarBotonesContacto(){
        btnNuevoCon.isVisible = false
        btnEditarCon.isVisible = false
        btnBorrarCon.isVisible = false
        btnGuardarCont.isVisible = true
        btnCancelarCon.isVisible = true

    }

    fun desactivarBotonesContacto(){
        btnNuevoCon.isVisible = true
        btnEditarCon.isVisible = false
        btnBorrarCon.isVisible = false
        btnGuardarCont.isVisible = false
        btnCancelarCon.isVisible = false
    }

    fun editarBotonesContacto(){
        btnNuevoCon.isVisible = false
        btnEditarCon.isVisible = true
        btnBorrarCon.isVisible = true
        btnGuardarCont.isVisible = false
        btnCancelarCon.isVisible = true
    }

    private fun editar(cod_contacto:String){
        txtCodigoContacto.setText(cod_contacto)
        val campos = JSONObject()
        campos.put("accion", "unContacto")
        campos.put("cod_contacto", cod_contacto)
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
                        txtNomContacto.setText(dato.getString("nombre_contacto"))
                        txtApeContacto.setText(dato.getString("apellido_contacto"))
                        txtCorreoContacto.setText(dato.getString("correo_contacto"))
                        txtTelefonoContacto.setText(dato.getString("telefono_contacto"))
                        todosContactos()
                    }
                    else{
                        Toast.makeText(applicationContext, obj.getString("response").toString(), Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException){
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
                }
            },
            { volleyError-> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() })
        rq.add(jsoresp)
    }

    private fun todosContactos(){
        codigos_contacto.clear()
        var bundle = intent.extras
        var cod_persona = bundle?.getString("cod_persona").toString()
        val intrapersonal = ArrayList<String>()
        val campos = JSONObject()
        campos.put("accion", "todosContactos")
        campos.put("cod_persona", cod_persona)
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
                            intrapersonal.add(fila.getString("cod_contacto") + "\nNombre: " + fila.getString("nombre_contacto") + " " + fila.getString("apellido_contacto") + "\n" + fila.getString("telefono_contacto") +  "\n" + fila.getString("correo_contacto"))
                            codigos_contacto.add(fila.getString("cod_contacto"))
                        }
                        val adapterList = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, intrapersonal)
                        listaContactos.adapter = adapterList
                        adapterList.notifyDataSetChanged()
                    }
                    else{
                        Toast.makeText(applicationContext, obj.getString("response").toString(), Toast.LENGTH_SHORT).show()
                    }

                } catch (e: JSONException){
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            { volleyError-> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() })
        rq.add(jsoresp)
    }

    private fun insertar(){
        val campos = JSONObject()
        campos.put("accion", "insertar")
        campos.put("nombre_contacto", txtNomContacto.text.toString())
        campos.put("apellido_contacto", txtApeContacto.text.toString())
        campos.put("telefono_contacto", txtCorreoContacto.text.toString())
        campos.put("correo_contacto", txtTelefonoContacto.text.toString())
        campos.put("cod_persona", txtCodigoPersona.text.toString())

        val rq = Volley.newRequestQueue(this)
        val jsoresp = JsonObjectRequest(Request.Method.POST, apis, campos,
            {
                    s->
                try {
                    val obj=(s)
                    if (obj.getBoolean("estado")){
                        Toast.makeText(applicationContext, obj.getString("response").toString(), Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(applicationContext, obj.getString("response").toString(), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException){
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            { volleyError-> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_SHORT).show() })
        rq.add(jsoresp)
    }

    private fun actualizar(cod_contacto: String){
        val campos = JSONObject()
        campos.put("accion", "actualizar")
        campos.put("cod_contacto", cod_contacto)
        campos.put("nombre_contacto", txtNomContacto.text.toString())
        campos.put("apellido_contacto", txtApeContacto.text.toString())
        campos.put("telefono_contacto", txtTelefonoContacto.text.toString())
        campos.put("correo_contacto", txtCorreoContacto.text.toString())

        val rq = Volley.newRequestQueue(this)
        val jsoresp = JsonObjectRequest(Request.Method.POST, apis, campos, {
                s->
            try {
                val obj = (s)
                if (obj.getBoolean("estado")){
                    Toast.makeText(applicationContext, obj.getString("response"), Toast.LENGTH_SHORT).show()
                    todosContactos()
                } else{
                    Toast.makeText(applicationContext, obj.getString("response"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
            }
        },
            {volleyError-> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_SHORT).show()})
        rq.add(jsoresp)
    }

    fun confirmaEliminar(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Advertencia")
        builder.setMessage("Esta seguro que desea eliminar este registro")
        builder.setPositiveButton(android.R.string.yes){ dialog, which->
            eliminar(txtCodigoContacto.text.toString())
            limpiarCajaContacto()
            desactivarCajaContacto()
            todosContactos()

        }
        builder.setNeutralButton(android.R.string.no){
                dialog, which->
            Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun eliminar(cod_contacto: String){
        val campos = JSONObject()
        campos.put("accion", "eliminar")
        campos.put("cod_contacto", cod_contacto)
        val rq = Volley.newRequestQueue(this)
        val jsoresp = JsonObjectRequest(Request.Method.POST, apis, campos, {
                s->
            try {
                val obj = (s)
                if (obj.getBoolean("estado")){
                    Toast.makeText(applicationContext, obj.getString("response"), Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(applicationContext, obj.getString("response"), Toast.LENGTH_SHORT).show()
                }
            }
            catch (e: JSONException){
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
            }
        },
            {volleyError-> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_SHORT).show()})
        rq.add(jsoresp)
    }
}