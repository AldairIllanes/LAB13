package com.example.lab13.ejercicios

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab13.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

/**
 * 1. Definición de los tres estados solicitados por el laboratorio
 */
enum class EstadoPokedex {
    CARGANDO,
    CONTENIDO,
    ERROR
}

/**
 * Ejercicio 4: Cambio de Contenido con AnimatedContent
 * Temática: Pantalla de la Pokédex (Pokémon) con flujo automático
 *
 * 1. Define tres estados (Cargando, Contenido y Error).
 * 2. Utiliza AnimatedContent para realizar la transición fluida entre estos estados.
 * 3. Transición con fadeIn y fadeOut en tiempos personalizados.
 * 4. Al abrir, inicia automáticamente "de una": escanea 2 segundos y muestra a Pikachu.
 */
@Composable
fun Ejercicio4Screen() {
    // 1. Estado actual de la Pokédex
    var estadoActual by remember { mutableStateOf(EstadoPokedex.CARGANDO) }
    var contadorEscaneo by remember { mutableIntStateOf(0) }
    var alternarFallo by remember { mutableStateOf(false) }

    // Flujo automático: inicia en CARGANDO y pasa "de una" a CONTENIDO o ERROR tras 2.2 segundos
    LaunchedEffect(contadorEscaneo) {
        estadoActual = EstadoPokedex.CARGANDO
        delay(2200) // Simulación de escaneo realista
        estadoActual = if (alternarFallo) EstadoPokedex.ERROR else EstadoPokedex.CONTENIDO
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDC2626), // Rojo Pokédex
                        Color(0xFF991B1B)  // Rojo profundo
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- PARTE SUPERIOR DE LA POKÉDEX (SENSORES Y LUCES) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lente azul grande de escaneo
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape, spotColor = Color(0xFF38BDF8))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFE0F2FE), Color(0xFF0284C7), Color(0xFF0369A1))
                            ),
                            shape = CircleShape
                        )
                        .border(4.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.TopStart
                ) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(12.dp)
                            .background(Color.White.copy(alpha = 0.8f), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Tres pequeñas luces LED indicadoras (Rojo, Amarillo, Verde)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LuzIndicadora(Color(0xFFEF4444))
                    LuzIndicadora(Color(0xFFFBBF24))
                    LuzIndicadora(Color(0xFF22C55E))
                }
            }

            // --- CARCASA Y PANTALLA DIGITAL DE LA POKÉDEX ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp))
                    .background(Color(0xFFD1D5DB), RoundedCornerShape(24.dp))
                    .border(3.dp, Color(0xFF9CA3AF), RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                // Marco interior oscuro biselado (también puedes tocar la pantalla para volver a escanear)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .border(3.dp, Color(0xFF0F172A), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 2 & 3. AnimatedContent con transición fadeIn y fadeOut personalizada
                    AnimatedContent(
                        targetState = estadoActual,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(durationMillis = 500)) togetherWith
                            fadeOut(animationSpec = tween(durationMillis = 350))
                        },
                        label = "transicionPokedex"
                    ) { estado ->
                        when (estado) {
                            EstadoPokedex.CARGANDO -> PantallaCargando()
                            EstadoPokedex.CONTENIDO -> PantallaContenidoPikachu()
                            EstadoPokedex.ERROR -> PantallaError()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- ÚNICO BOTÓN CLARO PARA REINICIAR O MOSTRAR EL ERROR ---
            Button(
                onClick = {
                    // Alterna el resultado en el siguiente escaneo para que se aprecien todos los estados
                    alternarFallo = !alternarFallo
                    contadorEscaneo++
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(50.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(50)),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF0F172A)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = when (estadoActual) {
                        EstadoPokedex.CARGANDO -> "⏳ Escaneando..."
                        EstadoPokedex.CONTENIDO -> "🔄 Escanear de nuevo"
                        EstadoPokedex.ERROR -> "⚠️ Reintentar conexión"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
        }
    }
}

/**
 * Estado 1: Cargando datos del Pokémon
 */
@Composable
fun PantallaCargando() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF38BDF8),
            strokeWidth = 4.dp,
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "INICIANDO POKÉDEX...",
            color = Color(0xFF38BDF8),
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Conectando con la base de datos de Kanto",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Estado 2: Contenido encontrado con éxito (Pikachu)
 */
@Composable
fun PantallaContenidoPikachu() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Avatar o representación gráfica de Pikachu
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFEF08A), Color(0xFFEAB308))
                    ),
                    shape = CircleShape
                )
                .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⚡", fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "PIKACHU #025",
            color = Color(0xFFFDE047),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Badge de tipo eléctrico
        Surface(
            shape = RoundedCornerShape(50),
            color = Color(0xFFEAB308).copy(alpha = 0.25f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE047))
        ) {
            Text(
                text = "TIPO ELÉCTRICO",
                color = Color(0xFFFDE047),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "¡Datos registrados con éxito en la Pokédex!",
            color = Color(0xFF86EFAC),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Estado 3: Error de conexión o no encontrado
 */
@Composable
fun PantallaError() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(Color(0xFFEF4444).copy(alpha = 0.15f), CircleShape)
                .border(2.dp, Color(0xFFEF4444), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⚠️", fontSize = 40.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ERROR DE CONEXIÓN",
            color = Color(0xFFF87171),
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "No se pudo sincronizar con el Laboratorio del Prof. Oak.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/**
 * Luz LED indicadora superior de la Pokédex
 */
@Composable
fun LuzIndicadora(color: Color) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .background(color, CircleShape)
            .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEjercicio4() {
    MyApplicationTheme {
        Ejercicio4Screen()
    }
}
