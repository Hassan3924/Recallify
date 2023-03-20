package com.example.recallify.view.ui.feature.application.dailydiary.conversationSummary

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recallify.databinding.ActivitySummarizeConversationBinding
import java.util.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
//
class SummarizeConversation : AppCompatActivity() {

    lateinit var speechRecognizer : SpeechRecognizer

    lateinit var mainBinding : ActivitySummarizeConversationBinding

    lateinit var speechIntent : Intent

    var conversationText=""

var response_saved = ""
    val database = FirebaseDatabase.getInstance()

    val auth = FirebaseAuth.getInstance()

    val user = auth.currentUser // in current user, u can reach info such as email and UID of user who logs into app using the user object

    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    @RequiresApi(Build.VERSION_CODES.O)
    val timeFormatted = current.format(timeFormatter)

    @RequiresApi(Build.VERSION_CODES.O)
    val currentTime = timeFormatted.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    val formatted = current.format(formatter)

    @RequiresApi(Build.VERSION_CODES.O)
    var currentDate:String = formatted.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mainBinding = ActivitySummarizeConversationBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        mainBinding.buttonStart.setOnClickListener {

            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO),1)

            } else {
                convertSpeech()
            }

        }


        //when the user clicks buttonStop, the mic. will close manually.
        mainBinding.buttonStop.setOnClickListener {

            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.destroy()
            mainBinding.textView.text = "Please tap on the button to speak"
          //  mainBinding.SummarizedConversation.text = conversationtText
            callFlaskService()

        }
        mainBinding.buttonDone.setOnClickListener {
         var  person_name= mainBinding.EnterName.text.toString().trim()

            if(person_name == ""){
                Toast.makeText(this,"Please enter the name of the person who you talked to",Toast.LENGTH_SHORT).show()
            }
            else {
                SendSummary(response_saved, person_name)
                Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_SHORT).show()
                Log.d("ResponseSummary", response_saved)
                mainBinding.textViewResult.text = ""
                mainBinding.EnterName.text.clear()
            }
        }

    }

    @Composable
    fun SpeakButton() {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Pleasee press me")
        }
    }
    fun recognitionListenerFunctions(){

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                mainBinding.textView.text = "Listening..."
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                mainBinding.textView.text = "Please tap to button to speak"
            }

            override fun onError(error: Int) {

                when (error) {

                    SpeechRecognizer.ERROR_NO_MATCH -> { // if there is no speak, this error code is throw and we can handle it
                        val handler = Handler(Looper.getMainLooper())
                        mainBinding.textView.text = "Restarting the microphone, please wait..."

                        //after 2 seconds, the microphone is ready to speak again
                        handler.postDelayed(object : Runnable{
                            override fun run() {
                                convertSpeech()
                            }
                        },2000)

                    }
                    //if there is another error, the microphone will close
                    else -> {
                        speechRecognizer.stopListening()
                        speechRecognizer.cancel()
                        speechRecognizer.destroy()
                        mainBinding.textView.text = "Please tap to button to speak"
                    }
                }
            }

            override fun onResults(results: Bundle?) {

                val data: ArrayList<String> = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>

                val myText = mainBinding.textViewResult.text.toString()
  //              val myText = "" //issue with this is it resets
                mainBinding.textViewResult.text = myText.plus(" .").plus(data[0])
                conversationText = myText.plus(" .").plus(data[0])
             //   mainBinding.textViewResult.text =conversationtText
//                conversationText="""
            //Dear UOWD Student, Hope this e-mail finds you well.
            //Please note of the following information regarding Autumn 2022 Results Releasing Date and Supplementary Exams.Results
            //As published in the academic calendar, the results for Autumn 2022 will be released on Thursday, 12th of January 2023.
            //Kindly refer to the Finalisation of Student Results Policy for grade reference, should you need clarification regarding your published grade(s). If in the unlikely event that your marks/grades will not be available on the 12th of January, you will receive notification from the FRED and Registry Services team with information on the expected new release date of your results.
            //To clarify, please see below what constitutes the passing or failing of a subject:
            //If you receive a mark of 50 or above, you will be classed as having passed the subject.
            //If you receive a mark of below 50, you will be classed as having failed the subject as you have not provided sufficient evidence of attainment of the relevant subject learning outcomes.
            //For students who fail the subject, it should be noted that students are not automatically given a supplementary assessment. For further information and clarification on this, please refer to section 11.1 of the Examination Procedure document in MyUOWD.
            //Supplementary Examinations
            //All approved supplementary exams for Autumn 2022 subjects will be held on campus between 27 Mar and 2 Apr '23.
            //""".trimIndent()


                //after the result, the microphone will not close, it will start again
                convertSpeech()
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}

        })
    }

    fun convertSpeech(){

        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        recognitionListenerFunctions()
        speechRecognizer.startListening(speechIntent)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            convertSpeech()
        }
    }
    private fun callFlaskService() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
//        val url = "http://yourflaskserver.com/summarize"
        val url = "https://ridzbmd.pythonanywhere.com/summarize"

        // Request a response from the provided URL.
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                // Display the response string in a TextView or handle it as required
//                summaryTextView.text = response
             //   mainBinding.SummarizedConversation.text  = response //here response is the summarized convo and we are displaying it

//               mainBinding.summaryTextView.text = response
//                Log.d("ResponseSummary2",response)

                response_saved=response
                //mainBinding.buttonDone.setOnClickListener {
                //SendSummary(response)
                //Toast.makeText(this,"Saved To Database",Toast.LENGTH_SHORT).show()
                //
                //                }
            },
            Response.ErrorListener { error ->
                // Handle error response
                Log.e("Volley Error", error.toString())
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            // Add parameters to the POST request
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
//                params["text"] = inputTextView.text.toString() // Replace with your input text
                params["text"] = conversationText // Replace with your input text
                return params
            }
        }

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun SendSummary(response:String, person_name:String){
        val uid = Firebase.auth.currentUser?.uid
        var dataRef = database.reference
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child(uid).child("conversation-summary").child(currentDate)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                   var childCount = snapshot.childrenCount.toInt()
//                    if(childCount==0){
//                        childCount=childCount+1
//                    }
                    childCount=childCount + 1

                 //   var name_summary = "Today on $currentDate at $currentTime  I talked to " +mainBinding.EnterName.text.toString() + " and here is the summarized conversation" + response
                    var name_summary = "Today on $currentDate at $currentTime  I talked to $person_name and here is the summarized conversation" + response
                    dataRef.child("users").child(uid).child("conversation-summary").child(currentDate).child(childCount.toString()).setValue(name_summary)
                    //mainBinding.textViewResult.text = ""

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
    }
}