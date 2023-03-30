package com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recallify.R
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.GuardianMainSettings
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme

class PrivacyAndSecurityTBI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_privacy_security)

        val privacySecurity: ComposeView = findViewById(R.id.privacy_security)

        privacySecurity.setContent {
            RecallifyTheme {
                PrivacyAndSecurityScreen()
            }
        }
    }

    @Composable
    fun PrivacyAndSecurityScreen() {

        Scaffold(
            topBar = { PrivacySecurityTopBar() },
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
                        text = "As a team dedicated to helping individuals with traumatic brain injuries (TBI) and their guardians, we understand the importance of privacy and security in maintaining the trust of our users. We are committed to ensuring that your personal information is kept confidential and secure while using our memory improvement application.",
                        style = TextStyle(fontSize = 16.sp, fontFamily = MaterialTheme.typography.body1.fontFamily),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
                    )
                }

                item {
                    Text(
                        text = "Our Privacy and Security settings are designed to give you control over what data is collected, how it's shared, and how it's managed. We encourage you to review and adjust these settings to your preferences.",
                        style = TextStyle(fontSize = 16.sp, fontFamily = MaterialTheme.typography.body1.fontFamily),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    Text(
                        text = "We know that memory loss and other cognitive challenges can be difficult to manage, which is why we are here to support you. Our application is designed to improve memory and help you manage daily tasks more easily. We hope that our application can make a meaningful difference in your life.",
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
    private fun PrivacySecurityTopBar() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 25.dp)
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
                    text = "Privacy and Security",
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }
}
