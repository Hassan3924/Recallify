package com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.recallify.R
import com.example.recallify.view.ui.theme.RecallifyTheme
import java.util.*
import kotlin.math.abs
import kotlin.math.log10

class ThinkfastProgressActivity : AppCompatActivity() {

    private var selectedDate by mutableStateOf("0000-00-00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thinkfast_progress)
        val thinkfastProgressCompose: ComposeView = findViewById(R.id.activity_think_fast_progress_screen)
        thinkfastProgressCompose.setContent {
            RecallifyTheme {
                ThinkFastProgressScreen()
            }
        }
    }


    @Composable
    fun ThinkFastProgressScreen() {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colors.surface)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.progress),
                    contentDescription = "progress",
                    modifier = Modifier
                        .fillMaxWidth(0.3F)
                        .size(50.dp)
                )
                Text(
                    text = "Analyze Think Fast Progress",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 40.dp, bottom = 20.dp)
            ) {
                Text(
                    text = "Please select date: ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = selectedDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    color = Color.Black
                )
            }
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .offset(y = (+20).dp)
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = RoundedCornerShape(25.dp)

                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        DatePickerView()
                        Spacer(modifier = Modifier.height(30.dp))
                        Button(
                            onClick =
                            {
                                val intent = Intent(
                                    this@ThinkfastProgressActivity,
                                    ProgressActivity::class.java)
                                intent.putExtra("currentDate",selectedDate)
                                startActivity(intent)
                                finish()
                            },
                            enabled = selectedDate != "0000-00-00",
                            colors =
                            ButtonDefaults.buttonColors(
                                backgroundColor = Color.Yellow,
                                disabledBackgroundColor = Color.Yellow
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .width(200.dp)
                                .height(50.dp)
                        ) {
                            Text(
                                text = "CLICK",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W500,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }

    private fun Int.length() = when(this) {
        0 -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }

    @Composable
    fun DatePickerView() {
        val currentContext = LocalContext.current
        val today = Calendar.getInstance()

        AndroidView(
            factory = {
                val datePickerLayout = LayoutInflater
                    .from(currentContext)
                    .inflate(R.layout.date_picker, null, false)
                val datePickerView: DatePicker = datePickerLayout.findViewById(R.id.datePicker)

                datePickerView
            },
            update = { datePickerView ->
                datePickerView.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
                ) {
                        _, year, _month, day ->
                    val month = _month + 1

                    val digitCounter = month.length()
                    val digitCounterDay = day.length()

                    selectedDate = if (digitCounter != 2) {
                        if (digitCounterDay != 2) {
                            "$year-0$month-0$day"
                        } else{
                            "$year-0$month-$day"
                        }
                    } else {
                        if(digitCounterDay!=2){
                            "$year-$month-0$day"
                        } else{
                            "$year-$month-$day"
                        }
                    }
                }
            }
        )
    }
}