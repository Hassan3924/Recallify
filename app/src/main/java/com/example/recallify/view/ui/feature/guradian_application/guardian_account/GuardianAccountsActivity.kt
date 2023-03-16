package com.example.recallify.view.ui.feature.guradian_application.guardian_account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
//import com.example.recallify.view.ui.feature.BaseActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiandailydiary.GuardianDailyDairyActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiandashboard.GuardiansDashboardActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiansidequest.GuardianSideQuestActivity
import com.example.recallify.view.ui.feature.guradian_application.guardianthinkfast.GuardianThinkFastActivity
import com.example.recallify.view.ui.feature.security.signin.LoginActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.example.speech_to_text_jetpack.navigation.AudioScreens
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GuardianAccountsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_accounts

        bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, GuardiansDashboardActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_daily_diary -> {
                    startActivity(Intent(applicationContext, GuardianDailyDairyActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_side_quest -> {
                    startActivity(Intent(applicationContext, GuardianSideQuestActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_think_fast -> {
                    startActivity(Intent(applicationContext, GuardianThinkFastActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_accounts -> true
                else -> false
            }
        }

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
        val test = Firebase.database.reference
        val database = Firebase.database.reference.child("users")
        val current = auth.currentUser?.uid!!
        val childValue = remember { mutableStateOf("") }
        var tbiEmail = remember { mutableStateOf(TextFieldValue()) }
        var showEmailField by remember { mutableStateOf(true) }
        var isDataChanged by remember { mutableStateOf(false) }
        var uid by remember { mutableStateOf("") }
        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        

        Scaffold(
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

                    Text(style = TextStyle(fontSize = 24.sp), text = "Account Settings")

                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(top = 50.dp)
                        .padding(bottom = 8.dp),
                    Arrangement.Center,
                    Alignment.CenterHorizontally
                ) {

                    var firstName: String by remember { mutableStateOf("") }
                    var lastName: String by remember { mutableStateOf("") }
                    var email: String by remember { mutableStateOf("") }
                    var password: String by remember { mutableStateOf("") }
                    var tbiEmail: String by remember { mutableStateOf("") }


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
                                    lastName = dataSnapshot.getValue(String::class.java) ?: ""
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

                        Text(text = "Password:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = buildString {
                            for (i in 1..password.length) {
                                append("*")
                            }
                        })
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {
                        Text(text = "TBI Email:")
                        Spacer(modifier = Modifier.weight(1f))
                        if (showEmailField) {
                            TextField(
                                value = tbiEmail,
                                onValueChange = { newValue -> tbiEmail = newValue },
                                label = { Text("Enter new email") },
                                modifier = Modifier.width(200.dp)
                            )
                        } else {
                            Text(
                                text = "",
                                color = Color.Gray,
                                modifier = Modifier.clickable { showEmailField = true }
                            )
                        }
                    }

                    Button(
                        modifier = Modifier.padding(top = 10.dp),
                        onClick = {
                            // Encode email address to use as key in Firebase database
                            val encodedEmail = tbiEmail.replace(".", "_")

                            // Set TBI Email value in database
                            database.child(current).child("profile").child("TBI Email")
                                .setValue(tbiEmail)

                            // Update UID value in testing_connection node
                            val userRef =
                                database.child("connections").child(encodedEmail).child("userID")

                            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                    if (dataSnapshot.exists()) {
                                        var uid = dataSnapshot.getValue(String::class.java)
                                        Log.i("This is the uid", uid!!)
                                        // Create a new node called "testing_connection" and set its UID value
                                        database.child("GuardiansLinkTable").child(current)
                                            .child("TBIID").setValue(uid)

                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.w(
                                        "GuardianAccountsActivity",
                                        "LoadProfile:onCancelled",
                                        databaseError.toException()
                                    )
                                }
                            })

                            // Reset states
//                            showEmailField = false
//                            isDataChanged = true
                        },
//                        enabled = showEmailField && tbiEmail.isNotBlank()
                    ) {
                        Text("Save")
                    }
                    LogoutButton(activity = this@GuardianAccountsActivity)
                }
            }
          }
        }

    @Composable
    fun LogoutButton(activity: GuardianAccountsActivity) {
        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }) {
            Text(text = "Log Out")
        }
    }
}

