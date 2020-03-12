package com.example.geoquiz

import android.app.Activity
import android.app.assist.AssistContent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_cheat.*

const val EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE =
    "com.bignerdranch.android.geoquiz.answer_is_true"

private const val answer = "answer"
private var answerClicked = false
private var isCheater = false
private const val is_Cheater = "isCheater"
private var answerText = 2

class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button

    private var answerIsTrue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        if(savedInstanceState!=null) {
            answerTextView.text=savedInstanceState.getString("answer")
        }

        showAnswerButton.setOnClickListener {
                answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            setAnswerShownResult()
            answerClicked = true
        }

        if(answerClicked){
            setAnswerShownResult()
        }

    }

    private fun setAnswerShownResult() {
        isCheater = true
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, true)
        }
        setResult(Activity.RESULT_OK, data)
    }

    fun onClose(view : View){
        finish()
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("answer", answer_text_view.text.toString());
    }
}