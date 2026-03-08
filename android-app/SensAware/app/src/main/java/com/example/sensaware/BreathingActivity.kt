package com.example.sensaware

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class BreathingActivity : AppCompatActivity() {

    private lateinit var breatheLabel: TextView
    private lateinit var breathCount: TextView
    private lateinit var livePulse: TextView
    private lateinit var liveBreathing: TextView
    private lateinit var resultsOverlay: LinearLayout

    private val handler = Handler(Looper.getMainLooper())
    private var startPulse = 0
    private var startBreathing = 0
    private var currentPulse = 0.0
    private var currentBreathing = 0.0
    private var exerciseRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breathing)

        startPulse = intent.getIntExtra("startPulse", 95)
        startBreathing = intent.getIntExtra("startBreathing", 58)

        breatheLabel = findViewById(R.id.breatheLabel)
        breathCount = findViewById(R.id.breathCount)
        livePulse = findViewById(R.id.livePulse)
        liveBreathing = findViewById(R.id.liveBreathing)
        resultsOverlay = findViewById(R.id.resultsOverlay)

        currentPulse = startPulse.toDouble()
        currentBreathing = Random.nextInt(24, 27).toDouble()

        livePulse.text = startPulse.toString()
        liveBreathing.text = currentBreathing.toInt().toString()

        handler.postDelayed({ startExercise() }, 2000)
    }

    private fun startExercise() {
        exerciseRunning = true
        startMetricsUpdater()
        runCycle(1)
    }

    private fun runCycle(cycle: Int) {
        if (cycle > 2) {
            finishExercise()
            return
        }

        breathCount.text = "breath $cycle of 2"

        breatheLabel.text = "INHALE"
        breatheLabel.textSize = 38f
        breatheLabel.setTextColor(0xFF7AB648.toInt())

        handler.postDelayed({
            breatheLabel.text = "EXHALE"
            breatheLabel.setTextColor(0xFF5B8DB8.toInt())

            handler.postDelayed({
                runCycle(cycle + 1)
            }, 4000)
        }, 4000)
    }

    private fun startMetricsUpdater() {
        val targetPulse = startPulse - Random.nextInt(8, 14)
        val targetBreathing = Random.nextInt(21, 23)
        val totalSteps = 16
        val stepMs = 1000L

        val pulseDrop = (startPulse - targetPulse).toDouble() / totalSteps
        val breathingDrop = (startBreathing - targetBreathing).toDouble() / totalSteps

        var step = 0
        val updater = object : Runnable {
            override fun run() {
                if (!exerciseRunning || step >= totalSteps) return

                step++
                currentPulse -= pulseDrop + Random.nextDouble(-0.8, 0.8)
                currentBreathing -= breathingDrop + Random.nextDouble(-0.5, 0.5)

                currentPulse = currentPulse.coerceAtLeast(targetPulse.toDouble() - 2.0)
                currentBreathing = currentBreathing.coerceAtLeast(targetBreathing.toDouble() - 2.0)

                livePulse.text = currentPulse.toInt().toString()
                liveBreathing.text = currentBreathing.toInt().toString()

                handler.postDelayed(this, stepMs)
            }
        }
        handler.postDelayed(updater, stepMs)
    }

    private fun finishExercise() {
        exerciseRunning = false
        breatheLabel.text = "well done"
        breatheLabel.setTextColor(0xFF7AB648.toInt())
        breathCount.text = ""

        handler.postDelayed({
            showResults()
        }, 1500)
    }

    private fun showResults() {
        resultsOverlay.visibility = View.VISIBLE

        findViewById<TextView>(R.id.beforePulse).text = startPulse.toString()
        findViewById<TextView>(R.id.beforeBreathing).text = startBreathing.toString()
        findViewById<TextView>(R.id.afterPulse).text = currentPulse.toInt().toString()
        findViewById<TextView>(R.id.afterBreathing).text = currentBreathing.toInt().toString()

        findViewById<Button>(R.id.doneButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exerciseRunning = false
        handler.removeCallbacksAndMessages(null)
    }
}
