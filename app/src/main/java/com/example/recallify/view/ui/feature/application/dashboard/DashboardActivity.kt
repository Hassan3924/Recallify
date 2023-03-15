package com.example.recallify.view.ui.feature.application.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.accounts.AccountsActivity
import com.example.recallify.view.ui.feature.application.dailydiary.DailyDiaryActivity
import com.example.recallify.view.ui.feature.application.sidequest.SideQuestActivity
import com.example.recallify.view.ui.feature.application.thinkfast.ThinkFastActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.CommonColor
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.example.recallify.view.ui.theme.light_Primary
import com.example.recallify.view.ui.theme.light_Secondary
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> true
                R.id.bottom_daily_diary -> {
                    startActivity(Intent(applicationContext, DailyDiaryActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_side_quest -> {
                    startActivity(Intent(applicationContext, SideQuestActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_think_fast -> {
                    startActivity(Intent(applicationContext, ThinkFastActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_accounts -> {
                    startActivity(Intent(applicationContext, AccountsActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }


        val dashBoardCompose: ComposeView = findViewById(R.id.activity_dash_board_screen)
        dashBoardCompose.setContent {
            RecallifyTheme {
                DashBoardScreen()
            }
        }

    }

    @Composable
    fun DashBoardScreen() {
        Scaffold(
            bottomBar = { BottomBarFiller() },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValue ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValue)
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp)
                        .padding(4.dp)
                ) {
                    Column {
                        HeaderUI()
                        TaskCardUI()
                        StatisticUI()
                        DescriptionUI()
                    }
                }
            }
        }
    }

    @Composable
    fun DescriptionUI() {
        Column(
            modifier = Modifier.padding(horizontal = 30.dp)
        ) {
            Text(
                text = "Description",
                fontSize = 16.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Make a youtube video on  Task management app user " +
                        "interface and make sure to post it on youtube, " +
                        "also create thumbnail and other pages",
                fontSize = 11.sp,
                color = CommonColor,
                fontWeight = FontWeight.Normal
            )
        }
    }

    @Composable
    fun StatisticUI() {
        Column(
            modifier = Modifier.padding(30.dp)
        ) {
            Text(
                text = "Jetpack Compose UI Design",
                fontSize = 16.sp,
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.round_access_time_24),
                        contentDescription = "",
                        tint = Color(0xFF818181),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "09.00 AM - 11.00 AM",
                        fontSize = 12.sp,
                        color = Color(0xFF818181),
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE1E3FA))
                        .border(
                            width = 0.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "On Going",
                        fontSize = 10.sp,

                        color = Color(0xFF7885B9)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Statistic",
                fontSize = 16.sp,
                color = CommonColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                StatisticProgressUI()
                Spacer(modifier = Modifier.width(12.dp))
                // StatisticIndicatorUI()
            }
        }
    }

    @Composable
    fun TaskCardUI() {
        val annotatedString1 = AnnotatedString.Builder("4/6 Task")
            .apply {
                addStyle(
                    SpanStyle(
                        color = MaterialTheme.colors.primaryVariant,
                    ), 0, 3
                )
            }

        Card(
            backgroundColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(top = 40.dp),
            elevation = 0.dp,
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Daily Task",
                        fontSize = 12.sp,
                        color = MaterialTheme.colors.primaryVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.round_access_time_24),
                            contentDescription = "",
                            tint = MaterialTheme.colors.primary,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = annotatedString1.toAnnotatedString(),
                            fontSize = 18.sp,
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }


                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Almost finished,\nkeep it up",
                        fontSize = 13.sp,
                        color = Color(0xFF292D32),
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.large)
                            .border(
                                width = 0.dp,
                                color = Color.Transparent,
                                shape = MaterialTheme.shapes.large
                            ),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Daily Task",
                            fontSize = 10.sp,
                            modifier = Modifier.align(alignment = CenterVertically),
                        )
                    }


                }


                ProgressBarUI(percentage = 67f)


            }
        }
    }

    @Composable
    fun ProgressBarUI(percentage: Float) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(100.dp)
                    .padding(6.dp)
            ) {
                drawCircle(
                    SolidColor(Color(0xFFE3E5E7)),
                    size.width / 2,
                    style = Stroke(26f)
                )
                val convertedValue = (percentage / 100) * 360
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(light_Secondary, light_Primary)
                    ),
                    startAngle = -90f,
                    sweepAngle = convertedValue,
                    useCenter = false,
                    style = Stroke(26f, cap = StrokeCap.Round)
                )
            }

            val annotatedString2 = AnnotatedString.Builder("${percentage.toInt()}%\nDone")
                .apply {
                    addStyle(
                        SpanStyle(
                            color = MaterialTheme.colors.secondaryVariant,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Normal
                        ), 4, 8
                    )
                }

            Text(
                text = annotatedString2.toAnnotatedString(),
                fontSize = 14.sp,
                color = MaterialTheme.colors.primaryVariant,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

        }
    }

    @Composable
    fun HeaderUI() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, John Recallify",
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Let's do your today task",
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Image(
                painter = painterResource(id = R.drawable.image_default),
                contentDescription = "",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
            )

        }
    }

    @Composable
    fun StatisticProgressUI(primaryPercentage: Float = 60f, secondaryPercentage: Float = 15f) {
        Box(
            modifier = Modifier
                .size(120.dp),
            contentAlignment = Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(100.dp)
            ) {
                drawCircle(
                    SolidColor(Color(0xFFE3E5E7)),
                    size.width / 2,
                    style = Stroke(34f)
                )
                val convertedPrimaryValue = (primaryPercentage / 100) * 360
                val convertedSecondaryValue =
                    ((secondaryPercentage / 100) * 360) + convertedPrimaryValue
                drawArc(
                    brush = SolidColor(light_Secondary),
                    startAngle = -90f,
                    sweepAngle = convertedSecondaryValue,
                    useCenter = false,
                    style = Stroke(34f, cap = StrokeCap.Round)
                )
                drawArc(
                    brush = SolidColor(light_Primary),
                    startAngle = -90f,
                    sweepAngle = convertedPrimaryValue,
                    useCenter = false,
                    style = Stroke(34f, cap = StrokeCap.Round)
                )
            }

            val annotatedString2 =
                AnnotatedString.Builder("${(primaryPercentage + secondaryPercentage).toInt()}%\nDone")
                    .apply {
                        addStyle(
                            SpanStyle(
                                color = CommonColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal
                            ), 4, 8
                        )
                    }

            Text(
                text = annotatedString2.toAnnotatedString(),

                fontSize = 20.sp,
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }

//    @Composable
//    fun StatisticIndicatorUI() {
//        Column(
//            modifier = Modifier
//                .height(120.dp) ,
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            IndicatorItemUI(text = "Finish on time")
//            IndicatorItemUI(color = light_Secondary, text = "Past the deadline")
//            IndicatorItemUI(color = Color(0xFFE3E5E7), text = "Still ongoing")
//        }
//    }

//    @Composable
//    fun IndicatorItemUI(color: Color = light_Primary,text:String) {
//        Row {
//            Icon(
//                painter = painterResource(id = coil.base.R.drawable.button_shape),
//                contentDescription = "",
//                tint = color,
//                modifier = Modifier.size(20.dp)
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Text(
//                text = text,
//
//                fontSize = 12.sp,
//                color = Color(0xFF818181),
//                fontWeight = FontWeight.Normal
//            )
//        }
//    }
}