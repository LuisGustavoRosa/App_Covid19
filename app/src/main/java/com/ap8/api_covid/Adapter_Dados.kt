package com.ap8.api_covid

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.elemento_.view.*
import java.util.*
import kotlin.collections.ArrayList

class Adapter_Dados(
    var context: Context,
    var lista: ArrayList<String>,
    var listaJson: MutableList<Estatisticas>,
    var endereco: String
):
    RecyclerView.Adapter<Adapter_Dados.VH>(), Filterable  {

    var lista_filtrada: ArrayList<String> = lista
    var lista_aux: ArrayList<String> = lista

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if(charSearch.isEmpty()) {
                    lista_filtrada = lista_aux
                } else {
                    val resultList = ArrayList<String>()
                    for (row in lista_aux) {
                        if (row.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    lista_filtrada = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = lista_filtrada
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                lista = results?.values as ArrayList<String>
                notifyDataSetChanged()
            }
        }
        lista = lista_aux
    }

    class VH(item: View):RecyclerView.ViewHolder(item) {
        val name: TextView = item.local_name_
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.elemento_, parent, false)

        view.setOnClickListener(View.OnClickListener {
            val intencao = Intent(context, DadosActivity::class.java)
            val nome = view.local_name_.text.toString()
            var uf: String? = null
            if(endereco == "estados") {
                uf = getEstado(nome)
            }

            intencao.putExtra("nome", nome)
            intencao.putExtra("uf", uf)
            intencao.putExtra("endereco", endereco)
            startActivity(context, intencao, null)
        })

        return VH(view)
    }

    override fun getItemCount(): Int {
        return lista_filtrada.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val dados = lista[position]
        holder.name.text = dados
    }

    fun getEstado(nome: String): String? {
        for(index in 0 .. listaJson.size - 1) {
            val elemento = listaJson[index]
            if(nome == elemento.state) {
                return elemento.uf.toString()
            }
        }
        return null
    }
}