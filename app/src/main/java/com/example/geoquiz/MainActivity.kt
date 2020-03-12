package com.example.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

private const val Tag = "MainActivity"
private const val QUIZ = "Page"
private const val Key = "value"
private const val REQUEST_CODE_CHEAT = 0
private const val answer_checked = "answer"
private const val is_cheater = "is_cheater"

class MainActivity : AppCompatActivity() {

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    private lateinit var trueButton : Button
    private lateinit var falseButton : Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton : ImageButton
    private lateinit var prevButton : ImageButton
    private lateinit var questionTextView: TextView
    private var bg_Image : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quizViewModel.isCheater = savedInstanceState?.getBoolean(is_cheater, false)?:false
/*
        quizViewModel.correct = intent.getIntExtra("curr", 0)
        quizViewModel.ansQues = intent.getIntExtra("ans", 1)
*/

        textScore.text = "" + quizViewModel.correct + "/" + quizViewModel.ansQues

        if(savedInstanceState!=null) {
            quizViewModel.currentIndex = savedInstanceState.getInt(QUIZ, 0)
            quizViewModel.value = savedInstanceState.getIntArray(Key)!!
            quizViewModel.isCheater = savedInstanceState.getBoolean(is_cheater, quizViewModel.isCheater)
            quizViewModel.ansQues = savedInstanceState.getInt("ques", quizViewModel.ansQues)
            quizViewModel.correct = savedInstanceState.getInt("correct", quizViewModel.correct)
        }

        bg_Image = findViewById(R.id.bgImage)
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        cheatButton = findViewById(R.id.cheat_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView = findViewById(R.id.question_text_view)

        trueButton.setOnClickListener { view : View ->
            checkAnswer(true)
            trueButton.isEnabled = false
            falseButton.isEnabled = false
            update()
            quizViewModel.value += quizViewModel.currentIndex
        }

        falseButton.setOnClickListener { view : View ->
            checkAnswer(false)
            trueButton.isEnabled = false
            falseButton.isEnabled = false
            update()
            quizViewModel.value += quizViewModel.currentIndex
        }

        cheatButton.setOnClickListener{
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            /*intent.putExtra("curr", quizViewModel.currentIndex.toInt())
            intent.putExtra("ans", quizViewModel.correct.toInt())*/
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }

        questionTextView.setOnClickListener{ view : View ->
            quizViewModel.moveToNext()
            trueButton.isEnabled = true
            falseButton.isEnabled = true
            quizViewModel.isCheater=false
            quizViewModel.ansQues = quizViewModel.currentIndex + 1
            update()
            if(quizViewModel.correct + quizViewModel.incorrect == quizViewModel.questionBank.size){
                questionTextView.isEnabled = false
                val intent = Intent(this, ScoreActivity::class.java)
                intent.putExtra("correct", quizViewModel.correct.toString().toInt())
                startActivity(intent)
            }
            update()
            changeImages()
            updateQuestion()
        }

        nextButton.setOnClickListener { view : View ->
            quizViewModel.moveToNext()
            trueButton.isEnabled = true
            falseButton.isEnabled = true
            quizViewModel.isCheater=false
            quizViewModel.ansQues = quizViewModel.currentIndex + 1
            update()
            if(quizViewModel.correct + quizViewModel.incorrect == quizViewModel.questionBank.size){
                nextButton.isEnabled = false
                val intent = Intent(this, ScoreActivity::class.java)
                intent.putExtra("correct", quizViewModel.correct.toString().toInt())
                startActivity(intent)
            }
            update()
            changeImages()
            updateQuestion()
        }

        prevButton.setOnClickListener { view : View ->
            quizViewModel.currentIndex = (quizViewModel.currentIndex + quizViewModel.questionBank.size - 1) % quizViewModel.questionBank.size
            quizViewModel.ansQues = quizViewModel.currentIndex + 1
            trueButton.isEnabled = true
            falseButton.isEnabled = true
            update()
            changeImages()
            updateQuestion()
        }
        updateQuestion()
        changeImages()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
            savedInstanceState.putInt(QUIZ,quizViewModel.currentIndex)
            savedInstanceState.putIntArray(Key, quizViewModel.value)
            savedInstanceState.putBoolean(is_cheater, quizViewModel.isCheater)
            savedInstanceState.putInt("ques", quizViewModel.ansQues)
            savedInstanceState.putInt("correct", quizViewModel.correct)
    }

    private fun changeImages(){
        val drawableResource = when (quizViewModel.currentIndex) {
            1 -> R.drawable.bg1
            2 -> R.drawable.bg2
            3 -> R.drawable.bg3
            4 -> R.drawable.bg4
            5 -> R.drawable.bg5
            else -> R.drawable.bg6
        }
        bg_Image?.setImageResource(drawableResource)
    }

    private fun updateQuestion(){
        val questionTextResId = quizViewModel.questionBank[quizViewModel.currentIndex].testResId
        questionTextView.setText(questionTextResId)
        preventRepeatingTheAnswers()
    }

    private fun checkAnswer(userAnswer: Boolean){
        val correctAnswer = quizViewModel.questionBank[quizViewModel.currentIndex].answer
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        if(userAnswer == correctAnswer){
            quizViewModel.correct++
        }else{
            quizViewModel.incorrect++
        }
        gradeMyQuiz()
    }

    private fun gradeMyQuiz(){
        var score = ((quizViewModel.correct.toDouble() / quizViewModel.questionBank.size) * 100).toString()
        if((quizViewModel.correct + quizViewModel.incorrect) == quizViewModel.questionBank.size){
            Toast.makeText(this, "Correct Answers: " + quizViewModel.correct.toString() + "\nPercentage: " + score.toString() + "%", Toast.LENGTH_LONG).show()
        }
    }

    private fun preventRepeatingTheAnswers(){
        if(quizViewModel.value.contains(quizViewModel.currentIndex)){
            falseButton.isEnabled = false
            trueButton.isEnabled = false
        }else{
            falseButton.isEnabled = true
            trueButton.isEnabled = true
        }
    }

    private fun update(){
        textScore.text = "" + quizViewModel.correct + "/" + quizViewModel.ansQues
    }
}