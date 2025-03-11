package com.zybooks.diceproject

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var numDice = 2
    private lateinit var diceList: MutableList<Dice>
    private lateinit var diceImageViewList: MutableList<ImageView>
    private var timer: CountDownTimer? = null
    private var timerLength = 2000L
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    private var branch = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        diceList = mutableListOf()
        for(i in 0 until numDice){
            diceList.add(Dice(i+1))
        }

        diceImageViewList = mutableListOf(findViewById(R.id.dice1),findViewById(R.id.dice2))

        progressBar = findViewById(R.id.branch_bar)
        progressText = findViewById(R.id.progressText)
    }

    fun onHelpClick(view: View){
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }

    fun onNewGameClick(view: View){
        progressBar.progress = 0
        progressText.text = "Branch 0"
    }

    fun onRollClick(view: View){
//            optionsMenu.findItem(R.id.action_stop).isVisible = true
            timer?.cancel()

            var sum = 0
            // Start a timer that periodically changes each visible dice
            timer = object : CountDownTimer(timerLength, 100) {
                override fun onTick(millisUntilFinished: Long) {
                    for (i in 0 until numDice) {
                        diceList[i].roll()
                    }
                    showDice()
                }

                override fun onFinish() {
//                    optionsMenu.findItem(R.id.action_stop).isVisible = false

                    for(i in 0 until numDice){
                        sum += diceList[i].number
                    }
                }
            }.start()

            // Do the checks on whether a special condition was triggered
            if(diceList[0].number == diceList[1].number){
                Toast.makeText(this, R.string.Doubles, Toast.LENGTH_LONG).show()
                if(branch < 4){
                    branch = 0;
                } else {
                    branch-=4
                }
                progressBar.progress = range(branch)
                progressText.text = "Branch " + branch
            } else if(sum == 4){
                Toast.makeText(this, R.string.Wind, Toast.LENGTH_LONG).show()
            } else if(branch + sum == 20 || branch + sum == 10){
                Toast.makeText(this, R.string.Bees, Toast.LENGTH_LONG).show()
                branch += sum
                progressBar.progress = range(branch)
                progressText.text = "Branch " + branch
            } else if (branch + sum >= 30){
                Toast.makeText(this, R.string.Winning, Toast.LENGTH_LONG).show()
                progressBar.progress = 100
                progressText.text = "Branch 30!"
            } else {
                branch += sum
                progressBar.progress = range(branch)
                progressText.text = "Branch " + branch
            }
        }

    fun showDice() {
        for(i in 0 until numDice){
            val diceDrawable = ContextCompat.getDrawable(this, diceList[i].imageId)
            diceImageViewList[i].setImageDrawable(diceDrawable)
            diceImageViewList[i].contentDescription = diceList[i].imageId.toString()
        }
    }

    fun range(inp: Int): Int {
        var out = 100 * inp
        out /= 30
        return out
    }
}