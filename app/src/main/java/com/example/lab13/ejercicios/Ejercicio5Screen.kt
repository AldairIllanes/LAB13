package com.example.lab13.ejercicios

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab13.R
import com.example.lab13.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Ejercicio Final: Animaciones Combinadas (Prototipo de videojuego)
 * Temática: Batalla épica — Caballero vs Dragón sobre un volcán en llamas
 *
 * Animaciones combinadas en una sola pantalla:
 *  - AnimatedVisibility   -> texto de daño flotante (aparece y desaparece)
 *  - animateColorAsState -> barra de vida (verde -> amarillo -> rojo) y destello de impacto
 *  - animateDpAsState    -> tamaño/posición de las barras, salto del ataque y sacudida
 *  - Modifier.offset     -> movimiento del caballero y del dragón
 *  - AnimatedContent     -> transición entre BATALLA / VICTORIA / DERROTA
 */
enum class EstadoCombate {
    BATALLA,
    VICTORIA,
    DERROTA
}

@Composable
fun Ejercicio5Screen() {
    val scope = rememberCoroutineScope()

    // --- Estados del juego ---
    var estadoCombate by remember { mutableStateOf(EstadoCombate.BATALLA) }
    var vidaCaballero by remember { mutableIntStateOf(100) }
    var vidaDragon by remember { mutableIntStateOf(100) }
    var bloqueado by remember { mutableStateOf(false) }

    // --- Estados de las animaciones de ataque / impacto ---
    var ataqueCaballero by remember { mutableStateOf(false) }
    var dragonGolpeado by remember { mutableStateOf(false) }
    var caballeroGolpeado by remember { mutableStateOf(false) }

    // --- Texto flotante de daño (AnimatedVisibility) ---
    var textoFlotante by remember { mutableStateOf<String?>(null) }
    var contadorTexto by remember { mutableIntStateOf(0) }

    fun mostrarTexto(texto: String) {
        textoFlotante = texto
        contadorTexto++
    }

    // Oculta el texto flotante después de 1.1 segundos
    LaunchedEffect(contadorTexto) {
        if (contadorTexto > 0) {
            delay(1100)
            textoFlotante = null
        }
    }

    fun reiniciar() {
        vidaCaballero = 100
        vidaDragon = 100
        estadoCombate = EstadoCombate.BATALLA
        bloqueado = false
        textoFlotante = null
        ataqueCaballero = false
        dragonGolpeado = false
        caballeroGolpeado = false
    }

    // Contraataque automático del Dragón tras la acción del jugador
    fun contraataqueEnemigo() {
        scope.launch {
            delay(550)
            caballeroGolpeado = true
            val danioEnemigo = Random.nextInt(17, 23)
            vidaCaballero = (vidaCaballero - danioEnemigo).coerceAtLeast(0)
            mostrarTexto("¡Dragón usa Llama! -$danioEnemigo HP 🔥")
            delay(550)
            caballeroGolpeado = false
            if (vidaCaballero <= 0) {
                estadoCombate = EstadoCombate.DERROTA
            } else {
                bloqueado = false
            }
        }
    }

    // Acciones del jugador
    fun atacar(danio: Int, critico: Boolean) {
        if (bloqueado || estadoCombate != EstadoCombate.BATALLA) return
        bloqueado = true
        ataqueCaballero = true
        dragonGolpeado = true
        vidaDragon = (vidaDragon - danio).coerceAtLeast(0)
        mostrarTexto(if (critico) "¡GOLPE CRÍTICO! -$danio HP 💥" else "¡Ataque exitoso! -$danio HP")

        scope.launch {
            delay(450)
            ataqueCaballero = false
            dragonGolpeado = false

            if (vidaDragon <= 0) {
                estadoCombate = EstadoCombate.VICTORIA
                bloqueado = false
                return@launch
            }
            contraataqueEnemigo()
        }
    }

    fun curar() {
        if (bloqueado || estadoCombate != EstadoCombate.BATALLA) return
        if (vidaCaballero >= 100) {
            mostrarTexto("¡El Caballero ya está al máximo! ❤️")
            return
        }
        bloqueado = true
        val curado = minOf(25, 100 - vidaCaballero)
        vidaCaballero += curado
        mostrarTexto("El Caballero recuperó +$curado HP 🧪")
        contraataqueEnemigo()
    }

    // --- Animaciones de posición (Modifier.offset) ---
    // El Caballero se lanza hacia arriba al atacar (física de resorte con rebote)
    val offsetYCaballero by animateDpAsState(
        targetValue = if (ataqueCaballero) (-46).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "saltoAtaqueCaballero"
    )
    // El Caballero tiembla lateralmente cuando recibe daño
    val offsetXCaballero by animateDpAsState(
        targetValue = if (caballeroGolpeado) 10.dp else 0.dp,
        animationSpec = tween(durationMillis = 120),
        label = "sacudidaCaballero"
    )
    // El Dragón se sacude lateralmente al recibir el golpe
    val offsetXDragon by animateDpAsState(
        targetValue = if (dragonGolpeado) 12.dp else 0.dp,
        animationSpec = tween(durationMillis = 120),
        label = "sacudidaDragon"
    )
    // Destello de impacto sobre el Dragón (transparencia -> blanco suave)
    val colorDestello by animateColorAsState(
        targetValue = if (dragonGolpeado) Color.White.copy(alpha = 0.45f) else Color.Transparent,
        animationSpec = tween(durationMillis = 150),
        label = "destelloImpacto"
    )
    // Destello de impacto sobre el Caballero (transparencia -> blanco suave)
    val colorDestelloCaballero by animateColorAsState(
        targetValue = if (caballeroGolpeado) Color.White.copy(alpha = 0.45f) else Color.Transparent,
        animationSpec = tween(durationMillis = 150),
        label = "destelloImpactoCaballero"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A0B08))
    ) {
        // Fondo del volcán en llamas (imagen real: lava, rocas y montañas)
        Image(
            painter = painterResource(id = R.drawable.fondo_volcan),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Sombra sutil para que los paneles de vida se lean sobre el fondo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A0B08).copy(alpha = 0.45f),
                            Color.Transparent,
                            Color(0xFF1A0B08).copy(alpha = 0.55f)
                        )
                    )
                )
        )

        // 5. AnimatedContent: transición entre BATALLA / VICTORIA / DERROTA
        AnimatedContent(
            targetState = estadoCombate,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 500)) togetherWith
                fadeOut(animationSpec = tween(durationMillis = 350))
            },
            label = "transicionCombate"
        ) { estado ->
            when (estado) {
                EstadoCombate.BATALLA -> PantallaCombate(
                    vidaCaballero = vidaCaballero,
                    vidaDragon = vidaDragon,
                    textoFlotante = textoFlotante,
                    offsetYCaballero = offsetYCaballero,
                    offsetXCaballero = offsetXCaballero,
                    offsetXDragon = offsetXDragon,
                    colorDestello = colorDestello,
                    colorDestelloCaballero = colorDestelloCaballero,
                    atacar = ::atacar,
                    curar = ::curar,
                    reiniciar = ::reiniciar
                )

                EstadoCombate.VICTORIA -> PantallaFinal(
                    emoji = "🏆",
                    titulo = "¡VICTORIA!",
                    colorTitulo = Color(0xFFFDE047),
                    mensaje = "El Dragón fue derrotado.\nEl Caballero ganó 3500 XP y subió al NV.36.",
                    textoBoton = "🔄 Volver a luchar",
                    reiniciar = ::reiniciar
                )

                EstadoCombate.DERROTA -> PantallaFinal(
                    emoji = "💀",
                    titulo = "DERROTA",
                    colorTitulo = Color(0xFFF87171),
                    mensaje = "El Caballero cayó en batalla.\nLo llevaron al castillo a curarse.",
                    textoBoton = "🔄 Reintentar",
                    reiniciar = ::reiniciar
                )
            }
        }
    }
}

