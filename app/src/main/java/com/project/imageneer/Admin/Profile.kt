package com.project.imageneer.Admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.project.imageneer.ui.theme.ImageneerTheme

@Composable
fun ProfileScreen(
    navController: NavHostController
) {

    val currentUser = FirebaseAuth
        .getInstance()
        .currentUser

    val email = currentUser?.email ?: "Username"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        Column {

            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFF7C3AED),
                        shape = RoundedCornerShape(
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .padding(16.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Akun Saya",
                    color = Color.White,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ICON PROFILE
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {

                // USERNAME
                Text(
                    text = "Username"
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    readOnly = true,

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(20.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7C3AED),
                        unfocusedBorderColor = Color(0xFF7C3AED)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Ketentuan Admin",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)

                ) {

                    Text(
                        text =
                            "• Menambahkan gambar tebakan\n\n" +
                                    "• Menambahkan kunci jawaban\n\n" +
                                    "• Menghapus gambar yang sudah diupload",

                        color = Color.DarkGray,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))



            // BUTTON LOGOUT
            Button(

                onClick = {
                    FirebaseAuth
                        .getInstance()
                        .signOut()

                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 24.dp
                    )
                    .height(55.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                ),

                shape = RoundedCornerShape(50.dp)

            ) {

                Text(
                    text = "Keluar",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfilePreview() {
    ImageneerTheme {
        ProfileScreen(
            navController = rememberNavController()
        )
    }
}