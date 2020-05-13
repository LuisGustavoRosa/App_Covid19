package com.ap8.api_covid

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_estado_result.*
import kotlinx.android.synthetic.main.activity_mundo_paises.*
import kotlinx.android.synthetic.main.activity_mundo_paises.textView_title
import kotlinx.android.synthetic.main.activity_mundo_paises.view_casesResult
import kotlinx.android.synthetic.main.activity_mundo_paises.view_deathsResult
import java.text.DecimalFormat
import kotlinx.android.synthetic.main.activity_estado_result.progress as progress1

class DadosActivity : AppCompatActivity() {

    private var asyncTask: EstatisticasTask? = null
    var paisOuEstado = ""
    var endereco = ""
    var name = ""
    var dados_ = Estatisticas(
        country = null,
        uf = null,
        state = null,
        suspects = 0,
        refuses = 0,
        cases = 0,
        confirmed = 0,
        deaths = 0,
        recovered = 0,
        date = null,
        hour = null
    )
    private var dados = dados_

    override fun onCreate(savedInstanceState: Bundle?) {
        val name_ = intent.getStringExtra("nome")
        val endereco_ = intent.getStringExtra("endereco")
        val uf_ = intent.getStringExtra("uf")
        paisOuEstado = name_
        endereco = endereco_
        if(endereco == "estados") {
            paisOuEstado = uf_
            name = name_
            setContentView(R.layout.activity_estado_result)
        } else if(endereco == "paises") {
            setContentView(R.layout.activity_mundo_paises)
        }
        super.onCreate(savedInstanceState)

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = ""
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        carregarEstatisticas()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun carregarEstatisticas() {
        dados = dados_
        if (dados != dados_) {
            showProgress(false)
        } else {
            if (asyncTask == null) {
                if (EstatisticasHTTP.hasConnection(this)) {
                    if (asyncTask?.status != AsyncTask.Status.RUNNING) {
                        asyncTask = EstatisticasTask()
                        asyncTask?.execute()
                    } else {
                        progress.visibility = View.GONE
                        Toast.makeText(this, "Sem conexão!", Toast.LENGTH_LONG).show()
                    }
                }
            } else if (asyncTask?.status == AsyncTask.Status.RUNNING) {
                showProgress(true)
            }
        }
    }

    inner class EstatisticasTask: AsyncTask<Void, Void, Estatisticas>() {
        override fun onPreExecute() {
            super.onPreExecute()
            showProgress(true)
        }

        override fun doInBackground(vararg params: Void?): Estatisticas? {
            var path = ""
            if(endereco == "estados") {
                path = "/brazil/uf/${paisOuEstado}"
            } else if(endereco == "paises") {
                path = "/${paisOuEstado}"
            }

            val list = EstatisticasHTTP.loadEstatisticas(path)
            return list[0]
        }
        override fun onPostExecute(resultado: Estatisticas?) {
            super.onPostExecute(resultado)
            showProgress(false)
            atualizarEstatisticas(resultado)
        }
    }

    private fun atualizarEstatisticas(resultado: Estatisticas?) {
        if(resultado != null) {
            dados = dados_
            dados = resultado
        }
        if(endereco == "estados") {
            exibirDadosEstado()
        } else if(endereco == "paises") {
            exibirDadosPais()
        }
    }

    fun showProgress(show: Boolean){
        if (!show){
            progress.visibility = if(show) View.VISIBLE else View.GONE
        }
    }

    fun exibirDadosPais() {
        val df = DecimalFormat("###,###")
        view_casesResult.text = df.format(dados.cases)
        view_confirmedResult.text = df.format(dados.confirmed)
        view_recoveredResult.text = df.format(dados.recovered)
        view_deathsResult.text = df.format(dados.deaths)
        textView_title.text = "${paisOuEstado} \nEstatísticas:"
    }

    fun exibirDadosEstado() {
        paisOuEstado = name
        val df = DecimalFormat("###,###")
        view_casesResult.text = df.format(dados.cases)
        view_suspectsResult.text = df.format(dados.suspects)
        view_refusesResult.text = df.format(dados.refuses)
        view_deathsResult.text = df.format(dados.deaths)
        textView_title.text = "${paisOuEstado} \nEstatísticas:"
    }
}
