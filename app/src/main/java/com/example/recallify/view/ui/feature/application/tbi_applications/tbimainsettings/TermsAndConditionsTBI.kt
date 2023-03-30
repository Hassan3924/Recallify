package com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme

class TermsAndConditionsTBI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_terms_condition)

        val privacySecurity: ComposeView = findViewById(R.id.terms_conditions)

        privacySecurity.setContent {
            RecallifyTheme {
                TermsAndConditionsScreen()
            }
        }
    }

    @Composable
    fun TermsAndConditionsScreen() {
        Scaffold(
            topBar = { TermsConditionTopBar() },
            bottomBar = { BottomBarFiller() },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = rememberLazyListState()
            ) {
                val robotoBlack = FontFamily(Font(R.font.roboto_black))

                item {
                    Text(
                        text = "Welcome to our memory improvement application for individuals with traumatic brain injuries (TBI) and their guardians. Our goal is to help you improve your memory and manage daily tasks more easily. Before you start using our application, please read our Terms & Conditions carefully."
                                ,style = TextStyle(fontSize = 16.sp, fontFamily = MaterialTheme.typography.body1.fontFamily),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
                    )
                }

                item {
                    Text(
                        text = "By using our application, you agree to our Terms & Conditions, which outline the terms of use and legal obligations that govern our relationship with you as a user. These terms include important information about the limitations of our liability, intellectual property rights, and how we handle your personal information.",
                        style = TextStyle(fontSize = 16.sp, fontFamily = MaterialTheme.typography.body1.fontFamily),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    Text(
                        text = "We have made every effort to ensure that our Terms & Conditions are clear and easy to understand. If you have any questions or concerns, please don't hesitate to contact us.",
                        style = TextStyle(fontSize = 16.sp, fontFamily = MaterialTheme.typography.body1.fontFamily),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    Text(
                        text = "If you have any questions or concerns about your privacy or security, please don't hesitate to contact us. We are here to help you.",
                        style = TextStyle(fontSize = 16.sp, fontFamily = MaterialTheme.typography.body1.fontFamily),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    Text(
                        text = "\n" +
                                "We are committed to providing a high-quality and secure experience for all our users. Our Terms & Conditions are designed to protect your rights and ensure that our application operates in a fair and transparent manner.",
                        style = TextStyle(fontSize = 16.sp, fontFamily = MaterialTheme.typography.body1.fontFamily),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
//                                MaterialTheme.typography.body1.copy(
//                                fontWeight = FontWeight.Medium
//                                )
                    )
                }

                item {
                    Text(
                        text = "Thank you for choosing our memory improvement application.",
                        style = TextStyle(fontSize = 16.sp, fontFamily = MaterialTheme.typography.body1.fontFamily),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
//                                MaterialTheme.typography.body1.copy(
//                                fontWeight = FontWeight.Medium
//                                )
                    )
                }

                item {
                    Text(
                        text = "Best regards,\nTeam Techno",
                        style = TextStyle(fontSize = 16.sp, fontFamily = MaterialTheme.typography.body1.fontFamily),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun TermsConditionTopBar() {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = {
                        val intent = Intent(
                            applicationContext,
                            MainSettingsTBI::class.java
                        )
                        startActivity(intent)
                        finish() //added by rb
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
                    text = "Terms & Conditions",
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(2f)
                )

            }
        }
    }
}
