package com.project.imageneer.authentication

import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.project.imageneer.ui.theme.ButtonPurple
import com.project.imageneer.ui.theme.MainPurple
import com.google.firebase.firestore.firestore // TAMBAHKAN IMPORT INI
import com.project.imageneer.R

class AdminLoginActivity : ComponentActivity()

@Composable
fun AdminLoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                        text = "ADMIN LOGIN",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Username", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MainPurple.copy(alpha = 0.5f),
                            focusedBorderColor = MainPurple,
                            cursorColor = MainPurple
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MainPurple.copy(alpha = 0.5f),
                            focusedBorderColor = MainPurple,
                            cursorColor = MainPurple
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            } else if (password.length < 6) {
                                Toast.makeText(context, "password minimal 6 digit", Toast.LENGTH_SHORT).show()
                            } else {
                                Firebase.auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful){
                                            val uid = Firebase.auth.currentUser?.uid
                                            if (uid != null) {
                                                // Cek role di Firestore
                                                Firebase.firestore.collection("users").document(uid).get()
                                                    .addOnSuccessListener { document ->
                                                        if (document != null && document.exists()) {
                                                            val role = document.getString("role")

                                                            if (role == "admin") {
                                                                // JIKA ADMIN: Berhasil masuk
                                                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                                                navController.navigate("admin_home"){
                                                                    popUpTo("admin_login") { inclusive = true }
                                                                }
                                                            } else {
                                                                // JIKA USER: Tolak akses dan paksa Sign Out
                                                                Firebase.auth.signOut()
                                                                Toast.makeText(context, "Akses ditolak. Anda bukan Admin!", Toast.LENGTH_LONG).show()
                                                            }
                                                        } else {
                                                            Firebase.auth.signOut()
                                                            Toast.makeText(context, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Firebase.auth.signOut()
                                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                        } else {
                                            Toast.makeText(context, "password salah", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple)
                    )  {
                        Text(
                            text = "Sign In",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("admin_login") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Login as User",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}