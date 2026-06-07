package com.project.imageneer.game

import android.util.Log
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.imageneer.R
import com.project.imageneer.data.SoalSoloEntity
import com.project.imageneer.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SoloGameScreen(navController: NavHostController) {
    val context = LocalContext.current

    // State Game
    var jawabanUser by remember { mutableStateOf("") }
    var skor by remember { mutableIntStateOf(0) }

    // State Room Database Data
    var daftarSoal by remember { mutableStateOf<List<SoalSoloEntity>>(emptyList()) }
    var indeksSoalSekarang by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // Mengambil DAO Room
    val db = remember { AppDatabase.getDatabase(context) }
    val soalDao = remember { db.soalSoloLocalDao() }

    // Ambil data dari Room Database secara asinkron
    LaunchedEffect(Unit) {
        try {
            val listTemp = withContext(Dispatchers.IO) {
                soalDao.getAllSoal()
            }
            daftarSoal = listTemp.shuffled()
            isLoading = false

            Log.d("SoloGameScreen", "Jumlah soal yang berhasil dimuat: ${listTemp.size}")
        } catch (e: Exception) {
            isLoading = false
            Toast.makeText(context, "Gagal memuat database lokal: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            Log.e("SoloGameScreen", "Error query Room: ${e.message}", e)
        }
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
                text = "Tidak ada data soal lokal tersedia.",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (indeksSoalSekarang >= daftarSoal.size) {
            // Tampilan Selesai
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Permainan Selesai!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Total Skormu: $skor", color = Color(0xFF22C55E), fontSize = 22.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        skor = 0
                        indeksSoalSekarang = 0
                        jawabanUser = ""
                        daftarSoal = daftarSoal.shuffled()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.width(220.dp).height(50.dp)
                ) {
                    Text("Main Lagi", color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Button(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.width(220.dp).height(50.dp)
                ) {
                    Text("Kembali ke Dashboard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        } else {
            val soalAktif = daftarSoal[indeksSoalSekarang]

            // Perbaikan Logika: Membersihkan string nama file dari spasi atau extension (.png/.jpg) jika terikut di DB
            val namaGambarBersih = remember(soalAktif.imageName) {
                soalAktif.imageName.trim()
                    .substringBefore(".") // Menghilangkan ".png" atau ".jpg" jika ada di DB
            }

            // Mencari ID Resource Gambar secara dinamis
            val imageResId = remember(namaGambarBersih) {
                val resId = context.resources.getIdentifier(
                    namaGambarBersih,
                    "drawable",
                    context.packageName
                )
                // Cetak log untuk debugging di Logcat Android Studio
                Log.d("SoloGameScreen", "Mencari drawable bernama: '$namaGambarBersih' -> Hasil Res ID: $resId")
                resId
            }

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
                            .background(Color(0xFFF97316), shape = RoundedCornerShape(50.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Stage : ${indeksSoalSekarang + 1}/${daftarSoal.size}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF22C55E), shape = RoundedCornerShape(50.dp))
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text("Skor : $skor", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // MAIN CARD GAME
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
                        // Container Gambar Lokal
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color(0xFFFCE7F3), shape = RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    id = if (imageResId != 0) imageResId else R.drawable.logo
                                ),
                                contentDescription = "Gambar Kuis Lokal",
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                contentScale = ContentScale.Fit
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
                            textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 16.sp)
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