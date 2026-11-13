package com.example.lab13.ejercicios

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab13.ui.theme.MyApplicationTheme

/**
 * Ejercicio 1: Animación de Visibilidad con AnimatedVisibility
 *
 * 1. Botón en posición fija para alternar la visibilidad de la caja.
 * 2. AnimatedVisibility con efecto de gravedad / caída (slideInVertically) y salida suave.
 * 3. Transiciones configuradas con fadeIn, fadeOut y especificaciones de tiempo / física de resorte.
 */
@Composable
fun Ejercicio1AnimatedVisibility() {
    // 1. Estado para alternar la visibilidad de la caja
    var esVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // --- FONDO: CIELO Y CÉSPED ---
        Column(modifier = Modifier.fillMaxSize()) {
            // CIELO (Parte superior)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF29B6F6), // Azul cielo vivo
                                Color(0xFF81D4FA), // Azul celeste
                                Color(0xFFE1F5FE)  // Horizonte claro
                            )
                        )
                    )
            ) {
                // Sol en la esquina superior derecha
                Box(
                    modifier = Modifier
                        .padding(top = 40.dp, end = 32.dp)
                        .size(64.dp)
                        .align(Alignment.TopEnd)
                        .background(Color(0xFFFFEE58), CircleShape)
                )

                // Nube decorativa 1
                Nube(
                    modifier = Modifier
                        .padding(top = 110.dp, start = 30.dp)
                        .align(Alignment.TopStart)
                )

                // Nube decorativa 2
                Nube(
                    modifier = Modifier
                        .padding(top = 160.dp, end = 40.dp)
                        .align(Alignment.TopEnd)
                )
            }

            // CÉSPED (Parte inferior donde aterriza la caja)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF66BB6A), // Verde césped claro
                                Color(0xFF388E3C), // Verde medio
                                Color(0xFF1B5E20)  // Verde tierra profundo
                            )
                        )
                    )
            ) {
                // Detalle de franja superior de césped
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(Color(0xFF81C784).copy(alpha = 0.5f))
                )
            }
        }

        // --- BOTÓN EN LUGAR FIJO (PARTE SUPERIOR) ---
        // Al estar posicionado aquí, nunca se mueve cuando la caja aparece o desaparece
        Button(
            onClick = { esVisible = !esVisible },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp)
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (esVisible) Color(0xFFD32F2F) else Color(0xFF1976D2)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = if (esVisible) "Ocultar caja" else "Soltar caja",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // --- ZONA DONDE CAE LA CAJA SOBRE EL CÉSPED ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp), // Acomodada para posarse sobre el césped
            contentAlignment = Alignment.BottomCenter
        ) {
            // 2 & 3. AnimatedVisibility con efecto de caída desde el cielo y salida suave
            AnimatedVisibility(
                visible = esVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 350)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { -it * 4 } // Cae desde arriba simulando la gravedad
                        ),
                exit = fadeOut(animationSpec = tween(durationMillis = 300)) +
                        slideOutVertically(
                            animationSpec = tween(durationMillis = 350),
                            targetOffsetY = { it * 2 } // Desciende suavemente al ocultarse
                        )
            ) {
                CajaDeCarton()
            }
        }
    }
}

/**
 * Componente que diseña una auténtica caja de cartón de envíos:
 * - Textura color kraft
 * - Cinta adhesiva central de embalaje
 * - Línea de solapas
 * - Etiqueta blanca de envío con código de barras
 * - Sello rojo de FRÁGIL
 */
@Composable
fun CajaDeCarton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(150.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0xFF1B5E20).copy(alpha = 0.6f)
            )
            .background(
                color = Color(0xFFC68B59), // Cartón kraft
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = Color(0xFFA56D3B),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // Cinta de embalaje vertical central
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(34.dp)
                .align(Alignment.Center)
                .background(Color(0xFFE5BE84).copy(alpha = 0.9f))
                .border(
                    width = 1.dp,
                    color = Color(0xFFC99F5E).copy(alpha = 0.5f)
                )
        )

        // Línea horizontal de corte de solapas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.Center)
                .background(Color(0xFFA56D3B).copy(alpha = 0.7f))
        )

        // Etiqueta de envío blanca con código de barras simulado
        Surface(
            modifier = Modifier
                .padding(top = 14.dp, start = 12.dp)
                .size(width = 44.dp, height = 32.dp),
            shape = RoundedCornerShape(4.dp),
            color = Color(0xFFFFFDF5),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Código de barras
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(2, 1, 3, 1, 2, 1, 3).forEach { barWidth ->
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(barWidth.dp)
                                .background(Color(0xFF333333))
                        )
                    }
                }
                // Línea de texto simulada
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(2.dp)
                        .background(Color.Gray.copy(alpha = 0.6f))
                )
            }
        }

        // Sello "FRÁGIL" en la esquina inferior derecha
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "FRÁGIL",
                color = Color(0xFFC62828),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "⬆⬆",
                color = Color(0xFFC62828),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Nube decorativa sencilla para ambientar el cielo
 */
@Composable
fun Nube(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color.White.copy(alpha = 0.85f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .offset(x = (-8).dp, y = (-4).dp)
                .background(Color.White.copy(alpha = 0.85f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(26.dp)
                .offset(x = (-16).dp)
                .background(Color.White.copy(alpha = 0.85f), CircleShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEjercicio1() {
    MyApplicationTheme {
        Ejercicio1AnimatedVisibility()
    }
}
