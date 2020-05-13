package com.ap8.api_covid

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_mundo.setOnClickListener(View.OnClickListener {
            val intencao = Intent(this, MundoActivity::class.java)
            ContextCompat.startActivity(this, intencao, null)
        })

        btn_paises.setOnClickListener(View.OnClickListener {
            val intencao = Intent(this, PaisesActivity::class.java)
            ContextCompat.startActivity(this, intencao, null)
        })

        btn_estados.setOnClickListener(View.OnClickListener {
            val intencao = Intent(this, EstadosActivity::class.java)
            ContextCompat.startActivity(this, intencao, null)
        })
    }
}
