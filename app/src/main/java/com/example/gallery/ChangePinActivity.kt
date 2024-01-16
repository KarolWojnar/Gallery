package com.example.gallery

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class ChangePinActivity : AppCompatActivity() {

    private lateinit var currentPinEditText: EditText
    private lateinit var newPinEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pin)

        currentPinEditText = findViewById(R.id.currentPinEditText)
        newPinEditText = findViewById(R.id.newPinEditText)
        val acceptChangePinButton = findViewById<Button>(R.id.acceptChangePinButton)
        val cancelChangePinButton = findViewById<Button>(R.id.cancelChangePinButton)

        sharedPreferences = getSharedPreferences("PinPreferences", Context.MODE_PRIVATE)

        acceptChangePinButton.setOnClickListener {
            val enteredCurrentPin = currentPinEditText.text.toString()
            val enteredNewPin = newPinEditText.text.toString()

            val savedPin = sharedPreferences.getString("PIN", "")

            if (enteredCurrentPin == savedPin && enteredNewPin.length == 4) {
                // Zmiana PIN w SharedPreferences
                sharedPreferences.edit().putString("PIN", enteredNewPin).apply()
                showMessage("PIN changed successfully!")
                startActivity(Intent(this, SecurityActivity::class.java))
                // Tutaj możesz przenieść użytkownika do kolejnej aktywności lub ekranu
                // np. startActivity(Intent(this, NextActivity::class.java))
            } else {
                showMessage("Invalid current PIN or new PIN must be 4 digits long")
            }
        }

        cancelChangePinButton.setOnClickListener {
            finish() // Zamknij aktywność
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}