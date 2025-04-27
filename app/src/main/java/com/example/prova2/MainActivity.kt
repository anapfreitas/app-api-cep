package com.example.prova2

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var cepInput: EditText
    private lateinit var btnBuscar: Button
    private lateinit var resultadoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cepInput = findViewById(R.id.cepInput)
        btnBuscar = findViewById(R.id.btnBuscar)
        resultadoTextView = findViewById(R.id.resultadoTextView)

        btnBuscar.setOnClickListener {
            val cep = cepInput.text.toString().trim()

            if (cep.length == 8) {
                buscarCep(cep)
            } else {
                Toast.makeText(this, "Digite um CEP válido (8 números)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buscarCep(cep: String) {
        val url = "https://viacep.com.br/ws/$cep/json/"

        val request = Request.Builder().url(url).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    resultadoTextView.text = "Erro ao conectar à API."
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && body != null) {
                        val json = JSONObject(body)
                        if (json.has("erro")) {
                            resultadoTextView.text = "CEP não encontrado."
                        } else {
                            val logradouro = json.getString("logradouro")
                            val bairro = json.getString("bairro")
                            val localidade = json.getString("localidade")
                            val uf = json.getString("uf")

                            resultadoTextView.text = """
                                Rua: $logradouro
                                Bairro: $bairro
                                Cidade: $localidade - $uf
                            """.trimIndent()
                        }
                    } else {
                        resultadoTextView.text = "CEP inválido ou erro na API."
                    }
                }
            }
        })
    }
}
