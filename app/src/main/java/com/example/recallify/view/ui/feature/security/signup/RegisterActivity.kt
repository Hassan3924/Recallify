package com.example.recallify.view.ui.feature.security.signup

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.tbi_applications.accounts.AccountsActivity
import com.example.recallify.view.ui.feature.guradian_application.guardian_account.GuardianAccountsActivity
import com.example.recallify.view.ui.feature.security.signin.LoginActivity
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    @Composable
    fun TextFieldWithError(
        modifier: Modifier = Modifier,
        value: String,
        onValueChange: (String) -> Unit,
        isError: Boolean,
        label: String,
        errorMessage: String,
        placeholder: String? = null,
        leadingIcon: (@Composable () -> Unit)? = null,
        trailingIcon: (@Composable () -> Unit)? = null,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        visualTransformation: VisualTransformation = VisualTransformation.None
    ) {
        val textColor = if (isError) Color.Red else Color.Black
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label, color = textColor) },
            placeholder = { placeholder?.let { Text(text = it, color = textColor) } },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            isError = isError
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val registerActivityCompos: ComposeView = findViewById(R.id.activity_register_screen)
        registerActivityCompos.setContent {
            RecallifyTheme {
                SignUpScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun SignUpScreen() {
        val context = LocalContext.current
        val auth = Firebase.auth
        val database = Firebase.database.reference

        var firstname by remember {
            mutableStateOf("")
        }
        var lastname by remember {
            mutableStateOf("")
        }
        var email by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        var confirmPassword by remember {
            mutableStateOf("")
        }
        var role by remember {
            mutableStateOf("")
        }
        var PIN by remember {
            mutableStateOf("")
        }
        var isExpanded by remember {
            mutableStateOf(false)
        }
        val errors = mutableListOf<String>()

        PIN = fourNumberGenerator()

        Scaffold(
            scaffoldState = rememberScaffoldState(),
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
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
                        onClick = {
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
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
                Text(
                    text = "Sign up, right here.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.h4
                )
                Text(
                    text = "These will just take a few minutes",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.h6.copy(
                        color = Color.Gray
                    )
                )
                Card(
                    modifier = Modifier.weight(1.5f),
                    backgroundColor = MaterialTheme.colors.background,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // First name
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = firstname,
                                onValueChange = { firstname = it },
                                singleLine = true,
                                label = { Text(text = "Firstname") },
                                placeholder = { Text("your first name") },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit_name),
                                        contentDescription = "first name icon",
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.Gray
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { firstname = "" }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.backspace_48),
                                            contentDescription = "firstname",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Ascii,
                                    imeAction = ImeAction.Next
                                )
                            )
                            // Last name
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = lastname,
                                onValueChange = { lastname = it },
                                singleLine = true,
                                label = { Text(text = "Lastname") },
                                placeholder = { Text("your last name") },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit_name),
                                        contentDescription = "first name icon",
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.Gray
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { lastname = "" }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.backspace_48),
                                            contentDescription = "lastname",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Ascii,
                                    imeAction = ImeAction.Next
                                )
                            )
                            // email address
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = email,
                                onValueChange = { email = it },
                                singleLine = true,
                                label = { Text("Email address") },
                                placeholder = { Text("example@gmail.com") },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_email_24),
                                        contentDescription = "first name icon",
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
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Ascii,
                                    imeAction = ImeAction.Next
                                )
                            )
                            // password
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = password,
                                onValueChange = { password = it },
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
                                    IconButton(onClick = {
                                        TODO("Implement password change")
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.visibility_48),
                                            contentDescription = "email",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                visualTransformation = PasswordVisualTransformation()
                            )
                            // confirm password
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                singleLine = true,
                                label = { Text("Confirm password") },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_password_24),
                                        contentDescription = "password",
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.Gray
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        TODO("Implement password change")
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.visibility_48),
                                            contentDescription = "email",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                            visualTransformation = PasswordVisualTransformation()
                            )
                            // user role
                            ExposedDropdownMenuBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                expanded = isExpanded,
                                onExpandedChange = { isExpanded = it }
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = role,
                                    onValueChange = { },
                                    readOnly = true,
                                    label = { Text("Choose role") },
                                    placeholder = { Text("Select a role") },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.role),
                                            contentDescription = "role",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Gray
                                        )
                                    },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                )
                                ExposedDropdownMenu(
                                    expanded = isExpanded,
                                    modifier = Modifier.fillMaxWidth(),
                                    onDismissRequest = { isExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        content = {
                                            Text(text = "TBI Patient")
                                        },
                                        onClick = {
                                            role = "TBI Patient"
                                            isExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        content = {
                                            Text(text = "Guardian")
                                        },
                                        onClick = {
                                            role = "Guardian"
                                            isExpanded = false
                                        }
                                    )
                                }
                            }
                            Button(
                                onClick = {

                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val user = task.result?.user
                                                val data = hashMapOf(
                                                    "firstname" to firstname,
                                                    "lastname" to lastname,
                                                    "email" to email,
                                                    "password" to password,
                                                    "role" to role,
                                                    "pin" to PIN
                                                )
                                                database.child("users")
                                                    .child(user?.uid.toString())
                                                    .child("profile")
                                                    .setValue(data)
                                                    .addOnSuccessListener {
                                                        Log.i(ContentValues.TAG, "RegistrationSuccessful: () -> ")
                                                    }
                                                    .addOnFailureListener {
                                                        Log.w(ContentValues.TAG, "RegistrationFailed: () -> ")
                                                    }

                                                val sanitizedEmail = email.replace(Regex("[.#$\\[\\]]"), "_")

                                                if (role == "TBI Patient") {

                                                    // Adding the UID under connection1
                                                    database.child("users")
                                                        .child("connections")
                                                        .child(sanitizedEmail)
                                                        .child("userID").setValue(user?.uid.toString())
                                                    database.child("users")
                                                        .child(user?.uid.toString())
                                                        .child("profile")
                                                        .child("PIN").setValue(PIN)
                                                    database.child("users")
                                                        .child("connections")
                                                        .child(sanitizedEmail)
                                                        .child("PIN").setValue(PIN)

                                                    val intent = Intent(context, AccountsActivity::class.java)
                                                    startActivity(intent)
                                                    finish()

                                                }
                                                else {

                                                    val intent = Intent(context, GuardianAccountsActivity::class.java)
                                                    startActivity(intent)
                                                    finish()

                                                }


                                            } else {
                                                Toast.makeText(context, "Registration failed!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Sign up",
                                    style = MaterialTheme.typography.button,
                                    fontSize = 18.sp
                                )
                            }
                            Text(
                                text = "Already have an Account?  Sign in.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp, bottom = 10.dp)
                                    .clickable {
                                        val intent = Intent(context, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @author hassan
     * */
    @Composable
    fun fourNumberGenerator(): String {
        val numbers = remember { mutableStateListOf<Int>() }
        while (numbers.size < 4) {
            val newNumber = (0..9).random()
            if (newNumber !in numbers) {
                numbers.add(newNumber)
            }
        }
        return numbers.joinToString(separator = "")
    }
}