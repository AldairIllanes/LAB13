package com.example.lab13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab13.ejercicios.Ejercicio1AnimatedVisibility
import com.example.lab13.ejercicios.Ejercicio2ColorAnimation
import com.example.lab13.ejercicios.Ejercicio3Screen
import com.example.lab13.ejercicios.Ejercicio4Screen
import com.example.lab13.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainLabScreen()
            }
        }
    }
}

@Composable
fun MainLabScreen() {
    // Control del ejercicio actual (1 a 5)
    var ejercicioActual by remember { mutableIntStateOf(1) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                (1..5).forEach { numero ->
                    val seleccionado = ejercicioActual == numero
                    NavigationBarItem(
                        selected = seleccionado,
                        onClick = { ejercicioActual = numero },
                        label = {
                            Text(
                                text = "Ej. $numero",
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Surface(
                                shape = CircleShape,
                                color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$numero",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (ejercicioActual) {
                1 -> Ejercicio1AnimatedVisibility()
                2 -> Ejercicio2ColorAnimation()
                3 -> Ejercicio3Screen()
                4 -> Ejercicio4Screen()
                else -> {
                    // Placeholder temporal para los siguientes ejercicios
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ejercicio $ejercicioActual\n(Pendiente)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
