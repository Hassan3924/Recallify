package com.example.recallify.view.ui.feature.security.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.dashboard.DashboardActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiandashboard.GuardiansDashboardActivity
import com.example.recallify.view.ui.feature.security.forgotpassword.ForgotPasswordActivity
import com.example.recallify.view.ui.feature.security.signup.RegisterActivity
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val binding: ComposeView = findViewById(R.id.activity_login_screen)
        binding.setContent {
            RecallifyTheme {
                val context = LocalContext.current
                SignInScreen(
                    onNavToTBIHome = {

                        val intent = Intent(context, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()

                    },
                    onNavToGuardianHome = {

                      val intent = Intent(context, GuardiansDashboardActivity::class.java)
                      startActivity(intent)
                      finish()

                    },
                    onNavToForgotPassword = {
                        val intent = Intent(context, ForgotPasswordActivity::class.java)
                        startActivity(intent)
                    },
                    onNavToSignUp = {
                        val intent = Intent(context, RegisterActivity::class.java)
                        startActivity(intent)
                    },
                )
            }
        }
    }

    @Composable
    fun SignInScreen(
        onNavToForgotPassword: () -> Unit,
        onNavToTBIHome: () -> Unit,
        onNavToGuardianHome: () -> Unit,
        onNavToSignUp: () -> Unit,
    ) {
        val auth: FirebaseAuth = Firebase.auth
        val password = rememberSaveable { mutableStateOf("") }
        val email = rememberSaveable { mutableStateOf("") }

        val showPassword = remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current

        val loading = remember { mutableStateOf(false) }
        val context: Context = LocalContext.current

        Scaffold(
            scaffoldState = rememberScaffoldState(),
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValue ->
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
                    Text(
                        text = "Login In, right here.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.h4
                    )
                    Text(
                        text = "These will just take some few minutes",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.h6.copy(
                            color = Color.Gray
                        )
                    )
                }
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
                            text = "We miss you!",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.subtitle2,
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            OutlinedTextField(
                                value = email.value,
                                onValueChange = { email.value = it },
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
                                    IconButton(onClick = { email.value = "" }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.backspace_48),
                                            contentDescription = "email",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = password.value,
                                onValueChange = { password.value = it },
                                singleLine = true,
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_password_24),
                                        contentDescription = "password",
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.Gray
                                    )
                                },
                                trailingIcon = {
                                    if (showPassword.value) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.visibility_48),
                                            contentDescription = "password",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Gray
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.visibility_off_48),
                                            contentDescription = "password",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                    }
                                )
                            )
                            Text(
                                text = "Forgot password?",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp, bottom = 10.dp)
                                    .clickable { onNavToForgotPassword() },
                                textAlign = TextAlign.Start
                            )
                            Button(
                                onClick = {
                                    val auth = Firebase.auth
                                    val database = Firebase.database.reference
                                    val userRole = database.child("users").child(auth.uid.toString()).child("profile").child("role")
                                    loading.value = true
                                    auth.signInWithEmailAndPassword(email.value, password.value)
                                        .addOnCompleteListener { task ->
                                            loading.value = false
                                            if (task.isSuccessful) {
                                                userRole.addListenerForSingleValueEvent(object :
                                                    ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        val role = snapshot.getValue(String::class.java)
                                                        if (role == "TBI") {
                                                            // Navigate to a specific destination for users with role "TBI"
                                                            onNavToTBIHome()

                                                        } else {
                                                            // Navigate to a different destination for users with other roles
                                                            onNavToGuardianHome()
                                                        }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        // Handle error here
                                                    }
                                                })
                                            } else {
                                                Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT).show()
                                                Log.w("LoginActivity", "SignInFailure : (Query related) : ->", task.exception)
                                            }
                                        }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Login",
                                    style = MaterialTheme.typography.button,
                                    fontSize = 18.sp
                                )
                            }

                            Text(
                                text = "Don't have an Account?  Sign up.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp, bottom = 10.dp)
                                    .clickable { onNavToSignUp() },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
        override fun onStart() {
        super.onStart()

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val database = Firebase.database.reference
        val userRole = database.child("users").child(auth.uid.toString()).child("profile").child("role")
        userRole.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val role = snapshot.getValue(String::class.java)
                if (role == "TBI") {
                    // Navigate to a specific destination for users with role "TBI"
                    if (user != null){
                        Toast.makeText(applicationContext,"Welcome to Recallify",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity,DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    } else {
                    // Navigate to a different destination for users with other roles
                    if (user != null) {
                        if (role == "Guardian") {
                            Toast.makeText(
                                applicationContext,
                                "Welcome to Recallify",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(this@LoginActivity, GuardiansDashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error here
            }
        })



    }
}


