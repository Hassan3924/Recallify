package com.example.recallify.view.ui.feature.application.dailydiary.conversationSummary

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.recallify.R
import com.example.recallify.databinding.ActivityConfirmScreenSummarizeConversationBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RetryPolicy
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.DailyDiaryActivity
import java.util.HashMap

lateinit var mainBinding : ActivityConfirmScreenSummarizeConversationBinding
//var conversationText=""

var responseSaved = ""

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
val formatted: String = current.format(formatter)

@RequiresApi(Build.VERSION_CODES.O)
var currentDate:String = formatted.toString()
class ConfirmScreenSummarizeConversation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityConfirmScreenSummarizeConversationBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        val loadingAnimation = LayoutInflater.from(this).inflate(R.layout.loading_animation, mainBinding.linearLayout1, false)
        mainBinding.linearLayout1.addView(loadingAnimation)
        loadingAnimation.visibility = View.VISIBLE

        var person_name = intent.getStringExtra("person_name")
        var conversationText = intent.getStringExtra("conversationText")
        Log.d("trackConversation2",conversationText.toString())
//mainBinding.buttonDone.setOnClickListener {
//    callFlaskService(conversationText.toString())
//    Toast.makeText(this@ConfirmScreenSummarizeConversation, "Please wait for the conversation to be summarized!", Toast.LENGTH_SHORT).show()
//                val handler = Handler(Looper.getMainLooper())
//                //after 40 seconds,
//                handler.postDelayed(object : Runnable{
//                    @RequiresApi(Build.VERSION_CODES.O)
//                    override fun run() {
//                        SendSummary(responseSaved, person_name.toString())
//                        Toast.makeText(this@ConfirmScreenSummarizeConversation, "Saved Successfully!", Toast.LENGTH_SHORT).show()
//                        Log.d("ResponseSummary", responseSaved)
//
//                        val intent = Intent(this@ConfirmScreenSummarizeConversation, DailyDiaryActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                },40000)
//}

        callFlaskService(conversationText.toString())
        Toast.makeText(this@ConfirmScreenSummarizeConversation, "Please wait for the conversation to be summarized!", Toast.LENGTH_SHORT).show()
        val handler = Handler(Looper.getMainLooper())
        //after 40 seconds,
                handler.postDelayed(object : Runnable{
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun run() {
                        SendSummary(responseSaved, person_name.toString())
                        Toast.makeText(this@ConfirmScreenSummarizeConversation, "Saved Successfully! \uD83C\uDF8A", Toast.LENGTH_SHORT).show()
                        Log.d("ResponseSummary", responseSaved)

                        val intent = Intent(this@ConfirmScreenSummarizeConversation, DailyDiaryActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                },40000)
    }
    private fun callFlaskService(conversationText:String) {

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
//        val url = "http://yourflaskserver.com/summarize"
        //  val url = "https://ridzbmd.pythonanywhere.com/summarize"
        val url = "https://RidinBMD.pythonanywhere.com/summarize"

        // Request a response from the provided URL.
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                // Display the response string in a TextView or handle it as required
//                summaryTextView.text = response
                //   mainBinding.SummarizedConversation.text  = response //here response is the summarized convo and we are displaying it

//               mainBinding.summaryTextView.text = response
//                Log.d("ResponseSummary2",response)

                responseSaved = response
                Log.d("Volley ErrorResponse", responseSaved)

                //close the connection here
                queue.stop()

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

                //close the connection here
                queue.stop()
            }
        ) {
            // Add parameters to the POST request
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
//                params["text"] = inputTextView.text.toString() // Replace with your input text
                params["text"] = conversationText // Replace with your input text
                return params
            }

//            // Set timeout value for the request
//            override fun getRetryPolicy(): RetryPolicy {
//                return DefaultRetryPolicy(
//                    15000, //15000 before
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                )
//            }


        }
        // Set the retry policy to handle timeout error
        stringRequest.retryPolicy = DefaultRetryPolicy(
            15000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun SendSummary(response:String, person_name:String){
        val uid = Firebase.auth.currentUser?.uid
        val dataRef = database.reference
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child(uid).child("conversation-summary").child(currentDate)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    var childCount = snapshot.childrenCount.toInt()
//                    if(childCount==0){
//                        childCount=childCount+1
//                    }
                    childCount += 1

                    //   var name_summary = "Today on $currentDate at $currentTime  I talked to " +mainBinding.EnterName.text.toString() + " and here is the summarized conversation" + response
                    var name_summary =
                        "Today on $currentDate at $currentTime  I talked to $person_name and here is the summarized conversation: $response"
                    dataRef.child("users").child(uid).child("conversation-summary").child(currentDate).child(childCount.toString()).setValue(name_summary)
                    //mainBinding.textViewResult.text = ""

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
    }
}