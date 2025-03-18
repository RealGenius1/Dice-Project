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
        // Initialize the view
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Set up the list of dice and their images
        diceList = mutableListOf()
        for(i in 0 until numDice){
            diceList.add(Dice(i+1))
        }

        diceImageViewList = mutableListOf(findViewById(R.id.dice1),findViewById(R.id.dice2))

        //Setting up the progress bars
        progressBar = findViewById(R.id.branch_bar)
        progressText = findViewById(R.id.progressText)
    }

    /**
     * Opens up the Help menu that contains instructions when button is clicked
     */
    fun onHelpClick(view: View){
        //Start Help Activity
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }

    /**
     * Resets the game when the button is clicked
     *
     * In this context, this just requires setting progress to 0
     */
    fun onNewGameClick(view: View){
        progressBar.progress = 0
        progressText.text = "Branch 0"
        branch = 0
    }

    /**
     * Rolls the dice using a timer for animated rolling
     *
     * Once the dice have been rolled it handles the game logic for moving Pooh up or down branches
     */
    fun onRollClick(view: View){
            //Timer so that I can create an animated dice roll
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
                    //Once done I need to get the sum for a condition check
                    for(i in 0 until numDice){
                        sum += diceList[i].number
                    }
                    // Do the checks on whether a special condition was triggered

                    //CASE 1: Doubles -> Drop 4 branches
                    if(diceList[0].number == diceList[1].number){
                        //NOTE: Some screens sizes have trouble with the toast messages. Purely Visual.
                        Toast.makeText(this@MainActivity, R.string.Doubles, Toast.LENGTH_LONG).show()
                        if(branch < 4){
                            branch = 0;
                        } else {
                            branch-=4
                        }
                        progressBar.progress = range(branch)
                        progressText.text = "Branch $branch"
                        //Check to see if we have fallen down to branches 10 or 20
                        if(branch == 10 || branch == 20){
                            Toast.makeText(this@MainActivity, R.string.Bees, Toast.LENGTH_LONG).show()
                        }
                    }
                    //CASE 2: Roll a total of 4 -> Roll is invalidated
                    else if(sum == 4){
                        Toast.makeText(this@MainActivity, R.string.Wind, Toast.LENGTH_LONG).show()
                    }
                    //CASE 3: If Pooh lands on Branch 10 or 20, he loses a turn (Doesn't do anything for 1 person game)
                    else if(branch + sum == 20 || branch + sum == 10){
                        Toast.makeText(this@MainActivity, R.string.Bees, Toast.LENGTH_LONG).show()
                        branch += sum
                        progressBar.progress = range(branch)
                        progressText.text = "Branch $branch"
                    }
                    //CASE 4: If Pooh reaches branch 30+ he wins
                    else if (branch + sum >= 30){
                        Toast.makeText(this@MainActivity, R.string.Winning, Toast.LENGTH_LONG).show()
                        progressBar.progress = 100
                        progressText.text = "Branch 30!"
                    }
                    //If no special effects are triggered than just run as normal
                    else {
                        branch += sum
                        progressBar.progress = range(branch)
                        progressText.text = "Branch $branch"
                    }
                }
            }.start()


        }

    /**
     * Function that iterates through the dice images on screen
     * and makes them show the proper number
     */
    fun showDice() {
        for(i in 0 until numDice){
            val diceDrawable = ContextCompat.getDrawable(this, diceList[i].imageId)
            diceImageViewList[i].setImageDrawable(diceDrawable)
            diceImageViewList[i].contentDescription = diceList[i].imageId.toString()
        }
    }

    /**
    * Function that converts the 0-30 range of branches to a 0-100 range for the progress bar
    *
    * @param inp The branch number pooh is currently on
    * @return The associated progress that this branch correlates to
     */
    fun range(inp: Int): Int {
        var out = 100 * inp
        out /= 30
        return out
    }
}