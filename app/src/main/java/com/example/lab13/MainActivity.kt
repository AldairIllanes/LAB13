package com.example.lab13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.lab13.ejercicios.Ejercicio1AnimatedVisibility
import com.example.lab13.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Aquí ejecutas la pantalla del ejercicio que desees probar
                Ejercicio1AnimatedVisibility()
            }
        }
    }
}
