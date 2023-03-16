package com.example.recallify.view.ui.feature.security.forgotpassword

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.ui.feature.security.signin.LoginActivity
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val forgotPasswordCompose: ComposeView = findViewById(R.id.activity_forgot_password_screen)
        forgotPasswordCompose.setContent {
            RecallifyTheme {
                val context = LocalContext.current
                ForgotPasswordScreen(
                    processEmailLink = {
                        Toast.makeText(context, "Email link sent successfully", Toast.LENGTH_SHORT).show()
                    },
                    onNavToLogin = {
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    @Composable
    fun ForgotPasswordScreen(
        processEmailLink: () -> Unit,
        onNavToLogin: () -> Unit,
    ) {

        var email by remember {
            mutableStateOf("")
        }

        Scaffold(backgroundColor = MaterialTheme.colors.surface) { paddingValue ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    IconButton(
                        onClick = { onNavToLogin() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_arrow_back_24),
                            contentDescription = "go back to login",
                            modifier = Modifier
                                .size(42.dp)
                                .border(
                                    border = BorderStroke(2.dp, SolidColor(Color.Black)),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.forgotpass),
                    contentDescription = "Login pic!",
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .size(200.dp),
                )

                Card(
                    Modifier.weight(2f),
                    backgroundColor = MaterialTheme.colors.background,
                    elevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Find your account",
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Sorry to see you forgot your password.\nPlease enter your email and a " +
                                    "link will be sent to you email address on how to get a new password.",
                            color = Color.Gray,
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.subtitle2,
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                singleLine = true,
                                label = { Text("Email address") },
                                placeholder = { Text("example@gmail.com") },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_email_24),
                                        contentDescription = "email",
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.Gray
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { email = "" }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.backspace_48),
                                            contentDescription = "email",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .padding(top = 30.dp)
                                    .fillMaxWidth()
                            )
                            Button(
                                onClick = { processEmailLink() },
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Find account",
                                    style = MaterialTheme.typography.button,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}