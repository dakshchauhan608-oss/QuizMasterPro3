package com.example.quizmasterpro

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private val navy = Color.rgb(20, 28, 58)
    private val purple = Color.rgb(103, 80, 220)
    private val pale = Color.rgb(245, 246, 255)
    private val ink = Color.rgb(35, 39, 65)
    private var category = "Android & Kotlin"
    private var current = 0
    private var score = 0
    private var secondsLeft = 15
    private var timer: CountDownTimer? = null
    private var locked = false
    private lateinit var root: LinearLayout
    private lateinit var timerText: TextView
    private lateinit var timerBar: ProgressBar
    private lateinit var questionText: TextView
    private lateinit var questionCount: TextView
    private lateinit var scoreText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var nextButton: Button
    private lateinit var feedbackText: TextView
    private lateinit var questions: List<Question>

    data class Question(val prompt: String, val options: List<String>, val answer: Int, val explanation: String)

    private val quizBank: Map<String, List<Question>> = mapOf(
        "Android & Kotlin" to listOf(
            Question("Which language is officially recommended for modern Android development?", listOf("Kotlin", "Ruby", "PHP", "Swift"), 0, "Kotlin is Google's recommended language for Android development."),
            Question("Which function is the usual entry point of an Android Activity?", listOf("start()", "onCreate()", "mainScreen()", "launch()"), 1, "Android calls onCreate() when the Activity is created."),
            Question("What does XML commonly define in an Android app?", listOf("Database passwords", "UI layouts", "CPU instructions", "Internet speed"), 1, "XML layout files describe views and their arrangement."),
            Question("Which keyword declares a read-only variable in Kotlin?", listOf("var", "let", "val", "constvar"), 2, "Use val for a reference that cannot be reassigned."),
            Question("Which file declares app activities and permissions?", listOf("AndroidManifest.xml", "settings.gradle", "README.md", "MainActivity.css"), 0, "The manifest declares application components and permissions.")
        ),
        "Science" to listOf(
            Question("What is the chemical symbol for water?", listOf("CO₂", "H₂O", "O₂", "NaCl"), 1, "A water molecule contains two hydrogen atoms and one oxygen atom."),
            Question("Which planet is known as the Red Planet?", listOf("Venus", "Jupiter", "Mars", "Mercury"), 2, "Iron minerals on Mars give its surface a reddish appearance."),
            Question("What is the SI unit of force?", listOf("Joule", "Watt", "Pascal", "Newton"), 3, "Force is measured in newtons."),
            Question("Which gas do plants absorb during photosynthesis?", listOf("Oxygen", "Carbon dioxide", "Nitrogen", "Hydrogen"), 1, "Plants use carbon dioxide, water and light to make sugars."),
            Question("What is the centre of an atom called?", listOf("Nucleus", "Cell", "Core shell", "Neutron field"), 0, "The nucleus contains protons and usually neutrons.")
        ),
        "General Knowledge" to listOf(
            Question("What is the capital of India?", listOf("Mumbai", "New Delhi", "Jaipur", "Kolkata"), 1, "New Delhi is the capital of India."),
            Question("How many colours are traditionally named in a rainbow?", listOf("Five", "Six", "Seven", "Eight"), 2, "The traditional list contains seven colours."),
            Question("Which is the largest ocean on Earth?", listOf("Atlantic", "Indian", "Arctic", "Pacific"), 3, "The Pacific Ocean is the largest and deepest ocean."),
            Question("Who wrote India's national anthem?", listOf("Rabindranath Tagore", "Bankim Chandra Chatterjee", "Sarojini Naidu", "Mahatma Gandhi"), 0, "Rabindranath Tagore wrote Jana Gana Mana."),
            Question("How many days are there in a leap year?", listOf("364", "365", "366", "367"), 2, "A leap year has 366 days.")
        ),
        "Programming" to listOf(
            Question("Which data structure follows FIFO?", listOf("Stack", "Queue", "Tree", "Graph"), 1, "A queue is first-in, first-out."),
            Question("What does HTML stand for?", listOf("HyperText Markup Language", "High Transfer Machine Link", "Home Tool Markup Language", "Hyperlink Text Mode Language"), 0, "HTML is the standard markup language for web pages."),
            Question("Which symbol commonly ends a statement in C?", listOf(":", ";", ".", "#"), 1, "C statements commonly end with a semicolon."),
            Question("Which loop checks its condition before running its body?", listOf("do-while", "while", "Both always run once", "Neither"), 1, "A while loop checks the condition before each iteration."),
            Question("What does CPU stand for?", listOf("Central Processing Unit", "Computer Personal Utility", "Core Program Upload", "Central Power User"), 0, "CPU stands for Central Processing Unit.")
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = navy
        window.navigationBarColor = navy
        showHome()
    }

    private fun baseScreen(): LinearLayout {
        val scroll = ScrollView(this).apply { setBackgroundColor(pale); isFillViewport = true }
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(26), dp(22), dp(24))
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(pale)
        }
        scroll.addView(root)
        setContentView(scroll)
        return root
    }

    private fun showHome() {
        timer?.cancel()
        baseScreen()
        addText("QUIZ MASTER", 14, purple, true, 8)
        addText("Challenge your\nknowledge.", 31, navy, true, 8)
        addText("Choose a category and see how many you can get right.", 16, Color.DKGRAY, false, 24)
        val categories = listOf(
            "Android & Kotlin" to "📱  Build your Android skills",
            "Science" to "🔬  Explore the world around you",
            "General Knowledge" to "🌍  Test what you know",
            "Programming" to "💻  Think like a developer"
        )
        categories.forEach { (name, subtitle) ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(18), dp(16), dp(18), dp(16))
                background = rounded(Color.WHITE, 18, Color.rgb(229, 230, 245), 1)
            }
            val title = TextView(this).apply { text = name; textSize = 18f; setTextColor(ink); typeface = Typeface.DEFAULT_BOLD }
            val desc = TextView(this).apply { text = subtitle; textSize = 13f; setTextColor(Color.DKGRAY); setPadding(0, dp(5), 0, 0) }
            card.addView(title); card.addView(desc)
            card.setOnClickListener { category = name; startQuiz() }
            root.addView(card, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })
        }
        addText("5 questions  •  15 seconds each  •  Instant feedback", 12, Color.GRAY, false, 4)
    }

    private fun startQuiz() {
        current = 0; score = 0
        questions = quizBank[category] ?: quizBank.getValue("General Knowledge")
        showQuestion()
    }

    private fun showQuestion() {
        timer?.cancel()
        locked = false
        baseScreen()
        val top = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val cat = TextView(this).apply { text = category.uppercase(); textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(purple) }
        questionCount = TextView(this).apply { text = "QUESTION ${current + 1} OF ${questions.size}"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.GRAY); gravity = Gravity.END }
        top.addView(cat, LinearLayout.LayoutParams(0, -2, 1f)); top.addView(questionCount)
        root.addView(top, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(18) })
        timerText = TextView(this).apply { text = "15s"; textSize = 14f; typeface = Typeface.DEFAULT_BOLD; setTextColor(purple); gravity = Gravity.END }
        root.addView(timerText, LinearLayout.LayoutParams(-1, -2))
        timerBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply { max = 15; progress = 15 }
        root.addView(timerBar, LinearLayout.LayoutParams(-1, dp(7)).apply { bottomMargin = dp(25) })
        questionText = TextView(this).apply {
            text = questions[current].prompt; textSize = 24f; typeface = Typeface.DEFAULT_BOLD; setTextColor(navy)
        }
        root.addView(questionText, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(22) })
        optionsGroup = RadioGroup(this).apply { orientation = RadioGroup.VERTICAL }
        questions[current].options.forEachIndexed { index, option ->
            val radio = RadioButton(this).apply {
                id = View.generateViewId(); text = option; textSize = 16f; setTextColor(ink)
                setPadding(dp(12), dp(12), dp(12), dp(12)); buttonTintList = android.content.res.ColorStateList.valueOf(purple)
                background = rounded(Color.WHITE, 14, Color.rgb(225, 227, 242), 1)
            }
            optionsGroup.addView(radio, RadioGroup.LayoutParams(-1, -2).apply { bottomMargin = dp(10) })
            radio.tag = index
        }
        root.addView(optionsGroup, LinearLayout.LayoutParams(-1, -2))
        feedbackText = TextView(this).apply { textSize = 14f; setTextColor(ink); setPadding(dp(2), dp(8), dp(2), dp(8)); visibility = View.GONE }
        root.addView(feedbackText)
        scoreText = TextView(this).apply { text = "Score: $score"; textSize = 13f; setTextColor(Color.GRAY) }
        root.addView(scoreText, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(8); bottomMargin = dp(12) })
        nextButton = makeButton(if (current == questions.lastIndex) "SEE RESULTS" else "CHECK ANSWER")
        nextButton.setOnClickListener {
            if (!locked) checkAnswer() else {
                current++
                if (current < questions.size) showQuestion() else showResults()
            }
        }
        root.addView(nextButton, LinearLayout.LayoutParams(-1, dp(54)))
        startTimer()
    }

    private fun startTimer() {
        secondsLeft = 15
        timer?.cancel()
        timer = object : CountDownTimer(15_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsLeft = ((millisUntilFinished + 999) / 1000).toInt()
                timerText.text = "${secondsLeft}s"
                timerBar.progress = secondsLeft
            }
            override fun onFinish() {
                timerText.text = "0s"; timerBar.progress = 0
                if (!locked) {
                    locked = true
                    feedbackText.visibility = View.VISIBLE
                    feedbackText.text = "Time's up! ${questions[current].explanation}"
                    nextButton.text = if (current == questions.lastIndex) "SEE RESULTS" else "NEXT QUESTION"
                    setOptionsEnabled(false)
                }
            }
        }.start()
    }

    private fun checkAnswer() {
        val checked = optionsGroup.checkedRadioButtonId
        if (checked == -1) {
            feedbackText.visibility = View.VISIBLE
            feedbackText.text = "Choose an option first."
            return
        }
        timer?.cancel()
        locked = true
        val selected = findViewById<RadioButton>(checked).tag as Int
        val q = questions[current]
        if (selected == q.answer) {
            score++
            feedbackText.text = "Correct! 🎉 ${q.explanation}"
        } else {
            feedbackText.text = "Not quite. Correct answer: ${q.options[q.answer]}. ${q.explanation}"
        }
        feedbackText.visibility = View.VISIBLE
        scoreText.text = "Score: $score"
        nextButton.text = if (current == questions.lastIndex) "SEE RESULTS" else "NEXT QUESTION"
        setOptionsEnabled(false)
    }

    private fun setOptionsEnabled(enabled: Boolean) {
        for (i in 0 until optionsGroup.childCount) optionsGroup.getChildAt(i).isEnabled = enabled
    }

    private fun showResults() {
        timer?.cancel()
        baseScreen()
        val percent = (score * 100) / questions.size
        addText("QUIZ COMPLETE", 13, purple, true, 18)
        addText(when { percent == 100 -> "Perfect score! 🏆"; percent >= 60 -> "Great work! 🎉"; else -> "Keep practising! 💪" }, 30, navy, true, 12)
        addText("$category", 17, Color.DKGRAY, false, 28)
        val scoreCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(dp(24), dp(28), dp(24), dp(28))
            background = rounded(Color.WHITE, 22, Color.rgb(226, 225, 250), 1)
        }
        scoreCard.addView(TextView(this).apply { text = "$score / ${questions.size}"; textSize = 44f; typeface = Typeface.DEFAULT_BOLD; setTextColor(purple); gravity = Gravity.CENTER })
        scoreCard.addView(TextView(this).apply { text = "$percent% score"; textSize = 17f; setTextColor(ink); gravity = Gravity.CENTER; setPadding(0, dp(8), 0, 0) })
        root.addView(scoreCard, LinearLayout.LayoutParams(-1, dp(170)).apply { bottomMargin = dp(22) })
        addText("Correct answers: $score\nIncorrect or unanswered: ${questions.size - score}", 16, ink, false, 28)
        val replay = makeButton("PLAY AGAIN")
        replay.setOnClickListener { startQuiz() }
        root.addView(replay, LinearLayout.LayoutParams(-1, dp(54)).apply { bottomMargin = dp(12) })
        val home = makeButton("CHOOSE ANOTHER CATEGORY", secondary = true)
        home.setOnClickListener { showHome() }
        root.addView(home, LinearLayout.LayoutParams(-1, dp(54)))
    }

    private fun addText(value: String, size: Int, color: Int, bold: Boolean, bottom: Int) {
        val tv = TextView(this).apply {
            text = value; textSize = size.toFloat(); setTextColor(color)
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }
        root.addView(tv, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(bottom) })
    }

    private fun makeButton(label: String, secondary: Boolean = false): Button = Button(this).apply {
        text = label; textSize = 14f; isAllCaps = false; typeface = Typeface.DEFAULT_BOLD
        setTextColor(if (secondary) purple else Color.WHITE)
        background = rounded(if (secondary) Color.WHITE else purple, 16, if (secondary) purple else purple, if (secondary) 1 else 0)
        stateListAnimator = null
    }

    private fun rounded(color: Int, radius: Int, strokeColor: Int, strokeWidth: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color); cornerRadius = dp(radius).toFloat()
        if (strokeWidth > 0) setStroke(dp(strokeWidth), strokeColor)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