/**
 * Pantalla de combate: paneles de vida, arena con los personajes y botones de acción
 */
@Composable
private fun PantallaCombate(
    vidaCaballero: Int,
    vidaDragon: Int,
    textoFlotante: String?,
    offsetYCaballero: Dp,
    offsetXCaballero: Dp,
    offsetXDragon: Dp,
    colorDestello: Color,
    colorDestelloCaballero: Color,
    atacar: (Int, Boolean) -> Unit,
    curar: () -> Unit,
    reiniciar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Barra de vida del enemigo ---
        PanelVida(nombre = "DRAGÓN", nivel = 50, vidaActual = vidaDragon, vidaMax = 100)

        Spacer(modifier = Modifier.height(12.dp))

        // --- ARENA DEL VOLCÁN ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // El Dragón arriba (se sacude y destella al recibir daño)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = offsetXDragon)
                    .size(145.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.28f),
                        shape = CircleShape
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dragon_sprite),
                    contentDescription = "Dragón",
                    modifier = Modifier.size(115.dp),
                    contentScale = ContentScale.Fit
                )
                // Capa de destello de impacto (parpadea en blanco al recibir daño)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(colorDestello, CircleShape)
                )
            }

            // El Caballero abajo (se lanza al atacar y tiembla al recibir daño)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
                    .offset(x = offsetXCaballero, y = offsetYCaballero)
                    .size(130.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.28f),
                        shape = CircleShape
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.caballero_sprite),
                    contentDescription = "Caballero",
                    modifier = Modifier.size(105.dp),
                    contentScale = ContentScale.Fit
                )
                // Capa de destello de impacto sobre el Caballero
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(colorDestelloCaballero, CircleShape)
                )
            }

            // 1. AnimatedVisibility: texto de daño flotante
            androidx.compose.animation.
            AnimatedVisibility(
                visible = textoFlotante != null,
                enter = fadeIn(animationSpec = tween(durationMillis = 150)) +
                        slideInVertically(animationSpec = tween(durationMillis = 300)) { -it / 2 },
                exit = fadeOut(animationSpec = tween(durationMillis = 350)),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 110.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFEF4444),
                    border = BorderStroke(2.dp, Color(0xFFFCA5A5))
                ) {
                    Text(
                        text = textoFlotante ?: "",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // --- Barra de vida del Caballero ---
        PanelVida(nombre = "CABALLERO", nivel = 36, vidaActual = vidaCaballero, vidaMax = 100)

        Spacer(modifier = Modifier.height(14.dp))

        // --- Panel de comandos del entrenador ---
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BotonAccion(
                    texto = "⚔️ Tajo crítico",
                    colorFondo = Color(0xFFEAB308),
                    colorTexto = Color(0xFF000000),
                    modifier = Modifier.weight(1f)
                ) { atacar(30, true) }

                BotonAccion(
                    texto = "🛡️ Embate",
                    colorFondo = Color(0xFF7DD3FC),
                    colorTexto = Color(0xFF000000),
                    modifier = Modifier.weight(1f)
                ) { atacar(18, false) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BotonAccion(
                    texto = "🧪 Poción +25",
                    colorFondo = Color(0xFF22C55E),
                    colorTexto = Color(0xFF000000),
                    modifier = Modifier.weight(1f)
                ) { curar() }

                BotonAccion(
                    texto = "🔄 Reiniciar",
                    colorFondo = Color(0xFF94A3B8),
                    colorTexto = Color(0xFF000000),
                    modifier = Modifier.weight(1f)
                ) { reiniciar() }
            }
        }
    }
}

