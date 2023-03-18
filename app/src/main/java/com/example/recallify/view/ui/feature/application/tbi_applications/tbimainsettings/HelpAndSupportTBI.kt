package com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recallify.R
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.GuardianMainSettings
import com.example.recallify.view.ui.theme.RecallifyTheme
import java.net.URLEncoder

class HelpAndSupportTBI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_help_support)

        val privacySecurity: ComposeView = findViewById(R.id.help_support)

        privacySecurity.setContent {
            RecallifyTheme {
                HelpAndSupportScreen()
            }
        }
    }

    @Composable
    fun HelpAndSupportScreen() {
        Scaffold(
            topBar = { HelpSupportTopBar() },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(top = 4.dp)
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Column(
                        modifier = Modifier.padding(
                            top = 100.dp,
                            bottom = 10.dp,
                            start = 10.dp,
                            end = 10.dp
                        )
                    ) {

                        MyImage()

                        Card(
                            modifier = Modifier.padding(top = 30.dp),
                            elevation = 5.dp
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier
                                    .clickable(onClick = {

                                    })
                                    .padding(horizontal = 10.dp, vertical = 10.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(text = "FAQs")
                            }
                        }

                        Card(
                            modifier = Modifier.padding(top = 30.dp),
                            elevation = 5.dp
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        openContactUs()
                                    })
                                    .padding(horizontal = 10.dp, vertical = 10.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(text = "Contact us")
                            }
                        }


//                        Card(
//                            modifier = Modifier.padding(top = 30.dp),
//                            elevation = 5.dp
//                        ) {
//                            Row(
//                                horizontalArrangement = Arrangement.Start,
//                                modifier = Modifier
//                                    .clickable(onClick = {
//                                        openWhatsApp(this@HelpAndSupport)
//                                    })
//                                    .padding(horizontal = 10.dp, vertical = 10.dp)
//                                    .fillMaxWidth()
//                            ) {
//                                Text(text = "WhatsApp us")
//                            }
//                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Card() {
                }
            }
        }
    }

    @Composable
    private fun HelpSupportTopBar() {
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
                    text = "Help & Support",
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }

    private fun openWhatsApp(context: Context) {
        val url = "https://api.whatsapp.com/send?phone=+971566346194"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

//    private fun openContactUs() {
//
//        val intent = Intent(Intent.ACTION_SEND)
//        intent.type = "plain/text"
//        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("hassanwork3924@gmail.com"))
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Recallify : Help")
//        intent.putExtra(Intent.EXTRA_TEXT, "I need to inform you . . .")
//        startActivity(Intent.createChooser(intent, "Recallify - Help"))
//
//    }

    private fun openContactUs() {
        val phoneNumber = "+971566346194"
        val message = "I need to inform you . . ."

        val whatsAppUri = Uri.parse("https://wa.me/$phoneNumber/?text=${message.encodeURIComponent()}")

        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "hassanwork3924@gmail.com", null))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Recallify : Help")
        intent.putExtra(Intent.EXTRA_TEXT, "I need to inform you . . .")
        val whatsAppIntent = Intent(Intent.ACTION_VIEW, whatsAppUri)

        val chooserIntent = Intent.createChooser(intent, "Recallify - Help")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(whatsAppIntent))

        startActivity(chooserIntent)
    }

    private fun String.encodeURIComponent(): String {
        return URLEncoder.encode(this, "UTF-8")
    }


//    @Composable
//    fun HelpSupportScreen(
//        onWhatsAppClick: () -> Unit,
//        onContactUsClick: () -> Unit
//    ) {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colors.background
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp)
//            ) {
//                Button(
//                    modifier = Modifier.fillMaxWidth(),
//                    onClick = { onWhatsAppClick() }
//                ) {
//                    Text("WhatsApp")
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//                Button(
//                    modifier = Modifier.fillMaxWidth(),
//                    onClick = { onContactUsClick() }
//                ) {
//                    Text("Contact Us")
//                }
//            }
//        }
//    }
//}

    @Composable
    fun MyImage() {
        Image(
            painter = painterResource(R.drawable.help_support),
            contentDescription = "Help and Support",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}