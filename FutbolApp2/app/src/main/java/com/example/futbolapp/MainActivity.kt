package com.example.futbolapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================
// COLORES PERSONALIZADOS
// ============================================
val VerdeOscuro = Color(0xFF1B5E20)
val VerdeMedio = Color(0xFF2E7D32)
val VerdeClaro = Color(0xFFE8F5E9)
val FondoClaro = Color(0xFFF5F5F5)
val Blanco = Color(0xFFFFFFFF)
val GrisTexto = Color(0xFF424242)

// ============================================
// ACTIVIDAD PRINCIPAL
// ============================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FutbolTema {
                PantallaMarcador()
            }
        }
    }
}

// ============================================
// TEMA
// ============================================
@Composable
fun FutbolTema(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = VerdeOscuro,
            onPrimary = Blanco,
            secondary = VerdeMedio,
            onSecondary = Blanco,
            background = FondoClaro,
            surface = Blanco,
            onSurface = GrisTexto
        ),
        content = content
    )
}

// ============================================
// PANTALLA PRINCIPAL
// ============================================
@Composable
fun PantallaMarcador() {
    // Estado con rememberSaveable para sobrevivir a la rotación
    var nombreLocal by rememberSaveable { mutableStateOf("") }
    var nombreVisitante by rememberSaveable { mutableStateOf("") }
    var golesLocal by rememberSaveable { mutableIntStateOf(0) }
    var golesVisitante by rememberSaveable { mutableIntStateOf(0) }

    // Nombres alternativos si están vacíos
    val nombreLocalMostrar = nombreLocal.ifBlank { "Local" }
    val nombreVisitanteMostrar = nombreVisitante.ifBlank { "Visitante" }

    // Scroll para pantallas pequeñas y teclado
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FondoClaro
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ---------- TÍTULO ----------
            Text(
                text = "Marcador de fútbol",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // ---------- TARJETAS DE EQUIPOS ----------
            TarjetaEquipo(
                nombre = nombreLocal,
                onNombreChange = { nuevo ->
                    if (nuevo.length <= 30) nombreLocal = nuevo
                },
                goles = golesLocal,
                onAgregarGol = { golesLocal++ },
                onQuitarGol = { if (golesLocal > 0) golesLocal-- },
                esLocal = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TarjetaEquipo(
                nombre = nombreVisitante,
                onNombreChange = { nuevo ->
                    if (nuevo.length <= 30) nombreVisitante = nuevo
                },
                goles = golesVisitante,
                onAgregarGol = { golesVisitante++ },
                onQuitarGol = { if (golesVisitante > 0) golesVisitante-- },
                esLocal = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ---------- MARCADOR CONJUNTO ----------
            MarcadorConjunto(
                nombreLocal = nombreLocalMostrar,
                nombreVisitante = nombreVisitanteMostrar,
                golesLocal = golesLocal,
                golesVisitante = golesVisitante
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- BOTÓN REINICIAR ----------
            Button(
                onClick = {
                    golesLocal = 0
                    golesVisitante = 0
                    // Los nombres se conservan intencionalmente
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeOscuro
                )
            ) {
                Text(
                    text = "Reiniciar marcador",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ============================================
// TARJETA DE EQUIPO (reutilizable)
// ============================================
@Composable
fun TarjetaEquipo(
    nombre: String,
    onNombreChange: (String) -> Unit,
    goles: Int,
    onAgregarGol: () -> Unit,
    onQuitarGol: () -> Unit,
    esLocal: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Etiqueta de equipo
            Text(
                text = if (esLocal) "Equipo local" else "Equipo visitante",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VerdeMedio,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Campo de nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre del equipo") },
                placeholder = { Text(if (esLocal) "Local" else "Visitante") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    Text(
                        text = "${nombre.length}/30",
                        fontSize = 12.sp,
                        color = if (nombre.length >= 30) Color.Red else GrisTexto
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Número grande de goles
            Text(
                text = goles.toString(),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro
            )

            Text(
                text = "Goles",
                fontSize = 14.sp,
                color = GrisTexto
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de goles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Quitar gol (deshabilitado si goles == 0)
                OutlinedButton(
                    onClick = onQuitarGol,
                    enabled = goles > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Quitar gol",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Botón Agregar gol
                Button(
                    onClick = onAgregarGol,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeMedio
                    )
                ) {
                    Text(
                        text = "Agregar gol",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ============================================
// MARCADOR CONJUNTO + MENSAJE DE GANADOR
// ============================================
@Composable
fun MarcadorConjunto(
    nombreLocal: String,
    nombreVisitante: String,
    golesLocal: Int,
    golesVisitante: Int
) {
    // Determinar mensaje
    val mensaje = when {
        golesLocal > golesVisitante -> "Va ganando $nombreLocal"
        golesVisitante > golesLocal -> "Va ganando $nombreVisitante"
        else -> "Empate"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VerdeClaro),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Marcador",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VerdeMedio
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Marcador grande: Local X - Y Visitante
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nombreLocal,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdeOscuro,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "  $golesLocal - $golesVisitante  ",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro
                )

                Text(
                    text = nombreVisitante,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdeOscuro,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mensaje de ganador o empate
            Text(
                text = mensaje,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro
            )
        }
    }
}

// ============================================
// PREVIEW
// ============================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaMarcador() {
    FutbolTema {
        PantallaMarcador()
    }
}