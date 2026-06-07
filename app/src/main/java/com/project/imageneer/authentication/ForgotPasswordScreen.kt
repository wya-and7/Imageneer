package com.project.imageneer.authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.project.imageneer.ui.theme.ButtonPurple
import com.project.imageneer.ui.theme.MainPurple
import com.project.imageneer.R

@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var username by remember { mutableStateOf("") } // Diisi alamat email asli user
    var isSendingEmail by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainPurple),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp)
            )

            // App Name
            Text(
                text = "Imageneer",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Card
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "RESET\nPASSWORD",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Username field (Email)
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("username", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MainPurple.copy(alpha = 0.5f),
                            focusedBorderColor = MainPurple,
                            cursorColor = MainPurple
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tombol Kirim Link Reset
                    Button(
                        onClick = {
                            val targetEmail = username.trim()
                            if (targetEmail.isBlank()) {
                                Toast.makeText(context, "Masukkan email terlebih dahulu", Toast.LENGTH_SHORT).show()
                            } else {
                                isSendingEmail = true
                                // Mengirimkan link reset password resmi langsung ke Gmail user
                                auth.sendPasswordResetEmail(targetEmail)
                                    .addOnCompleteListener { task ->
                                        isSendingEmail = false
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Link reset password telah dikirim ke Gmail Anda", Toast.LENGTH_LONG).show()
                                            // Langsung balikkan user ke halaman login setelah berhasil kirim
                                            navController.navigate("login") {
                                                popUpTo("forgot_password") { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(context, "Gagal: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        },
                        enabled = !isSendingEmail,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple)
                    ) {
                        if (isSendingEmail) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "Kirim Link Reset",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tombol Kembali ke Login alternatif
                    Text(
                        text = "Kembali ke Login",
                        color = MainPurple,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable {
                                navController.navigate("login") {
                                    popUpTo("forgot_password") { inclusive = true }
                                }
                            }
                    )
                }
            }
        }
    }
}