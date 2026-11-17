package com.example.lab13.ejercicios

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab13.ui.theme.MyApplicationTheme

/**
 * Ejercicio 3: Animación de Tamaño y Posición
 * Metáfora: "Inspección con Lupa (Zoom y Traslación)"
 *
 * 1. Botón fijo superior que altera el estado de inspección (acercado / alejado).
 * 2. animateDpAsState para animar el tamaño (tamano) y Modifier.offset para animar la posición (x, y).
 * 3. Demostración del orden de los modificadores (.offset -> .size -> .shadow -> .background).
 */
@Composable
fun Ejercicio3Screen() {
    // 1. Estado para controlar si el objeto está ampliado/cerca (true) o alejado (false)
    var estaAcercado by remember { mutableStateOf(false) }

    val tamano by animateDpAsState(
        targetValue = if (estaAcercado) 180.dp else 70.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "animacionTamano"
    )

    val offsetX by animateDpAsState(
        targetValue = if (estaAcercado) 0.dp else 90.dp,
        animationSpec = tween(durationMillis = 600),
        label = "animacionOffsetX"
    )

    val offsetY by animateDpAsState(
        targetValue = if (estaAcercado) 0.dp else (-130).dp,
        animationSpec = tween(durationMillis = 600),
        label = "animacionOffsetY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E293B), // Azul noche pizarra
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        // --- ÁREA CENTRAL DE OBSERVACIÓN / BASE DE LA LUPA ---
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(240.dp)
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Círculo concéntrico sutil
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .background(Color.White.copy(alpha = 0.03f), CircleShape)
            )
        }

        // --- BOTÓN CLARO EN POSICIÓN FIJA SUPERIOR (LUPA) ---
        Button(
            onClick = { estaAcercado = !estaAcercado },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
                .height(50.dp)
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(50)),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF0F172A)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text(
                text = if (estaAcercado) "🔍 Alejar objeto" else "🔍 Acercar con lupa",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        // --- OBJETO / CUADRO ANIMADO EN TAMAÑO Y POSICIÓN ---
        // Observa el orden de modificadores:
        // 1°. offset: sitúa el elemento en la coordenada X, Y animada
        // 2°. size: define la anchura y altura animada por animateDpAsState
        // 3°. shadow: proyecta la sombra calculada en la nueva posición y tamaño
        // 4°. background: pinta el fondo dentro de los límites resultantes
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = offsetX, y = offsetY) // <-- Posición modificada con Modifier.offset
                .size(tamano)                     // <-- Tamaño modificado con animateDpAsState
                .shadow(
                    elevation = if (estaAcercado) 24.dp else 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color(0xFF38BDF8).copy(alpha = 0.8f) // Resplandor cian
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0284C7), // Cian azulado
                            Color(0xFF6366F1)  // Índigo
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF38BDF8).copy(alpha = 0.8f),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icono central escalable
                Text(
                    text = if (estaAcercado) "💎" else "🔹",
                    fontSize = if (estaAcercado) 46.sp else 22.sp
                )

                // Texto descriptivo que solo se muestra cuando está ampliado
                if (estaAcercado) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ZOOM ACTIVO",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${tamano.value.toInt()} dp",
                        color = Color(0xFF7DD3FC),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Etiqueta informativa inferior fija
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.08f)
        ) {
            Text(
                text = if (estaAcercado) "Posición: Centro (0, 0) • Tamaño: Grande" else "Posición: Distante (+90, -130) • Tamaño: Pequeño",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEjercicio3() {
    MyApplicationTheme {
        Ejercicio3Screen()
    }
}