/**
 * Pantalla final: Victoria o Derrota (transiciona con AnimatedContent)
 */
@Composable
private fun PantallaFinal(
    emoji: String,
    titulo: String,
    colorTitulo: Color,
    mensaje: String,
    textoBoton: String,
    reiniciar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = emoji, fontSize = 84.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = titulo,
            color = colorTitulo,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = mensaje,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = reiniciar,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(50)),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF0F172A)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(text = textoBoton, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Panel de vida estilo épico con animateDpAsState (ancho) y animateColorAsState (color)
 */
@Composable
private fun PanelVida(nombre: String, nivel: Int, vidaActual: Int, vidaMax: Int) {
    val porcentaje = (vidaActual.toFloat() / vidaMax).coerceIn(0f, 1f)

    // 2. animateColorAsState: la barra pasa de verde -> amarillo -> rojo crítico
    val colorBarra by animateColorAsState(
        targetValue = when {
            porcentaje > 0.5f -> Color(0xFF22C55E)
            porcentaje > 0.2f -> Color(0xFFFBBF24)
            else -> Color(0xFFEF4444)
        },
        animationSpec = tween(durationMillis = 500),
        label = "colorBarraVida"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0F172A).copy(alpha = 0.88f),
        border = BorderStroke(2.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$nombre  NV.$nivel",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "HP $vidaActual/$vidaMax",
                    color = colorBarra,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3. animateDpAsState: el ancho de la barra se reduce de forma suave
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .background(Color(0xFF1E293B), RoundedCornerShape(50))
                    .border(1.dp, Color(0xFF475569), RoundedCornerShape(50))
            ) {
                val anchoDisponible = maxWidth - 4.dp
                val anchoBarra by animateDpAsState(
                    targetValue = anchoDisponible * porcentaje,
                    animationSpec = tween(durationMillis = 500),
                    label = "anchoBarraVida"
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(2.dp)
                        .height(10.dp)
                        .width(anchoBarra)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(colorBarra, colorBarra.copy(alpha = 0.65f))
                            ),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}

/**
 * Botón de acción del caballero
 */
@Composable
private fun BotonAccion(
    texto: String,
    colorFondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,
            contentColor = colorTexto
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
        Text(
            text = texto,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEjercicio5() {
    MyApplicationTheme {
        Ejercicio5Screen()
    }
}
