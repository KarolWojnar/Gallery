package com.example.gallery

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class EnterPinActivity : AppCompatActivity() {

    private lateinit var enterPinEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_pin)
        enterPinEditText = findViewById(R.id.enterPinEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val resetButton = findViewById<Button>(R.id.resetButton)

        sharedPreferences = getSharedPreferences("PinPreferences", Context.MODE_PRIVATE)

        loginButton.setOnClickListener {
            val enteredPin = enterPinEditText.text.toString()
            val savedPin = sharedPreferences.getString("PIN", null)

            if (enteredPin == savedPin) {
                showMessage("Logowanie udane!")
                startActivity(Intent(this, PrivateActivity::class.java))
            } else {
                showMessage("Nieprawidłowy PIN")
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }

        resetButton.setOnClickListener {
            startActivity(Intent(this, ChangePinActivity::class.java))
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}