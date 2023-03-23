package com.example.recallify.view.ui.feature.application.tbi_applications.accounts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings.MainSettingsTBI
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.GuardianMainSettings
import com.example.recallify.view.ui.feature.security.signin.LoginActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase



class AccountsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)

        val accountsCompose: ComposeView = findViewById(R.id.activity_accounts_screen)
        accountsCompose.setContent {
            RecallifyTheme {
                AccountsScreen()
            }
        }
    }

    @Composable
    fun AccountsScreen() {

        val auth: FirebaseAuth = Firebase.auth
        val database = Firebase.database.reference.child("users")
        val current = auth.currentUser?.uid!!
        val childValue = remember { mutableStateOf("") }

            Scaffold(
                topBar = {AccountSettingsTopBar()} ,
            bottomBar = { BottomBarFiller() },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp)
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "User icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(150.dp)
                    )

//                    Text(style = TextStyle(fontSize = 24.sp), text = "Account Settings")

                }
                Column( modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(top = 50.dp)
                    .padding(bottom = 8.dp),
                    Arrangement.Center,
                    Alignment.CenterHorizontally) {

                    var firstName: String by remember { mutableStateOf("") }
                    var lastName: String by remember { mutableStateOf("") }
                    var email: String by remember { mutableStateOf("") }
                    var password: String by remember { mutableStateOf("") }
                    var PIN : String by remember { mutableStateOf("") }
//                    var password by rememberSaveable { mutableStateOf("") }


                    LaunchedEffect(Unit) {

                        database.child(current).child("profile").child("firstname")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    firstName = dataSnapshot.getValue(String::class.java) ?: ""
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        database.child(current).child("profile").child("lastname")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    lastName = dataSnapshot.getValue(String::class.java) ?:""
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        database.child(current).child("profile").child("email")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    email = dataSnapshot.getValue(String::class.java) ?: ""
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        database.child(current).child("profile").child("password")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    password = dataSnapshot.getValue(String::class.java) ?: ""
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        database.child(current).child("profile").child("pin")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    PIN = dataSnapshot.getValue(String::class.java) ?: ""
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                    }
                    Row(
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        Text(text = "First Name:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = firstName)
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {

                        Text(text = "Last Name:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = lastName)
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {
                        Text(text = "Email:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = email)

                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {

                        Text(text = "PIN:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = PIN)

                    }
                    LogoutButton(activity = this@AccountsActivity)
                }
            }
        }
    }

    @Composable
    fun LogoutButton(activity: AccountsActivity) {
        Button(modifier = Modifier.padding(top = 20.dp), onClick = {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountsActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }) {
            Text(text = "Log Out")
        }
    }

    @Composable
    private fun AccountSettingsTopBar() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp)
                .clip(shape = RoundedCornerShape(26.dp))
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(
                    onClick = {
                        val intent = Intent(
                            applicationContext,
                            MainSettingsTBI::class.java
                        )
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        finish()
                    },
                    Modifier.weight(1f)

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_arrow_back_24),
                        contentDescription = "Go back to Main Settings",
                        modifier = Modifier
                            .size(42.dp)
                            .border(
                                border = BorderStroke(2.dp, SolidColor(Color.Black)),
                                shape = RoundedCornerShape(20.dp)
                            )
                    )
                }
                Text(
                    text = "Account Settings",
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }
}



