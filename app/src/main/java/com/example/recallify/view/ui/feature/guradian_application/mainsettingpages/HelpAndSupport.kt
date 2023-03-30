package com.example.recallify.view.ui.feature.guradian_application.mainsettingpages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recallify.R
import com.example.recallify.view.ui.theme.RecallifyTheme
import java.net.URLEncoder

class HelpAndSupport : AppCompatActivity() {

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
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(modifier = Modifier.padding(bottom = 10.dp)) {
                        MyImage()

                        Card(
                            modifier = Modifier.padding(top = 30.dp),
                            elevation = 5.dp,
                            backgroundColor = MaterialTheme.colors.background
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        val intent = Intent(
                                            applicationContext,
                                            GuardianFactsAndQuestionsActivity::class.java
                                        )
                                        startActivity(intent)
                                        finish() //added by rb
                                        overridePendingTransition(
                                            R.anim.slide_in_right,
                                            R.anim.slide_out_left
                                        )
                                    })
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 10.dp
                                    )
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.baseline_question_answer_24),
                                        contentDescription = null,
                                        Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                    Text(
                                        text = "FAQs",
                                        style = MaterialTheme.typography.button
                                    )
                                }
                                Icon(
                                    painterResource(id = R.drawable.round_arrow_forward_24),
                                    contentDescription = null,
                                    Modifier.size(24.dp)
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.padding(top = 30.dp),
                            elevation = 5.dp,
                            backgroundColor = MaterialTheme.colors.background
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        openContactUs()
                                    })
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 10.dp
                                    )
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.round_contact_support_24),
                                        contentDescription = null,
                                        Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                    Text(
                                        text = "Contact Us",
                                        style = MaterialTheme.typography.button
                                    )
                                }
                            }
                        }
                        Text(
                            text = "With love from Recallify 💖\nversion 0.1.3",
                            style = MaterialTheme.typography.caption.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colors.onBackground.copy(
                                alpha = ContentAlpha.medium
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 90.dp),
                            textAlign = TextAlign.Center
                        )
                    }
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
                .padding(top = 4.dp)
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
                            GuardianMainSettings::class.java
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
                    text = "Customer Care",
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }

    private fun openContactUs() {
        val phoneNumber = "+971566346194"
        val message = "I need to inform you . . ."

        val whatsAppUri =
            Uri.parse("https://wa.me/$phoneNumber/?text=${message.encodeURIComponent()}")

        val intent =
            Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "hassanwork3924@gmail.com", null))
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