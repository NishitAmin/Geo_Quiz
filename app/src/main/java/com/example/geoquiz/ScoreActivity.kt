package com.example.geoquiz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_score.*
import kotlin.system.exitProcess

class ScoreActivity : AppCompatActivity() {

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        val pref = getPreferences(Context.MODE_PRIVATE)
        val editor = pref.edit()
        var highscore = pref.getInt("SCORE", 0);

        var correct =intent.getIntExtra("correct", 0)

        if (correct > highscore) {
            editor.putInt("SCORE", correct)
            editor.commit()
            textScore.setText("Highest Score: " + correct.toString())
        }else{
            textScore.setText("Highest Score: " + highscore.toString())
        }

        urScore.setText("Your Score: " + correct + "/" + quizViewModel.questionBank.size)
    }

    fun tryAgain(view: View) {
        /*val pref = getPreferences(Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("SCORE", quizViewModel.correct.toString().toInt())
        editor.commit()

        finish()
        */val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        recreate()
    }

    fun exit(view: View) {
        finish()
    }
}
