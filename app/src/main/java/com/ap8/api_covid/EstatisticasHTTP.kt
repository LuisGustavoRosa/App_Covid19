package com.ap8.api_covid

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object EstatisticasHTTP {

    fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnected
    }

    fun loadEstatisticas(comPath: String): MutableList<Estatisticas> {
        val Json_URL = "https://covid19-brazil-api.now.sh/api/report/v1${comPath}"

        val client = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
        val req = Request.Builder()
            .url(Json_URL)
            .build()
        val res = client.newCall(req).execute()

        val jsonString = res.body?.string()
        val json_Object = JSONObject(jsonString)
        if(comPath == "/countries" || comPath == "") {
            val json_array = json_Object.getJSONArray("data")
            return readJsonArray(json_array, comPath)
        } else if(comPath.length >= 10) {
            if (comPath.substring(0, 10) == "/brazil/uf") {
                var array_estatisticas = mutableListOf<Estatisticas>()
                var json_ = getJsonEstados(json_Object)
                array_estatisticas.add(json_)
                return array_estatisticas
            }
        }

        return readJsonObjetic(json_Object)
    }

    fun readJsonArray(jsonArray: JSONArray, comPath: String): MutableList<Estatisticas> {
        val array_estatisticas = mutableListOf<Estatisticas>()

        try {
            for (i in 0 .. jsonArray.length()-1){
                val json = jsonArray.getJSONObject(i)
                if(comPath == "/countries") {
                    var dados = getJsonPaises(json)
                    array_estatisticas.add(dados)
                } else if(comPath == "") {
                    var dados = getJsonEstados(json)
                    array_estatisticas.add(dados)
                }
            }
        }
        catch (e : IOException){
            Log.e("Erro", "Impossivel ler JSON")
        }

        return array_estatisticas
    }

    fun readJsonObjetic(json: JSONObject): MutableList<Estatisticas> {
        val array_estatisticas = mutableListOf<Estatisticas>()

        try {
            var dados = getJsonPaises(json.getJSONObject("data"))
            array_estatisticas.add(dados)
        }
        catch (e : IOException){
            Log.e("Erro", "Impossivel ler JSON")
        }

        return array_estatisticas
    }

    fun getJsonPaises(json: JSONObject): Estatisticas {
        val date_ = formatarData(json.getString("updated_at").substring(0,10))
        val hour_ = json.getString("updated_at").substring(11, 16)


        val estatisticas = Estatisticas(
            country = json.getString("country"),
            cases = json.optInt("cases",0),
            confirmed = json.optInt("confirmed",0),
            deaths = json.optInt("deaths",0),
            recovered = json.optInt("recovered",0),
            date = date_,
            hour = hour_,
            uf = null,
            state = null,
            refuses = 0,
            suspects = 0
        )


        return estatisticas
    }

    fun getJsonEstados(json: JSONObject): Estatisticas {
        val date_ = formatarData(json.getString("datetime").substring(0,10))
        val hour_ = json.getString("datetime").substring(11, 16)
        val estatisticas = Estatisticas(
            uf = json.getString("uf"),
            state = json.getString("state"),
            cases = json.optInt("cases",0),
            deaths = json.optInt("deaths",0),
            suspects = json.optInt("suspects",0),
            refuses = json.optInt("refuses",0),
            date = date_,
            hour = hour_,
            country = null,
            recovered = 0,
            confirmed = 0
        )
        return estatisticas
    }

    fun formatarData(data: String): String {
        val diaString =data
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        var date = LocalDate.parse(diaString)
        var formattedDate = date.format(formatter)
        return formattedDate
    }
}