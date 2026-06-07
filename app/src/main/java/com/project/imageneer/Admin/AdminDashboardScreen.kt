package com.project.imageneer.Admin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.project.imageneer.R
import com.project.imageneer.data.GuessData
import com.project.imageneer.dashboard.DashboardScreen
import com.project.imageneer.data.SoalSolo
import com.project.imageneer.ui.theme.ImageneerTheme

@Composable
fun AdminDashboardScreen(navController: NavHostController) {

    val viewModel: AdminViewModel = viewModel()
    val email = FirebaseAuth
        .getInstance()
        .currentUser
        ?.email ?: "User"

    val username = email
        .substringBefore("@")
        .replaceFirstChar { it.uppercase() }

    var daftarTebakan by remember {
        mutableStateOf<List<SoalSolo>>(emptyList())
    }

    LaunchedEffect(Unit) {
        viewModel.getSoal {
            daftarTebakan = it
        }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(
                    Color(0xFF7C3AED),
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Bagian Admin",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        navController.navigate("profile")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Admin",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // WELCOME TEXT
        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {

            Text(
                text = "Selamat Datang Admin $username",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kamu bisa menambah Gambar Tebakan dan Kunci Jawabannya",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BUTTON TAMBAH
        Button(
            onClick = {
                navController.navigate("add_image")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7C3AED)
            )
        ) {

            Text(
                text = "Tambah",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TITLE
        Text(
            text = "Baru Ditambahkan",
            modifier = Modifier.padding(horizontal = 20.dp),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // LIST DATA
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            items(daftarTebakan) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF7C3AED),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // IMAGE
                        StorageImage(
                            storagePath = item.imageUrl
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // TEXT
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Jawaban",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.kunciJawaban,
                                fontSize = 16.sp,
                                color = Color.DarkGray
                            )
                        }

                        // DELETE BUTTON
                        IconButton(
                            onClick = {

                                viewModel.deleteSoal(
                                    soal = item,

                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "Data berhasil dihapus",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },

                                    onFailure = {
                                        Toast.makeText(
                                            context,
                                            it.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFFF5722),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StorageImage(
    storagePath: String
) {

    var downloadUrl by remember(storagePath) {
        mutableStateOf("")
    }

    LaunchedEffect(storagePath) {
        FirebaseStorage.getInstance()
            .reference
            .child(storagePath)
            .downloadUrl
            .addOnSuccessListener { uri ->
                downloadUrl = uri.toString()
            }
    }

    AsyncImage(
        model = downloadUrl,
        contentDescription = null,
        modifier = Modifier
            .size(70.dp)
            .clip(RoundedCornerShape(10.dp)),
        contentScale = ContentScale.Crop
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    ImageneerTheme {
        AdminDashboardScreen(
            navController = rememberNavController()
        )
    }
}