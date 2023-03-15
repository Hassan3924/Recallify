package com.example.recallify.view.ui.feature.application.dailydiary.daily_log

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.contract.ActivityResultContract
import java.util.*
import kotlin.collections.ArrayList

class SpeechRecognitionContract : ActivityResultContract<Unit, ArrayList<String>?>() {

    val pauseRecordingTime = 5000L

    override fun createIntent(context: Context, input: Unit): Intent {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Speak Something"
        )

        return intent
    }

    // let's handle the result before that lets make result nullable
    override fun parseResult(resultCode: Int, intent: Intent?): java.util.ArrayList<String>? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }
        return intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
    }
}
