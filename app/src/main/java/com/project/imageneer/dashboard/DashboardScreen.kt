package com.project.imageneer.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.size
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.project.imageneer.R
import com.project.imageneer.ui.theme.ImageneerTheme

@Composable
fun DashboardScreen(navController: NavHostController) {

    val email = FirebaseAuth
        .getInstance()
        .currentUser
        ?.email ?: "User"

    val username = email
        .substringBefore("@")
        .replaceFirstChar { it.uppercase() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7C3AED))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(64.dp))
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Character",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = "Imageneer",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Bubble Chat
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.animasi),
                    contentDescription = "Character",
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.width(0.dp))

                Box(
                    modifier = Modifier
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {

                    Text(
                        text = "Halo $username,\nSelamat Bermain!!",
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(70.dp))

            // Button Solo
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E)
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .width(250.dp)
                    .height(65.dp)
            ) {

                Text(
                    text = "Solo",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Button Multiplayer
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF97316)
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .width(250.dp)
                    .height(65.dp)
            ) {

                Text(
                    text = "Multiplayer",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    ImageneerTheme {
        DashboardScreen(
            navController = rememberNavController()
        )
    }
}