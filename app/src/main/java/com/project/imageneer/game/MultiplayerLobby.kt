package com.project.imageneer.game

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.project.imageneer.R
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun MultiplayerLobbyScreen(navController: NavHostController) {
    var roomId by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Inisialisasi Firebase Ref ke node "ruang_multiplayer"
    val databaseRef = FirebaseDatabase.getInstance().getReference("ruang_multiplayer")

    // State tambahan untuk mengontrol indikator loading saat memproses database
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF7B3FE4), Color(0xFF6A2FD0))
                )
            )
    ) {
        // Tombol Kembali ke Dashboard
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(start = 16.dp, top = 48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFFFFFFF)
            )
        }

        // Overlay Loading jika sedang memproses create/join room
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFF6B6B), Color(0xFFCC44CC))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Imageneer Logo",
                        modifier = Modifier.size(80.dp) // Ukuran disesuaikan agar proporsional di dalam Box 72.dp
                    )
                }
                Text(
                    text = "Imageneer",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFFFFF)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Create Room Button (Tombol Hijau)
            Button(
                onClick = {
                    isLoading = true

                    // 1. Ambil data user yang sedang login saat ini dari Firebase Auth
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    // Ambil nama dari displayName, jika kosong gunakan email, jika kosong lagi gunakan "Host"
                    val namaUserLogin = currentUser?.displayName ?: currentUser?.email ?: "Host"
                    val username = namaUserLogin
                        .substringBefore("@")
                        .replaceFirstChar { it.uppercase() }
                    // Membuat 5 digit kode angka acak sebagai kode Room
                    val kodeAcak = (10000..99999).random().toString()

                    // 2. Masukkan nama asli user login ke dalam struktur data Room
                    val roomBaru = mapOf(
                        "roomId" to kodeAcak,
                        "status" to "waiting",
                        "pembuat" to username,
                        "pemain" to mapOf(
                            "host_player" to mapOf(
                                "nama" to username,
                                "skor" to 0
                            )
                        )
                    )

                    // Push data ke Firebase Realtime Database
                    databaseRef.child(kodeAcak).setValue(roomBaru)
                        .addOnSuccessListener {
                            isLoading = false
                            Toast.makeText(context, "Room $kodeAcak Berhasil Dibuat!", Toast.LENGTH_SHORT).show()
                            navController.navigate("multiplayer_waiting/$kodeAcak")
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = Color(0xFF388E3C).copy(alpha = 0.4f),
                        spotColor = Color(0xFF388E3C).copy(alpha = 0.4f)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text(
                    text = "Create Room",
                    color = Color(0xFFFFFFFF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Join Room Card (Kartu Putih)
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
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
                        color = Color(0xFF1A1A1A)
                    )

                    OutlinedTextField(
                        value = roomId,
                        onValueChange = { roomId = it },
                        placeholder = { Text(text = "Input Room ID", color = Color(0xFFAAAAAA)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color(0xFF7B3FE4),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5),
                            cursorColor = Color(0xFF7B3FE4)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Diubah ke Number karena kodenya acak angka 5 digit
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                    )

                    // Tombol Join (Tombol Oranye)
                    Button(
                        onClick = {
                            val targetRoomCode = roomId.trim()
                            isLoading = true

                            // 1. Ambil data user login untuk Guest
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            val namaGuestLogin = currentUser?.displayName ?: currentUser?.email ?: "Guest"

                            val targetRoomRef = databaseRef.child(targetRoomCode)

                            targetRoomRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        // 2. Masukkan nama asli Guest ke Firebase Realtime Database
                                        val guestData = mapOf(
                                            "nama" to namaGuestLogin, // <-- Nama dinamis dari Auth
                                            "skor" to 0
                                        )

                                        targetRoomRef.child("pemain").child("guest_player").setValue(guestData)
                                            .addOnSuccessListener {
                                                isLoading = false
                                                Toast.makeText(context, "Berhasil bergabung!", Toast.LENGTH_SHORT).show()
                                                navController.navigate("multiplayer_waiting/$targetRoomCode")
                                            }
                                            .addOnFailureListener { e ->
                                                isLoading = false
                                                Toast.makeText(context, "Gagal masuk: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(context, "Kode Room tidak valid!", Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    isLoading = false
                                    Toast.makeText(context, "Koneksi Bermasalah: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                        enabled = roomId.isNotBlank()
                    ) {
                        Text(
                            text = "Join",
                            color = Color(0xFFFFFFFF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MultiplayerLobbyPreview() {
    MaterialTheme {
        MultiplayerLobbyScreen(navController = rememberNavController())
    }
}