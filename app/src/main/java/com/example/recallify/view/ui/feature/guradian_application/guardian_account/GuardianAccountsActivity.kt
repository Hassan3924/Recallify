package com.example.recallify.view.ui.feature.guradian_application.guardian_account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.recallify.R
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.GuardianMainSettings
//import com.example.recallify.view.ui.feature.BaseActivity
import com.example.recallify.view.ui.feature.security.signin.LoginActivity
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GuardianAccountsActivity : AppCompatActivity() {

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
        var showPINField by remember { mutableStateOf(true)}
        var tbiEmailConfirmed : String = ""
        

        Scaffold(
            topBar = {AccountSettingsTopBar()},
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
                        .padding(top = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "User icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(150.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 10.dp)
                        .padding(bottom = 8.dp),
                    Arrangement.Center,
                    Alignment.CenterHorizontally
                ) {

                    var firstName: String by remember { mutableStateOf("") }
                    var lastName: String by remember { mutableStateOf("") }
                    var email: String by remember { mutableStateOf("") }
                    var password: String by remember { mutableStateOf("") }
                    var tbiEmail: String by remember { mutableStateOf("") }
                    var PIN: String by remember { mutableStateOf("")}


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
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        Text(
                            text = "First Name:",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.SemiBold
                            ))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = firstName)
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {

                        Text(text = "Last Name:",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.SemiBold
                            ))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = lastName)
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {
                        Text(text = "Email:",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.SemiBold
                            ))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = email)

                    }

                    database.child(current).child("profile").child("TBI Email")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                tbiEmailConfirmed = dataSnapshot.getValue(String::class.java) ?: ""
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {
                        Text(text = "TBI Email:",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.SemiBold
                            ))
                        Spacer(modifier = Modifier.width(40.dp))

                        if (tbiEmailConfirmed.isNullOrEmpty()) {
                            Text(text = "No patient's email connected to this account")
                        }
                        else {
                            Text(text= tbiEmailConfirmed)
                        }


                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {
                        Text(text = "TBI Email:",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.SemiBold
                            ))
                        Spacer(modifier = Modifier.weight(1f))
                        if (showEmailField) {
                            TextField(
                                value = tbiEmail,
                                onValueChange = { newValue -> tbiEmail = newValue },
                                label = { Text("Enter new email") },
                                modifier = Modifier.width(200.dp),
                                singleLine = true
                            )
                        } else {
                            Text(
                                text = "",
                                color = Color.Gray,
                                modifier = Modifier.clickable { showEmailField = true }
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {
                        Text(text = "PIN:",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.SemiBold
                            ))
                        Spacer(modifier = Modifier.weight(1f))
                        if (showPINField) {
                            TextField(
                                value = PIN.toString(),
                                onValueChange = { newValue -> PIN = newValue },
                                label = { Text("Enter PIN") },
                                modifier = Modifier.width(200.dp),
                                singleLine = true
                            )
                        } else {
                            Text(
                                text = "",
                                color = Color.Gray,
                                modifier = Modifier.clickable { showEmailField = true }
                            )
                        }
                    }


                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 30.dp)
                    ) {
                        SaveButton(tbiEmail = tbiEmail, PIN = PIN ,database, current)
                        Spacer(modifier = Modifier.width(50.dp))
                        LogoutButton(activity = this@GuardianAccountsActivity)
                    }
                }
            }
        }
    }

    @Composable
    fun SaveButton(tbiEmail: String, PIN: String, database: DatabaseReference, current: String){

        // State variable to track whether the button has been clicked
        var isButtonClicked by remember { mutableStateOf(false) }

        Button(
            onClick = {
                // Encode email address to use as key in Firebase database
                val encodedEmail = tbiEmail.replace(".", "_")

//                database.child(current).child("profile").child("PIN").setValue(PIN)

                // Update UID value in testing_connection node
                val userIdCheck = database.child("connections").child(encodedEmail).child("userID")
                val pinCheck = database.child("connections").child(encodedEmail).child("PIN")
                val userEmailCheck = database.child("connections").child(encodedEmail).key

                userIdCheck.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.exists()) {
                            var uid = dataSnapshot.getValue(String::class.java)
                            Log.i("This is the uid", uid!!)

                            pinCheck.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot2: DataSnapshot) {

                                    if (dataSnapshot2.exists()) {
                                        val pin = dataSnapshot2.getValue(String::class.java)
                                        Log.i("This is the pin", pin!!)

                                        if (userEmailCheck.equals(encodedEmail) && (pin.equals(PIN))) {
//                                             If both uid and pin exist, make the connection
                                            database.child("GuardiansLinkTable").child(current)
                                                .child("TBI_ID").setValue(uid)

                                            // Set the isButtonClicked state variable to true
                                            isButtonClicked = true

                                            // Set TBI Email value in database
                                            database.child(current).child("profile").child("TBI Email")
                                                .setValue(tbiEmail)

                                            Toast.makeText(this@GuardianAccountsActivity, "Congratulations! Accounts have been connected", Toast.LENGTH_SHORT).show()


                                        } else {
                                            Toast.makeText(this@GuardianAccountsActivity, "Wrong Email or PIN", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("onCancelled", "Unable to read value: ${error.toException()}")
                                }
                            }
                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            },
            // Disable the button if it has already been clicked
            enabled = !isButtonClicked
        ) {
            Text("Save")
        }
    }


    @Composable
    fun LogoutButton(activity: GuardianAccountsActivity) {

        Button(
            onClick = {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@GuardianAccountsActivity, LoginActivity::class.java)
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
                            GuardianMainSettings::class.java
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
                            .size(28.dp)
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

