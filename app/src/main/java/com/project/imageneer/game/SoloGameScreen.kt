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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.imageneer.R
import com.project.imageneer.data.SoalSolo

@Composable
fun SoloGameScreen(navController: NavHostController) {
    val context = LocalContext.current

    // State Game
    var jawabanUser by remember { mutableStateOf("") }
    var skor by remember { mutableIntStateOf(0) }

    // State Firebase Data
    var daftarSoal by remember { mutableStateOf<List<SoalSolo>>(emptyList()) }
    var indeksSoalSekarang by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // Ambil data dari Firebase
    LaunchedEffect(Unit) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("permainan_solo")
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listTemp = mutableListOf<SoalSolo>()
                for (data in snapshot.children) {
                    val soal = data.getValue(SoalSolo::class.java)
                    if (soal != null) {
                        listTemp.add(soal)
                    }
                }
                daftarSoal = listTemp.shuffled()
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                Toast.makeText(context, "Gagal memuat: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7C3AED))
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (daftarSoal.isEmpty()) {
            Text(
                text = "Tidak ada data soal tersedia.",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (indeksSoalSekarang >= daftarSoal.size) {
            // Tampilan Selesai
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Permainan Selesai!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Total Skormu: $skor", color = Color(0xFF22C55E), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        skor = 0
                        indeksSoalSekarang = 0
                        daftarSoal = daftarSoal.shuffled()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Main Lagi", color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)
                }
            }
        } else {
            val soalAktif = daftarSoal[indeksSoalSekarang]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 50.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TOP BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text("Exit", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF22C55E), shape = RoundedCornerShape(50.dp))
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text("Skor : $skor", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // MAIN CARD PLAYGROUND (Sesuai Gambar Mockup Anda)
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Container Gambar menggunakan SubcomposeAsyncImage agar proses render aman
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color(0xFFFCE7F3), shape = RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            SubcomposeAsyncImage(
                                model = soalAktif.imageUrl,
                                contentDescription = "Gambar Kuis",
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                contentScale = ContentScale.Fit,
                                loading = {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = Color(0xFF7C3AED), strokeWidth = 3.dp)
                                    }
                                },
                                error = {
                                    // Menampilkan Gambar Cadangan jika URL internet bermasalah atau gagal load
                                    Image(
                                        painter = painterResource(id = R.drawable.logo),
                                        contentDescription = "Gagal memuat gambar",
                                        modifier = Modifier.size(120.dp)
                                    )
                                }
                            )
                        }

                        // Input Text Field
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
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(28.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF7C3AED),
                                unfocusedBorderColor = Color(0xFFC084FC)
                            ),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 16.sp)
                        )

                        // Tombol Jawab
                        Button(
                            onClick = {
                                val jawabanBersih = jawabanUser.trim().lowercase()
                                val kunciBersih = soalAktif.kunciJawaban.trim().lowercase()

                                if (jawabanBersih == kunciBersih) {
                                    skor += 10
                                    Toast.makeText(context, "🎉 Jawaban Benar!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "❌ Salah! Jawaban: ${soalAktif.kunciJawaban}", Toast.LENGTH_SHORT).show()
                                }

                                jawabanUser = ""
                                indeksSoalSekarang++
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                            shape = RoundedCornerShape(28.dp),
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            enabled = jawabanUser.isNotBlank()
                        ) {
                            Text("Jawab", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}