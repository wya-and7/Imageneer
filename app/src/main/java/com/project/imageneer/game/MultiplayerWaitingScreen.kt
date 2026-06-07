package com.project.imageneer.game

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun MultiplayerWaitingScreen(navController: NavHostController, roomId: String) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    // State untuk memantau status pemain yang bergabung
    var namaHost by remember { mutableStateOf("Menghubungkan...") }
    var namaGuest by remember { mutableStateOf("Menunggu Pemain...") }
    var isGuestJoined by remember { mutableStateOf(false) }

    // PENGAMAN: Mencegah navigasi dipicu berkali-kali (Double Navigate Bug)
    var isNavigating by remember { mutableStateOf(false) }

    // Animasi denyut nadi (Pulse) untuk indikator menunggu pemain
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scaleFactor by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Mendengarkan perubahan data secara Realtime dari Firebase
    val roomRef = remember(roomId) {
        FirebaseDatabase.getInstance().getReference("ruang_multiplayer").child(roomId)
    }

    val databaseListener = remember {
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Ambil Nama Host
                    val hostName = snapshot.child("pemain").child("host_player").child("nama").getValue(String::class.java)
                    if (hostName != null) namaHost = hostName

                    // Periksa apakah Guest sudah masuk
                    val guestSnapshot = snapshot.child("pemain").child("guest_player")
                    if (guestSnapshot.exists()) {
                        val guestName = guestSnapshot.child("nama").getValue(String::class.java)
                        namaGuest = guestName ?: "Guest"
                        isGuestJoined = true

                        // Jalankan navigasi HANYA JIKA belum sedang berpindah halaman
                        if (!isNavigating) {
                            isNavigating = true
                            Toast.makeText(context, "Game Dimulai!", Toast.LENGTH_SHORT).show()

                            navController.navigate("multiplayer_game/$roomId") {
                                popUpTo("multiplayer_lobby") { inclusive = false }
                            }
                        }
                    }
                } else {
                    if (!isNavigating) {
                        Toast.makeText(context, "Room tidak lagi tersedia", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memantau ruang: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Gunakan LaunchedEffect khusus untuk menempelkan listener ke Firebase saat awal masuk halaman
    LaunchedEffect(roomId) {
        if (isPreview) {
            namaHost = "Achmad Wildan"
            namaGuest = "Menunggu Pemain..."
        } else {
            roomRef.addValueEventListener(databaseListener)
        }
    }

    // Gunakan DisposableEffect untuk membersihkan listener saat keluar halaman (Pencegahan Memory Leak)
    DisposableEffect(roomId) {
        onDispose {
            if (!isPreview) {
                roomRef.removeEventListener(databaseListener)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF7B3FE4), Color(0xFF6A2FD0))
                )
            )
    ) {
        // Tombol Kembali / Keluar dari Room
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(start = 16.dp, top = 48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Teks Header Informasi Room
            Text(
                text = "RUANG TUNGGU",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFCE7F3),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Kode Akses Utama untuk dibagikan ke teman
            Text(
                text = roomId,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFF6B35),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Bagikan kode di atas kepada teman Anda untuk bermain bersama",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // PANEL PLAYER 1 (HOST)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(72.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = namaHost, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                        Text(text = "Pembuat Room", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xFF4CAF50))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Teks VS Pemisah
            Text(
                text = "V S",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PANEL PLAYER 2 (GUEST)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isGuestJoined) Color.White else Color.White.copy(alpha = 0.15f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .scale(if (!isGuestJoined) scaleFactor else 1f),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isGuestJoined) 6.dp else 0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = namaGuest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isGuestJoined) Color(0xFF1A1A1A) else Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = if (isGuestJoined) "Pemain Kedua" else "Mencari lawan...",
                            fontSize = 12.sp,
                            color = if (isGuestJoined) Color(0xFFFF6B35) else Color.White.copy(alpha = 0.4f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (isGuestJoined) Color(0xFF4CAF50) else Color(0xFFFF6B35))
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = Color.White.copy(alpha = 0.7f),
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Preview
@Composable
fun MultiplayerWaitingPreview() {
    MaterialTheme {
        MultiplayerWaitingScreen(navController = rememberNavController(), roomId = "58214")
    }
}