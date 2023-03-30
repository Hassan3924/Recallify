package com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.ui.theme.RecallifyTheme

class ThinkfastRulesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thinkfast_rules)
        val thinkfastRulesCompose: ComposeView = findViewById(R.id.activity_think_fast_rules_screen)
        thinkfastRulesCompose.setContent {
            val context: Context = LocalContext.current
            RecallifyTheme {
                RulesScreen {
                    Toast.makeText(context, "Loading your stage...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ThinkfastRulesActivity, ThinkFastLoadingActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    @Composable
    private fun RulesScreen(playGame: () -> Unit) {
        val ruleScrollState = rememberScrollState()

        Scaffold(backgroundColor = MaterialTheme.colors.surface) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.think_fast_question2),
                        contentDescription = "think fast picture",
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 24.dp)
                            .size(200.dp)
                            .fillMaxWidth(),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        "Here is an explanation on the rules for the game.\nOnce done please swipe down to the button to continue.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = ruleScrollState),
                    ) {
                        Rules(
                            ruleNumber = "1",
                            imageSource = R.drawable.think_fast_rule1,
                            ruleMessage = "Look at the Image for the specified duration mentioned at the top of the screen."
                        )
                        Rules(
                            ruleNumber = "2",
                            imageSource = R.drawable.think_fast_rule2,
                            ruleMessage = "After the specified duration, questions will be displayed to you."
                        )
                        Rules(
                            ruleNumber = "3",
                            imageSource = R.drawable.think_fast_rule3,
                            ruleMessage = "Click the option which you think is correct"
                        )
                        Button(
                            onClick = { playGame() },
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text("Play Game")
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Rules(
        ruleNumber: String,
        imageSource: Int,
        ruleMessage: String,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(4.dp),
            elevation = 5.dp,
            backgroundColor = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    "Rule $ruleNumber",
                    style = MaterialTheme.typography.caption.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                        .padding(start = 16.dp)
                )
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = imageSource),
                        contentDescription = "rules image",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = ruleMessage,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                }
            }
        }
    }
}