package com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.recallify.R
import com.example.recallify.view.common.components.CustomFaq
import com.example.recallify.view.ui.theme.RecallifyTheme

class FactsAndQuestionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facts_and_questions)

        val factsAndQuestionsActivity: ComposeView = findViewById(R.id.activity_tbi_faq)
        factsAndQuestionsActivity.setContent {
            RecallifyTheme {
                FactsAndQuestionTBIScreen()
            }
        }
    }

    @Composable
    private fun FactsAndQuestionTBIScreen() {
        val scaffoldState = rememberScaffoldState()
        val scrollableState = rememberScrollState()
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                FAQsTopAppBar {
                    IconButton(onClick = {
                        val intent = Intent(
                            this@FactsAndQuestionsActivity,
                            HelpAndSupportTBI::class.java
                        )
                        startActivity(intent)
                        finish()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_arrow_back_24),
                            contentDescription = null
                        )
                    }
                }
            },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                        .verticalScroll(scrollableState),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    CustomFaq(
                        title = "What is Recallify?",
                        message = "Recallify is a productivity application for individuals in the " +
                                "early stages of Traumatic Brain Injury.",
                    )
                    CustomFaq(
                        title = "What features does the app offer to help improve memory for TBI patients?",
                        message = "Daily Activity --\nAllows users to record their day and what " +
                                "they did." +
                                "\n\nSide Quest --\nTakes information from " +
                                "Daily Activity and questions users about it. Users can later on" +
                                " review their mistakes.\n\nThink Fast --\nUser have " +
                                "to look at a visual for a certain period of time and try to recall " +
                                "variations in the visual in a series of questions. It can be reviewed later on as well.",
                    )
                    CustomFaq(
                        title = "Are we backed by scientific research or developed in collaboration with medical professionals?\n",
                        message = "The app has been developed through extensive research and is backed by medical viewings."
                    )
                    CustomFaq(
                        title = " How does the app track progress and show improvements in memory over time?",
                        message = "It tracks progress by seeing how the user is doing over 6 days and a progress graph" +
                                " is displayed on the dashboard. If the user is getting better at answering the tasks it's" +
                                " a sign they are improving\n"
                    )
                    CustomFaq(
                        title = "Are there any customization options to tailor the app to individual needs and preferences?",
                        message = "At the moment, no. We are still in beta development and are looking to adding customization features " +
                                "to further improve the user experience. So be on the look out for updates."
                    )
                    CustomFaq(
                        title = "How does the app ensure user privacy and data security is secured?",
                        message = "We do not share our user's information with any third-party vendor" +
                                "all information are consumed by the system and the GUARDIAN in charge." +
                                " We want to keep our users safe and privacy secure."
                    )
                    CustomFaq(
                        title = "Is the app suitable for TBI patients of all ages and severity levels?",
                        message = "The application is intended for ages 9 and above who have sufficient" +
                                " technological knowledge and are in our audience scope of individuals with" +
                                " early stages of Traumatic Brain Injury"
                    )
                    CustomFaq(
                        title = "Are there any costs associated with using the app?",
                        message = "Nothing, it's free.😁"
                    )
                    CustomFaq(
                        title = "Is the app available on both Android and iOS devices?",
                        message = "It's only available on Android at the moment."
                    )
                }
            }
        }
    }

    @Composable
    private fun FAQsTopAppBar(
        onNavBackButton: @Composable (() -> Unit),
    ) {
        Box(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
        ) {
            TopAppBar(
                title = { Text("FAQs") },
                backgroundColor = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = { onNavBackButton() }
            )
        }
    }
}