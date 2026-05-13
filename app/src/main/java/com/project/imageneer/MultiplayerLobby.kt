package com.project.imageneer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.project.imageneer.ui.theme.ImageneerTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.ui.draw.shadow

class MultiplayerLobby : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageneerTheme {
                MultiplayerLobbyScreen ()
            }
        }
    }
}

// ─── Colors ───────────────────────────────────────────────────────────────────
private val PurpleBackground = Color(0xFF7B3FE4)
private val PurpleDark       = Color(0xFF6A2FD0)
private val GreenButton      = Color(0xFF4CAF50)
private val GreenDark        = Color(0xFF388E3C)
private val OrangeButton     = Color(0xFFFF6B35)
private val OrangeDark       = Color(0xFFE55A25)
private val White            = Color(0xFFFFFFFF)
private val LightGray        = Color(0xFFF5F5F5)
private val HintGray         = Color(0xFFAAAAAA)
private val CardBackground   = Color(0xFFFFFFFF)

// ─── Logo Icon Placeholder ─────────────────────────────────────────────────────
// In a real project, replace this with your actual drawable resource.
// e.g. painterResource(id = R.drawable.ic_imageneer_logo)

@Composable
fun MultiplayerLobbyScreen(
    onCreateRoom: () -> Unit = {},
    onJoinRoom: (roomId: String) -> Unit = {}
) {
    var roomId by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PurpleBackground, PurpleDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo ──────────────────────────────────────────────────────────
            LogoSection()

            Spacer(modifier = Modifier.height(48.dp))

            // ── Create Room Button ────────────────────────────────────────────
            Button(
                onClick = onCreateRoom,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = GreenDark.copy(alpha = 0.4f),
                        spotColor = GreenDark.copy(alpha = 0.4f)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenButton
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Create Room",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Join Room Card ────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color.Black.copy(alpha = 0.25f),
                        spotColor = Color.Black.copy(alpha = 0.25f)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Join Room",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        textAlign = TextAlign.Center
                    )

                    // Room ID Input
                    OutlinedTextField(
                        value = roomId,
                        onValueChange = { roomId = it },
                        placeholder = {
                            Text(
                                text = "Input Room ID",
                                color = HintGray,
                                fontSize = 15.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = PurpleBackground,
                            unfocusedContainerColor = LightGray,
                            focusedContainerColor = LightGray,
                            cursorColor = PurpleBackground,
                            focusedTextColor = Color(0xFF1A1A1A),
                            unfocusedTextColor = Color(0xFF1A1A1A)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    // Join Button
                    Button(
                        onClick = { onJoinRoom(roomId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeButton,
                            disabledContainerColor = OrangeButton
                        ),
                        enabled = roomId.isNotBlank(),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Join",
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

// ─── Logo Section ─────────────────────────────────────────────────────────────
@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon badge with gradient background
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFF6B6B), Color(0xFFCC44CC))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "Imageneer Logo",
                tint = White,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Imageneer",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = White,
            letterSpacing = 0.5.sp
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(
    showBackground = true,
    backgroundColor = 0xFF7B3FE4,
    widthDp = 360,
    heightDp = 780,
    name = "MultiplayerLobby"
)
@Composable
fun MultiplayerLobbyPreview() {
    MaterialTheme {
        MultiplayerLobbyScreen()
    }
}