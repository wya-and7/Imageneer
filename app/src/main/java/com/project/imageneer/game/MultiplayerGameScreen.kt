package com.project.imageneer.game

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.project.imageneer.R
import com.project.imageneer.data.GuessData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MultiplayerGameScreen(navController: NavHostController, roomId: String) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Database Reference
    val roomRef = remember(roomId) {
        FirebaseDatabase.getInstance().getReference("ruang_multiplayer").child(roomId)
    }
    val soalRef = remember {
        FirebaseDatabase.getInstance().getReference("solo")
    }
    // Tambah referensi ke riwayat_multiplayer
    val historyDatabaseRef = remember {
        FirebaseDatabase.getInstance().getReference("riwayat_multiplayer")
    }

    // State Game Utama
    var jawabanUser by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // State Identitas & Peran (Host atau Guest)
    var myRole by remember { mutableStateOf("") }
    var myName by remember { mutableStateOf("You") }
    var enemyName by remember { mutableStateOf("Enemy") }

    // State Realtime Score & Stage Sync
    var myScore by remember { mutableStateOf(0) }
    var enemyScore by remember { mutableStateOf(0) }
    var currentStage by remember { mutableStateOf(1) }
    var enemyStage by remember { mutableStateOf(1) }
    var totalStage by remember { mutableStateOf(0) }

    // Tambahan State: Mengunci agar pengiriman riwayat hanya tereksekusi satu kali (mencegah duplikasi data)
    var isHistorySaved by remember { mutableStateOf(false) }

    // List Soal Game
    var daftarSoal by remember { mutableStateOf<List<GuessData>>(emptyList()) }

    // State Firebase Storage untuk Gambar Berjalan
    var resolvedImageUrl by remember { mutableStateOf<String?>(null) }
    var isImageLoading by remember { mutableStateOf(false) }

    // 1. Inisialisasi Awal
    LaunchedEffect(Unit) {
        roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(context, "Room tidak valid", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                    return
                }

                val hostName = snapshot.child("pemain").child("host_player").child("nama").getValue(String::class.java) ?: ""
                val currentUserName = currentUser?.displayName ?: currentUser?.email ?: ""

                val hostClean = hostName.substringBefore("@").replaceFirstChar { it.uppercase() }
                val currentUserClean = currentUserName.substringBefore("@").replaceFirstChar { it.uppercase() }

                if (hostClean == currentUserClean) {
                    myRole = "host_player"
                    myName = hostClean
                    val guestName = snapshot.child("pemain").child("guest_player").child("nama").getValue(String::class.java) ?: "Enemy"
                    enemyName = guestName.substringBefore("@").replaceFirstChar { it.uppercase() }
                } else {
                    myRole = "guest_player"
                    val guestName = snapshot.child("pemain").child("guest_player").child("nama").getValue(String::class.java) ?: "Guest"
                    myName = guestName.substringBefore("@").replaceFirstChar { it.uppercase() }
                    enemyName = hostClean
                }

                soalRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(soalSnapshot: DataSnapshot) {
                        val listTemp = mutableListOf<GuessData>()
                        for (data in soalSnapshot.children) {
                            val soal = data.getValue(GuessData::class.java)
                            if (soal != null) listTemp.add(soal)
                        }

                        daftarSoal = listTemp.shuffled()
                        totalStage = listTemp.size

                        if (myRole.isNotEmpty()) {
                            roomRef.child("pemain").child(myRole).child("stage").setValue(1)
                        }
                        isLoading = false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isLoading = false
                        Toast.makeText(context, "Gagal memuat bank soal", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 2. Listener Realtime
    LaunchedEffect(myRole) {
        if (myRole.isEmpty()) return@LaunchedEffect

        val roomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    // Hanya pindah jika game belum selesai secara normal
                    if (currentStage <= totalStage || enemyStage <= totalStage) {
                        Toast.makeText(context, "Meninggalkan permainan", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                    return
                }

                val hostScore = snapshot.child("pemain").child("host_player").child("skor").getValue(Int::class.java) ?: 0
                val guestScore = snapshot.child("pemain").child("guest_player").child("skor").getValue(Int::class.java) ?: 0

                val hostStage = snapshot.child("pemain").child("host_player").child("stage").getValue(Int::class.java) ?: 1
                val guestStage = snapshot.child("pemain").child("guest_player").child("stage").getValue(Int::class.java) ?: 1

                val hostUid = snapshot.child("pemain").child("host_player").child("uid").getValue(String::class.java) ?: ""
                val guestUid = snapshot.child("pemain").child("guest_player").child("uid").getValue(String::class.java) ?: ""

                if (myRole == "host_player") {
                    myScore = hostScore
                    enemyScore = guestScore
                    currentStage = hostStage
                    enemyStage = guestStage
                } else {
                    myScore = guestScore
                    enemyScore = hostScore
                    currentStage = guestStage
                    enemyStage = hostStage
                }

                // Jika kedua belah pihak sudah melampaui totalStage dan riwayat belum tersimpan
                if (totalStage > 0 && hostStage > totalStage && guestStage > totalStage && !isHistorySaved) {
                    isHistorySaved = true // Kunci agar tidak melakukan loop write berkali-kali

                    // Format Tanggal Hari Ini
                    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                    val currentFormattedDate = sdf.format(Date())

                    // 💡 SOLUSI AMPUH: Tentukan Uid secara dinamis berdasarkan Role device saat ini!
                    // Karena room tidak menyimpan UID lawan, kita pasang UID kita ke tempat yang tepat,
                    // dan biarkan UID lawan diisi string dummy unik agar tidak bentrok atau kosong.
                    val finalHostUid: String
                    val finalGuestUid: String

                    if (myRole == "host_player") {
                        finalHostUid = currentUser?.uid ?: ""
                        // Karena kita tidak tahu UID guest, kita beri penanda khusus agar tidak kosong,
                        // nanti device Guest akan melakukan push datanya sendiri dengan UID miliknya yang valid.
                        finalGuestUid = "GUEST_OF_${currentUser?.uid}"
                    } else {
                        finalHostUid = "HOST_OF_${currentUser?.uid}"
                        finalGuestUid = currentUser?.uid ?: ""
                    }

                    // Buat objek Map untuk disimpan ke node "riwayat_multiplayer"
                    val historyData = mapOf(
                        "tanggal" to currentFormattedDate,
                        "hostUid" to finalHostUid,
                        "hostName" to (if(myRole == "host_player") myName else enemyName),
                        "hostScore" to hostScore,
                        "guestUid" to finalGuestUid,
                        "guestName" to (if(myRole == "guest_player") myName else enemyName),
                        "guestScore" to guestScore
                    )

                    // Perubahan Logika Penting:
                    // Izinkan KEDUA belah pihak (Host maupun Guest) melakukan push history ke database.
                    // Dengan begitu, device Host akan mencatat history dengan HostUid yang valid,
                    // dan device Guest akan mencatat history dengan GuestUid yang valid!
                    historyDatabaseRef.push().setValue(historyData)
                        .addOnSuccessListener {
                            android.util.Log.d("FirebaseHistory", "Berhasil mencatat riwayat untuk role: $myRole")
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Gagal mencatat riwayat ke database", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        roomRef.addValueEventListener(roomListener)
    }

    // 3. Mengonversi Jalur Path Firebase Storage
    LaunchedEffect(currentStage, daftarSoal) {
        if (daftarSoal.isNotEmpty() && currentStage <= daftarSoal.size) {
            val soalAktif = daftarSoal[currentStage - 1]

            if (!soalAktif.imageUrl.isNullOrBlank()) {
                isImageLoading = true
                resolvedImageUrl = null

                try {
                    val storageRef = FirebaseStorage.getInstance().getReference(soalAktif.imageUrl)
                    storageRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            resolvedImageUrl = uri.toString()
                            isImageLoading = false
                        }
                        .addOnFailureListener { error ->
                            isImageLoading = false
                            Toast.makeText(context, "Gagal memuat gambar: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                } catch (e: Exception) {
                    isImageLoading = false
                    e.printStackTrace()
                }
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
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.Center))
        }
        else if (totalStage > 0 && currentStage > totalStage && enemyStage <= totalStage) {
            // TAMPILAN WAITING STATUS
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = Color(0xFFFF6B35), strokeWidth = 4.dp)
                Text(
                    text = "Menunggu Lawan Selesai...",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "$enemyName saat ini berada di Stage $enemyStage/$totalStage",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }
        }
        else if (totalStage > 0 && currentStage > totalStage && enemyStage > totalStage) {
            // Tampilan Selesai Match
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Match Selesai!", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                val hasilPertandingan = when {
                    myScore > enemyScore -> "🎉 Anda Menang!"
                    myScore < enemyScore -> "❌ Anda Kalah!"
                    else -> "🤝 Hasil Seri!"
                }

                Text(hasilPertandingan, color = Color(0xFFFF6B35), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text("Skor Anda: $myScore vs Lawan: $enemyScore", color = Color.White, fontSize = 18.sp)

                Spacer(modifier = Modifier.height(24.dp))

                // BARU: Tombol Lihat History Pertandingan
                Button(
                    onClick = {
                        roomRef.removeValue() // Hapus room tanding lama
                        navController.navigate("multiplayer_history") {
                            // Clear backstack game agar tidak bisa kembali ke halaman game yang sudah selesai
                            popUpTo("home") { inclusive = false }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)), // Warna Hijau
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.width(220.dp).height(50.dp)
                ) {
                    Text("Lihat History", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol Kembali Ke Home
                Button(
                    onClick = {
                        // Menghapus data room tanding saat ini
                        roomRef.removeValue()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)), // Warna Oranye
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.width(220.dp).height(50.dp)
                ) {
                    Text("Kembali ke Home", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        } else {
            val soalAktif = daftarSoal.getOrNull(currentStage - 1)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 48.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TOP BAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            roomRef.removeValue()
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text("Exit", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF22C55E), shape = RoundedCornerShape(50.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Stage : $currentStage/$totalStage", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // MATCHUP SCORE BOARD
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(20.dp))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = myName, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF22C55E), shape = RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Text("Skor : $myScore", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "VS",
                        color = Color(0xFFFF6B35),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(20.dp))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = enemyName, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF22C55E), shape = RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Text("Skor : $enemyScore", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // MAIN CARD PLAYGROUND
                if (soalAktif != null) {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Container Gambar Kuis
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color(0xFFFCE7F3), shape = RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isImageLoading || resolvedImageUrl == null) {
                                    CircularProgressIndicator(color = Color(0xFF7C3AED), strokeWidth = 3.dp)
                                } else {
                                    SubcomposeAsyncImage(
                                        model = resolvedImageUrl,
                                        contentDescription = "Gambar Kuis",
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        contentScale = ContentScale.Fit,
                                        loading = {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                CircularProgressIndicator(color = Color(0xFF7C3AED), strokeWidth = 3.dp)
                                            }
                                        },
                                        error = {
                                            Image(
                                                painter = painterResource(id = R.drawable.logo),
                                                contentDescription = "Error Load",
                                                modifier = Modifier.size(120.dp)
                                            )
                                        }
                                    )
                                }
                            }

                            // Input Box Jawaban
                            OutlinedTextField(
                                value = jawabanUser,
                                onValueChange = { jawabanUser = it },
                                placeholder = {
                                    Text("Masukan Jawabanmu", color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                shape = RoundedCornerShape(28.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF7C3AED),
                                    unfocusedBorderColor = Color(0xFFC084FC),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 16.sp)
                            )

                            // Tombol Jawab
                            Button(
                                onClick = {
                                    val jawabanBersih = jawabanUser.trim().lowercase()
                                    val kunciBersih = soalAktif.kunciJawaban.trim().lowercase()

                                    if (jawabanBersih == kunciBersih) {
                                        roomRef.child("pemain").child(myRole).child("skor").setValue(myScore + 10)
                                        Toast.makeText(context, "🎉 Benar! +10", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "❌ Salah!", Toast.LENGTH_SHORT).show()
                                    }

                                    jawabanUser = ""
                                    roomRef.child("pemain").child(myRole).child("stage").setValue(currentStage + 1)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                                shape = RoundedCornerShape(28.dp),
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                enabled = jawabanUser.isNotBlank() && !isImageLoading
                            ) {
                                Text("Jawab", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}