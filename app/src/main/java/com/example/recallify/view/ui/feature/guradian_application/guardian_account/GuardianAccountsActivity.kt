package com.example.recallify.view.ui.feature.guradian_application.guardian_account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.recallify.R
import com.example.recallify.view.common.components.RecallifyCustomHeader
import com.example.recallify.view.common.resources.GuardiansAccountTopAppBar
import com.example.recallify.view.ui.feature.guradian_application.guardiandashboard.GuardiansDashboardActivity
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
import java.util.*

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
        val showPINField by remember { mutableStateOf(true) }
        var tbiEmailConfirmed: String = ""


        Scaffold(
            topBar = {
                GuardiansAccountTopAppBar(
                    onNavBackButton = {
                        IconButton(onClick = {
                            val intent = Intent(
                                this@GuardianAccountsActivity,
                                GuardianMainSettings::class.java
                            )
                            startActivity(intent)
                            finish()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_arrow_back_24),
                                null
                            )
                        }
                    }
                ) {
                    LogoutButton(activity = this@GuardianAccountsActivity)
                }
            },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 16.dp),
                    Arrangement.Top,
                    Alignment.CenterHorizontally
                ) {
                    var firstName: String by remember { mutableStateOf("") }
                    var lastName: String by remember { mutableStateOf("") }
                    var email: String by remember { mutableStateOf("") }
                    var password: String by remember { mutableStateOf("") }
                    var tbiEmail: String by remember { mutableStateOf("") }
                    var PIN: String by remember { mutableStateOf("") }


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
                    RecallifyCustomHeader(title = "Account Details")
                    Row(
                        modifier = Modifier.padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "First Name:",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = firstName.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        })
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {

                        Text(
                            text = "Last Name:",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = lastName.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        })
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = "Email:",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
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
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = "TBI Email:",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        if (tbiEmailConfirmed.isEmpty()) {
                            Text(text = "No patient's email connected to this account")
                        } else {
                            Text(text = tbiEmailConfirmed)
                        }


                    }
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    RecallifyCustomHeader(title = "Add new TBI user")
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        if (showEmailField) {
                            OutlinedTextField(
                                value = tbiEmail,
                                onValueChange = { newValue -> tbiEmail = newValue },
                                label = { Text("TBI Email Address") },
                                placeholder = {
                                    Text(text = "Example: user@gmail.com")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        tbiEmail = ""
                                    }) {
                                        Icon(
                                            painterResource(id = R.drawable.backspace_48),
                                            null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
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
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        if (showPINField) {
                            val maxChar = 4
                            OutlinedTextField(
                                value = PIN,
                                onValueChange = { newValue -> if (newValue.length <= maxChar) PIN = newValue },
                                label = { Text("TBI Account Pin") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                placeholder = {
                                    Text(text = "Example: 4231")
                                },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        PIN = ""
                                    }) {
                                        Icon(
                                            painterResource(id = R.drawable.backspace_48),
                                            null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            )
                        } else {
                            Text(
                                text = "",
                                color = Color.Gray,
                                modifier = Modifier.clickable { showEmailField = true }
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SaveButton(tbiEmail = tbiEmail, PIN = PIN, database, current)
                        Spacer(modifier = Modifier.padding(horizontal = 30.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            RecallifyCustomHeader(title = "Let's head Out. ⚡")
                            Button(
                                onClick = {
                                    val intent = Intent(
                                        this@GuardianAccountsActivity,
                                        GuardiansDashboardActivity::class.java
                                    )
                                    startActivity(intent)
                                    finish()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.primaryVariant
                                )
                            ) {
                                Text(
                                    text = "Go to Dashboard",
                                    style = MaterialTheme.typography.button
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SaveButton(tbiEmail: String, PIN: String, database: DatabaseReference, current: String) {

        var isButtonClicked by remember { mutableStateOf(false) }

        Button(
            onClick = {
                val encodedEmail = tbiEmail.replace(".", "_")
                val userIdCheck = database.child("connections").child(encodedEmail).child("userID")
                val pinCheck = database.child("connections").child(encodedEmail).child("PIN")
                val userEmailCheck = database.child("connections").child(encodedEmail).key

                userIdCheck.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val uid = dataSnapshot.getValue(String::class.java)
                            Log.i("This is the uid", uid!!)
                            pinCheck.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot2: DataSnapshot) {
                                    if (dataSnapshot2.exists()) {
                                        val pin = dataSnapshot2.getValue(String::class.java)
                                        Log.i("This is the pin", pin!!)
                                        if (userEmailCheck.equals(encodedEmail) && (pin.equals(PIN))) {
                                            database.child("GuardiansLinkTable").child(current)
                                                .child("TBI_ID").setValue(uid)

                                            isButtonClicked = true

                                            database.child(current).child("profile")
                                                .child("TBI Email")
                                                .setValue(tbiEmail)

                                            Toast.makeText(
                                                this@GuardianAccountsActivity,
                                                "Congratulations! Accounts have been connected",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        } else {
                                            Toast.makeText(
                                                this@GuardianAccountsActivity,
                                                "Wrong Email or PIN",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e(
                                        "onCancelled",
                                        "Unable to read value: ${error.toException()}"
                                    )
                                }
                            })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(
                            "onCancelled",
                            "Unable to read value: ${error.toException()}"
                        )
                    }
                })
            },
            enabled = !isButtonClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Save",
                style = MaterialTheme.typography.button
            )
        }
    }

    @Composable
    fun LogoutButton(activity: GuardianAccountsActivity) {
        val showDialog = remember { mutableStateOf(false) }
        IconButton(
            onClick = {
                showDialog.value = true
            }
        ) {
            Icon(
                painterResource(id = R.drawable.round_logout_24),
                "log out button"
            )
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text(
                        text = "Log Out.",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(
                            vertical = 8.dp
                        )
                    )
                },
                text = {
                    Text(
                        text = "Logging out of your account. Come back soon!",
                        style = MaterialTheme.typography.body1
                    )
                },
                backgroundColor = Color.White,
                shape = MaterialTheme.shapes.medium,
                confirmButton = {
                    TextButton(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                            showDialog.value = false
                        },
                    ) {
                        Text(
                            text = "Log Out",
                            style = MaterialTheme.typography.button.copy(
                                color = MaterialTheme.colors.error,
                                fontWeight = FontWeight.Medium
                            ),
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog.value = false },
                    ) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.button.copy(
                                color = MaterialTheme.colors.onBackground,
                                fontWeight = FontWeight.Medium
                            ),
                        )
                    }
                }
            )
        }
    }
}


