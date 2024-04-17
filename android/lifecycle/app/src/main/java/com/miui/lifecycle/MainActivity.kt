package com.miui.lifecycle

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.miui.lifecycle.lifecycle.DiceRollViewModel
import com.miui.lifecycle.lifecycle.MyObserver
import androidx.activity.viewModels


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.lifecycle.addObserver(MyObserver())


        val viewModel: DiceRollViewModel by viewModels()

        viewModel.uiState.observe(this) { uiState ->
            findViewById<TextView>(R.id.textView).text = uiState.numberOfRolls.toString()
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            viewModel.rollDice()
        }
    }
}