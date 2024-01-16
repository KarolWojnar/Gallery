package com.example.gallery

// SetPinActivity.kt
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SetPinActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var pinEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_pin)

        pinEditText = findViewById(R.id.pinEditText)
        val acceptButton = findViewById<Button>(R.id.acceptButton)

        sharedPreferences = getSharedPreferences("PinPreferences", Context.MODE_PRIVATE)

        acceptButton.setOnClickListener {
            val pin = pinEditText.text.toString()

            if (pin.length == 4) {
                savePin(pin)
                showMessage("PIN został ustawiony")
                //checkSavedPin()
                startActivity(Intent(this, SecurityActivity::class.java))
            } else {
                showMessage("Wprowadź 4 cyfry PINu")
            }
        }

        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            // Przypisanie powrotu do SecurityActivity po naciśnięciu Anuluj
            finish()
        }

    }

    private fun checkSavedPin() {
        val savedPin = sharedPreferences.getString("PIN", null)

        if (savedPin != null) {
            // PIN został zapisany, możesz go wyświetlić
            showMessage("Pin: $savedPin")
        } else {
            Log.d("SetPinActivity", "Brak zapisanego PINu")
        }
    }

    private fun savePin(pin: String) {
        val editor = sharedPreferences.edit()
        editor.putString("PIN", pin)
        editor.apply()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
