package com.example.contactos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.android.volley.Request
import org.json.JSONException

class loggin : AppCompatActivity() {

    lateinit var txtUsuario: EditText
    lateinit var txtPasswd: EditText
    lateinit var btnLogin: Button

    val apis:String = "https://agenda.ioasystem.com/login.php"

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
                    check_loggin(txtUsuario.text.toString(), txtPasswd.text.toString())
                }
            }
        }
    }

    fun mapeo(){
        txtUsuario = findViewById(R.id.txt_usuario)
        txtPasswd = findViewById(R.id.txt_passwd)
        btnLogin = findViewById(R.id.btn_login)
    }

    private fun check_loggin(email: String, passwd:String){
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
                if (obj.getBoolean("estado")){
                    val array = obj.getJSONArray("data")
                    val dato = array.getJSONObject(0)
                    Toast.makeText(applicationContext, obj.getString("response"), Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(applicationContext, obj.getString("response"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
            }

        }, {
            volleyError-> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_SHORT).show()
        })
        rq.add(jsoresp)
    }
}