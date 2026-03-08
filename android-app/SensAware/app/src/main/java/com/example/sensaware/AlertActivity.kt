package com.example.sensaware

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlertActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)

        val pulse = intent.getIntExtra("pulse", 0)
        val breathing = intent.getIntExtra("breathing", 0)

        findViewById<TextView>(R.id.pulseValue).text = if (pulse > 0) "$pulse" else "--"
        findViewById<TextView>(R.id.breathingValue).text = if (breathing > 0) "$breathing" else "--"

        findViewById<Button>(R.id.breatheButton).setOnClickListener {
            val breatheIntent = Intent(this, BreathingActivity::class.java)
            breatheIntent.putExtra("startPulse", pulse)
            breatheIntent.putExtra("startBreathing", breathing)
            startActivity(breatheIntent)
            finish()
        }

        findViewById<Button>(R.id.returnHomeButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
