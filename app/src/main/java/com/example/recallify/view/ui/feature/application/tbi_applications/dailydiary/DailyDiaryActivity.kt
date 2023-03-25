package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.recallify.R
import com.example.recallify.view.common.components.DiaryTopAppBar
import com.example.recallify.view.common.components.TabDiary
import com.example.recallify.view.common.components.TabPage
import com.example.recallify.view.ui.feature.application.dailydiary.conversationSummary.SummarizeConversation
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity.DailyActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.DailyLogActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dashboard.DashboardActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.sidequest.SideQuestActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings.MainSettingsTBI
import com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast.ThinkFastActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DailyDiaryActivity : AppCompatActivity() {

    private val viewModel: ActivityViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_diary)

        /**
         * The navigation component for the bottom nav bar of the Recallify application. The view
         * targets the parent XML file in the layout directory and controls the pages that have
         * been selected or not.
         *
         * Each page is given a unique ID that identifies them and also a visual queue that tells
         * the user which page there on.
         *
         *
         * Bottom navigation view has a listener that listens for all the selection changes to the
         * pages on the screen. The item stored is the current page at which the scope is in.
         * In this case the **"Daily Diary Screen"**.
         *
         * @return The boolean value of a screen that has been selected. True | False.
         * False is the navigation to the desired screen.
         *
         * @author enoabasi
         *
         * @see BottomNavigationView
         * */
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_daily_diary
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                // path to the Home screen
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                // path to the Daily Diary screen
                R.id.bottom_daily_diary -> true

                // path to the Side Quest screen
                R.id.bottom_side_quest -> {
                    startActivity(Intent(applicationContext, SideQuestActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                // path to the Think fast screen
                R.id.bottom_think_fast -> {
                    startActivity(Intent(applicationContext, ThinkFastActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                // path to the Accounts screen
                R.id.bottom_accounts -> {
                    startActivity(Intent(applicationContext, MainSettingsTBI::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                // No path selected
                else -> false
            }
        }

        /**
         * This composable sets the Layout XMl view for the Daily Diary feature of the application.
         * By using the ID to identify the layout map in the "res/layout" directory
         *
         * @return The composable-layout interpolation of the composable function
         * **"DailyDiaryScreen()"**
         *
         * @author enoabasi
         * */
        val dailyDiaryCompose: ComposeView = findViewById(R.id.activity_daily_diary_screen)
        dailyDiaryCompose.setContent {
            RecallifyTheme {
                DailyDiaryScreen()
            }
        }
    }

    /**
     * Daily Diary Screen. The Daily Diary ...
     *
     * @return The recomposition feature of the daily diary.
     *
     * @author enoabasi
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun DailyDiaryScreen() {
        /**
         * The state of the Modal bottom sheet
         *
         * @author enoabasi
         * */
        val state = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )

        /**
         * The scope of the Scaffold Layout
         * @author enoabasi
         * */
        val scope = rememberCoroutineScope()

        /**
         * @author enoabasi
         * */
//        val fetchScope = rememberCoroutineScope()

        /**
         * The context of the application package. Most likely used in a toast or intent
         * @author enoabasi
         * */
        val context = LocalContext.current

        /**
         * The state of the tabs visited in the Daily diary feature. The tabs could be Activities
         * Logs.
         * @author enoabasi
         * */
        var tabPage by remember { mutableStateOf(TabPage.Activity) }

        /**
         * The database variable is a reference to the firebase database. It does not return any path
         * but an initialization to the Firebase database.
         *
         * @author hassan
         *
         * @see Firebase.database
         * */
        val database = Firebase.database

        /**
         * The auth variable is a reference to the firebase authentication. In this context it does
         * not return any key or authentication reference but an initialization to the firebase
         * authentication.
         * @author hassan
         * */
        val auth = Firebase.auth

        /**
         * The current user is a firebase authentication to the current user in the application.
         * This will return the current user's UID.
         *
         * @author hassan
         * */
        val currentUser = auth.currentUser?.uid!!

//        val ref = database.getReference("users").child(currentUser).child("conversation-summary").child(getCurrentDate())

        /**
         * **"children_of_logs"**, a data-snapshot reference gotten from firebase that is used to remember
         * the state of the database. In this context of the application is it used to remember
         * the children in the real-time database.
         *
         * It holds a [mutableStateListOf] DataSnapshots.
         *
         * @author hassan
         * */
        val childrenOfLogs = remember { mutableStateListOf<DataSnapshot>() }

        /**
         * **"children_of_activities"**, a data-snapshot reference gotten from firebase that is
         * used to remember the state of the database. In this context of the application is it
         * used to remember the children in the real-time database.
         *
         * It holds a [mutableStateListOf] DataSnapshots.
         *
         * @author hassan
         * */
        val childrenOfActivities = remember { mutableStateListOf<DataSnapshot>() }

        /**
         * This is used to remember the state of a coroutine. For if it is loading on not loading.
         * The logic is being implemented later in the code base. This loading has been set to
         * monitor the **Logs** loading phase.
         *
         * It will return the truth value of the code block in which it is being called.
         *
         * @author hassan
         * */
        var isLogsLoading by remember { mutableStateOf(true) }

        /**
         * This is used to remember the state of a coroutine. For if it is loading on not loading.
         * The logic is being implemented later in the code base. This loading has been set to
         * monitor the **Activities** loading phase.
         *
         * It will return the truth value of the code block in which it is being called.
         *
         * @author enoabasi
         * */
        var isActivitiesLoading by remember { mutableStateOf(true) }

        /**
         * This is used to remember the state of the selected date from a filter or calendar. The
         * date is used ot filter objects gotten from firebase database.
         *
         * @author hassan
         * */
        var selectedDate by remember { mutableStateOf(LocalDate.now()) }
        LaunchedEffect(selectedDate) {
            /**
             * The reference call for the **"Activity logs"** in the real-time database. The path
             * structure is as follows;
             *
             * ```markdown
             *  database-root-url
             *      |- users
             *          |- UID
             *              |- conversation-summary
             *                      |- selectedDate
             *                              |- database-children
             * ```
             * @see Firebase.database
             *
             * @author hassan
             * */
            val logsRef = database.getReference("users")
                .child(currentUser)
                .child("conversation-summary")
                .child(selectedDate.toString())

            /**
             * The reference call the for the **"Daily Dairy Activities"** in the real-time database.
             * The path structure is as follows;
             * ```markdown
             *  database-root-url
             *      |- users
             *          |- UID
             *              |- conversation-summary
             *                      |- selectedDate
             *                              |- database-children
             * ```
             * @author enoabasi
             * @see Firebase.database
             * */
            val activityRef = database.getReference("users")
                .child(currentUser)
                .child("dailyDairyDummy")
                .child(selectedDate.toString())

            logsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Snapshot", snapshot.toString()) // add this line
                    childrenOfLogs.clear()
                    snapshot.children.forEach { child ->
                        childrenOfLogs.add(child)
                    }
                    isLogsLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isLogsLoading = false
                }
            })

            activityRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("ActivitySnapshot", snapshot.toString())
                    childrenOfActivities.clear()
                    snapshot.children.forEach { child ->
                        childrenOfActivities.add(child)
                    }
                    isActivitiesLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isActivitiesLoading = false
                }
            })
        }

        Scaffold(
            bottomBar = { BottomBarFiller() },
            topBar = {
                DiaryTopAppBar(
                    clickCreate = {
                        scope.launch {
                            if (state.currentValue == ModalBottomSheetValue.Hidden) {
                                state.animateTo(ModalBottomSheetValue.Expanded, tween(500))
                            } else {
                                state.animateTo(ModalBottomSheetValue.Hidden, tween(500))
                            }
                        }
                    },
                    clickFilter = {
                        pickDate(context = context) {
                            selectedDate = it
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                    }
                )
            },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                horizontalAlignment = CenterHorizontally
            ) {
                ModalBottomSheetLayout(
                    sheetContent = {
                        Surface(modifier = Modifier.background(MaterialTheme.colors.primaryVariant)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .background(MaterialTheme.colors.secondary),
                                contentAlignment = Center
                            ) {
                                Text(
                                    "+ Create",
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.padding(0.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(vertical = 6.dp))
                        ActionSheetItem(
                            icon = R.drawable.daily_activity,
                            text = "Summarised Text",
                            onStart = {
                                val intent =
                                    Intent(
                                        this@DailyDiaryActivity,
                                        SummarizeConversation::class.java
                                    )
                                startActivity(intent)
                            }
                        )
                        Spacer(modifier = Modifier.padding(vertical = 6.dp))
                        ActionSheetItem(
                            icon = R.drawable.daily_activity,
                            text = "Daily activity",
                            onStart = {
                                val intent =
                                    Intent(
                                        this@DailyDiaryActivity,
                                        DailyActivity::class.java
                                    )
                                startActivity(intent)
                            }
                        )
                        Spacer(modifier = Modifier.padding(vertical = 5.dp))
                        ActionSheetItem(
                            icon = R.drawable.daily_log,
                            text = "Daily log",
                            onStart = {
                                val intent =
                                    Intent(
                                        this@DailyDiaryActivity,
                                        DailyLogActivity::class.java
                                    )
                                startActivity(intent)
                            }
                        )
                        Spacer(modifier = Modifier.padding(vertical = 5.dp))
                        ActionSheetItem(
                            icon = R.drawable.moment_snap,
                            text = "Moment snap",
                            onStart = {
                                // todo: implement the moment snap functionalities


                            }
                        )
                    },
                    sheetBackgroundColor = MaterialTheme.colors.background,
                    sheetElevation = 5.dp,
                    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    sheetState = state,
                ) {
                    Column {
                        TabDiary(selectTabIndex = tabPage.ordinal, onSelectTab = { tabPage = it })
                        when (tabPage.ordinal) {
                            0 -> {
                                when (val result = viewModel.response.value) {
                                    is DataState.Loading -> {
                                        LinearProgressIndicator()
                                    }
                                    is DataState.Success -> {
                                        LazyColumn {
                                            items(result.data) { activity ->
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(150.dp)
                                                        .padding(16.dp),
                                                    backgroundColor = MaterialTheme.colors.background
                                                ) {
                                                    Box(modifier = Modifier.fillMaxSize()) {
                                                        Image(
                                                            painter = rememberAsyncImagePainter(
                                                                activity.imageLinkInformation
                                                            ),
                                                            contentDescription = "Activity-Image",
                                                            contentScale = ContentScale.FillWidth,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    }
                                                    Column {
                                                        Row {
                                                            Text(text = activity.dateInformation.toString())
                                                            Text(text = activity.timeInformation.toString())
                                                        }
                                                        Text(text = activity.locationNameInformation.toString())
                                                        Row {
                                                            Text(text = activity.latInformation.toString())
                                                            Text(text = activity.latInformation.toString())
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    is DataState.Failed -> {
                                        Text(
                                            text = result.message,
                                            fontSize = MaterialTheme.typography.h5.fontSize
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = "Error fetching from network!",
                                            fontSize = MaterialTheme.typography.h5.fontSize
                                        )
                                    }
                                }
                                BackHandler(
                                    enabled = (state.currentValue == ModalBottomSheetValue.HalfExpanded ||
                                            state.currentValue == ModalBottomSheetValue.Expanded),
                                    onBack = {
                                        scope.launch {
                                            state.animateTo(
                                                ModalBottomSheetValue.Hidden,
                                                tween(400)
                                            )
                                        }
                                    }
                                )
                            }
                            1 -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 8.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = CenterHorizontally
                                ) {
                                    when {
                                        childrenOfLogs.isEmpty() -> {
                                            Box(
                                                Modifier.fillMaxSize(),
                                                contentAlignment = Center
                                            ) {
                                                Text(
                                                    text = "No data available for\n ${
                                                        selectedDate.format(
                                                            DateTimeFormatter.ISO_DATE
                                                        )
                                                    }",
                                                    modifier = Modifier.padding(16.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }

                                        else -> {
                                            LazyColumn {
                                                items(childrenOfLogs) { child ->
                                                    Card(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(16.dp),
                                                        elevation = 8.dp,
                                                    ) {
                                                        Text(
                                                            text = child.value.toString(),
                                                            modifier = Modifier.padding(16.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                BackHandler(
                                    enabled = (state.currentValue == ModalBottomSheetValue.HalfExpanded ||
                                            state.currentValue == ModalBottomSheetValue.Expanded),
                                    onBack = {
                                        scope.launch {
                                            state.animateTo(
                                                ModalBottomSheetValue.Hidden,
                                                tween(300)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the selected date from the a date picker dialog and uses the value for filtering through
     * the data in the database.
     *
     * @return A unit selected date from the calendar
     *
     * @author hassan
     * */
    private fun pickDate(context: Context, onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    /**
     * A component that defines an action menu in an action sheet for a modal bottom sheet
     *
     * @param icon The icon of the action (in the modal bottom sheet)
     * @param onStart The action event to be performed, most likely an Intent
     * @param text The title of the action
     *
     * @author enoabasi
     * */
    @Composable
    fun ActionSheetItem(icon: Int, text: String, onStart: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable {
                    onStart()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
            Text(
                text,
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

data class ActivityInformation(
    var imageLinkInformation: String? = null,
    var locationNameInformation: String? = null,
    var timeInformation: String? = null,
    var dateInformation: String? = null,
    var lngInformation: String? = null,
    var latInformation: String? = null,
)

sealed class DataState {
    class Success(val data: MutableList<ActivityInformation>) : DataState()
    class Failed(val message: String) : DataState()
    object Loading : DataState()
    object Empty : DataState()
}
