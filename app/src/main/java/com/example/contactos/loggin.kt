package com.example.contactos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.android.volley.Request
import org.json.JSONException

class loggin : AppCompatActivity() {

    lateinit var txtUsuario: EditText
    lateinit var txtPasswd: EditText
    lateinit var btnLogin: Button

    val apis:String = "https://agenda.ioasystem.com/api_login.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loggin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mapeo()

        btnLogin.setOnClickListener {
            when{
                txtUsuario.text.isEmpty()->{
                    Toast.makeText(applicationContext, "Ingrese un usuario", Toast.LENGTH_SHORT).show()
                }
                txtPasswd.text.isEmpty()->{
                    Toast.makeText(applicationContext, "Ingrese la contraseña", Toast.LENGTH_SHORT).show()
                }
                else->{
                    login(txtUsuario.text.toString(), txtPasswd.text.toString())
                }
            }
        }
    }

    fun mapeo(){
        txtUsuario = findViewById(R.id.txt_usuario)
        txtPasswd = findViewById(R.id.txt_passwd)
        btnLogin = findViewById(R.id.btn_login)
    }

    private fun login(email: String, passwd:String){
        val campos = JSONObject()
        campos.put("accion", "login")
        campos.put("email", email)
        campos.put("password", passwd)
        val rq = Volley.newRequestQueue(this)
        val jsoresp = JsonObjectRequest(Request.Method.POST, apis, campos,
            {
                    s->
                try {
                    val obj = (s)
                    if(obj.getBoolean("estado")){
                        val array = obj.getJSONArray("data")
                        val dato = array.getJSONObject(0)
                        val cod_persona = dato.getString("cod_persona")
                        val full_name = dato.getString("nom_persona") + " " + dato.getString("ape_persona")
                        Toast.makeText(applicationContext, obj.getString("response").toString(), Toast.LENGTH_LONG).show()
                        val form2 = Intent(this, contactos::class.java)
                        val bundle = Bundle()
                        bundle.putString("cod_persona", cod_persona)
                        bundle.putString("fullname", full_name)
                        form2.putExtras(bundle)
                        startActivity(form2)
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
}