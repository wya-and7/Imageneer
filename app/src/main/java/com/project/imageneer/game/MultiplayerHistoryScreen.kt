package com.project.imageneer.game

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.imageneer.data.MatchHistory // IMPORT MODEL DATA BARU

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplayerHistoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    // State untuk menampung data asli dari Firebase
    var historyList by remember { mutableStateOf<List<MatchHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Mengambil UID pengguna yang sedang login
    val myUid = currentUser?.uid ?: ""

    LaunchedEffect(myUid) {
        if (myUid.isEmpty()) {
            isLoading = false
            Toast.makeText(context, "Sesi user tidak ditemukan. Silakan login kembali.", Toast.LENGTH_LONG).show()
            return@LaunchedEffect
        }

        val historyRef = FirebaseDatabase.getInstance().getReference("riwayat_multiplayer")

        historyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<MatchHistory>()

                if (!snapshot.exists()) {
                    Log.d("FirebaseHistory", "Node 'riwayat_multiplayer' kosong atau tidak ada.")
                }

                for (data in snapshot.children) {
                    // Ambil UID Host & Guest dari database (Gunakan trim() untuk mengantisipasi whitespace)
                    val pemainHostUid = (data.child("hostUid").getValue(String::class.java) ?: "").trim()
                    val pemainGuestUid = (data.child("guestUid").getValue(String::class.java) ?: "").trim()
                    val currentUidClean = myUid.trim()

                    // COCOKKAN UID
                    if (currentUidClean == pemainHostUid || currentUidClean == pemainGuestUid) {
                        val id = data.key ?: ""
                        val date = data.child("tanggal").getValue(String::class.java) ?: "Unknown Date"

                        val hostName = data.child("hostName").getValue(String::class.java) ?: "Player 1"
                        val guestName = data.child("guestName").getValue(String::class.java) ?: "Player 2"

                        val hostScore = data.child("hostScore").getValue(Int::class.java) ?: 0
                        val guestScore = data.child("guestScore").getValue(Int::class.java) ?: 0

                        // Kondisional penentuan posisi nama Anda vs Lawan
                        val match = if (currentUidClean == pemainHostUid) {
                            MatchHistory(id, date, hostName, guestName, hostScore, guestScore)
                        } else {
                            MatchHistory(id, date, guestName, hostName, guestScore, hostScore)
                        }
                        tempList.add(match)
                    }
                }

                // Urutkan riwayat dari yang paling baru
                historyList = tempList.reversed()
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                Toast.makeText(context, "Gagal memuat riwayat: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp)
        ) {
            // TOP BAR
            TopAppBar(
                title = {
                    Text(text = "Match History", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            // LOADING & KONDISIONAL LIST
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (historyList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Belum ada riwayat pertandingan.", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(historyList) { item ->
                        HistoryCard(match = item)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(match: MatchHistory) {
    val statusText: String
    val statusColor: Color
    val statusIcon: @Composable () -> Unit

    when {
        match.myScore > match.enemyScore -> {
            statusText = "WIN"
            statusColor = Color(0xFF22C55E)
            statusIcon = { Icon(Icons.Default.CheckCircle, contentDescription = "Win", tint = statusColor, modifier = Modifier.size(28.dp)) }
        }
        match.myScore < match.enemyScore -> {
            statusText = "LOSE"
            statusColor = Color(0xFFEF4444)
            statusIcon = { Icon(Icons.Default.Clear, contentDescription = "Lose", tint = statusColor, modifier = Modifier.size(28.dp)) }
        }
        else -> {
            statusText = "DRAW"
            statusColor = Color(0xFFFFB703)
            statusIcon = { Icon(Icons.Default.Refresh, contentDescription = "Draw", tint = statusColor, modifier = Modifier.size(28.dp)) }
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = match.date, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Surface(color = statusColor.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                    Text(text = statusText, color = statusColor, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = match.myName, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp, maxLines = 1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = match.myScore.toString(), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = if (match.myScore >= match.enemyScore) Color(0xFF7B3FE4) else Color.Gray)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
                    statusIcon()
                    Text(text = "VS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.LightGray)
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = match.enemyName, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp, maxLines = 1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = match.enemyScore.toString(), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = if (match.enemyScore >= match.myScore) Color(0xFF7B3FE4) else Color.Gray)
                }
            }
        }
    }
}