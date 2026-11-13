package com.example.lab13.ejercicios

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab13.R
import com.example.lab13.ui.theme.MyApplicationTheme

@Composable
fun Ejercicio2ColorAnimation() {
    // 1. Estado para alternar entre Luz Verde (true) y Luz Roja (false)
    var esVerde by remember { mutableStateOf(false) }

    // 2 & 3. animateColorAsState para cambiar el color del único foco de forma continua
    val colorFoco by animateColorAsState(
        targetValue = if (esVerde) Color(0xFF00E676) else Color(0xFFFF1744),
        animationSpec = tween(durationMillis = 650),
        label = "colorFocoSemaforo"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // --- FONDO CON LA IMAGEN DE LA MUÑECA ---
        Image(
            painter = painterResource(id = R.drawable.squid_game_doll),
            contentDescription = "Muñeca del Juego del Calamar",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa sutil de oscurecimiento para destacar el semáforo y los controles
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
        )

        // --- BOTÓN CLARO EN POSICIÓN FIJA SUPERIOR ---
        Button(
            onClick = { esVerde = !esVerde },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
                .height(50.dp)
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(50)),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF212121)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 3.dp
            )
        ) {
            Text(
                text = if (esVerde) "Cambiar a LUZ ROJA" else "Cambiar a LUZ VERDE",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (esVerde) Color(0xFFC62828) else Color(0xFF1B5E20)
            )
        }

        // --- SEMÁFORO DE UN SOLO FOCO (CAMBIO DE COLOR DINÁMICO) ---
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Estructura de la caja del semáforo
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(32.dp),
                        spotColor = colorFoco.copy(alpha = 0.8f) // Resplandor dinámico del foco activo
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2A2A2A),
                                Color(0xFF171717)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(
                        width = 4.dp,
                        color = Color(0xFF484848),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Único foco que cambia de color entre rojo y verde
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = CircleShape,
                            spotColor = colorFoco
                        )
                        .background(
                            color = colorFoco,
                            shape = CircleShape
                        )
                        .border(
                            width = 3.dp,
                            color = Color.White.copy(alpha = 0.6f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (esVerde) "🟢" else "🔴",
                        fontSize = 42.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Placa indicadora cuyo fondo también se anima suavemente con animateColorAsState
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = colorFoco
                    )
                    .background(
                        color = colorFoco,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(2.dp, Color.White, RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(
                    text = if (esVerde) "¡LUZ VERDE! (AVANZA)" else "¡LUZ ROJA! (ALTO)",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEjercicio2() {
    MyApplicationTheme {
        Ejercicio2ColorAnimation()
    }
}
