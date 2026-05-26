package com.project.imageneer.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.imageneer.R
import com.project.imageneer.ui.theme.ImageneerTheme

@Composable
fun SoloGameScreen(navController: NavHostController) {
    var jawabanUser by remember { mutableStateOf("") }
    var skor by remember { mutableIntStateOf(0) } // State untuk tracking skor game

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7C3AED)) // Latar belakang ungu sesuai gambar
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 50.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ─── TOP BAR (EXIT & SKOR) ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol Exit
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(50.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        text = "Exit",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Status Skor
                Box(
                    modifier = Modifier
                        .background(Color(0xFF22C55E), shape = RoundedCornerShape(50.dp))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .height(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Skor : $skor",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(75.dp))

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    // Container Gambar Tebakan
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(Color(0xFFFCE7F3), shape = RoundedCornerShape(16.dp)), // Placeholder warna pink cerah sesuai asset gambar game tebakan
                        contentAlignment = Alignment.Center
                    ) {
                        // Pasang file gambar kuis Anda di sini (sementara menggunakan logo project)
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Gambar Tebakan Game",
                            modifier = Modifier.size(180.dp)
                        )
                    }

                    // Input Text Field (Border Ungu Transparan saat Unfocused)
                    OutlinedTextField(
                        value = jawabanUser,
                        onValueChange = { jawabanUser = it },
                        placeholder = {
                            Text(
                                text = "Masukan Jawabanmu",
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(28.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF7C3AED),
                            unfocusedBorderColor = Color(0xFFC084FC),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    // Tombol Jawab Hijau Panjang
                    Button(
                        onClick = {
                            // TODO: Taruh logika pengecekan jawaban / penambahan skor di sini
                            if (jawabanUser.isNotBlank()) {
                                // Contoh simulasi jika jawaban benar
                                skor += 10
                                jawabanUser = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22C55E),
                            disabledContainerColor = Color(0xFF22C55E).copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = jawabanUser.isNotBlank()
                    ) {
                        Text(
                            text = "Jawab",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SoloGameScreenPreview() {
    ImageneerTheme {
        SoloGameScreen(
            navController = rememberNavController()
        )
    }
}