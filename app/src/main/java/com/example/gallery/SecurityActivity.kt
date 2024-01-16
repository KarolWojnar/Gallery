package com.example.gallery

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class SecurityActivity : AppCompatActivity() {
    private val KEYGUARD_REQUEST_CODE = 1234
    private lateinit var keyguardManager: KeyguardManager
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)

        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val fingerprintButton = findViewById<Button>(R.id.fingerPrint_btn)
        fingerprintButton.setOnClickListener {
            showBiometricPrompt()
        }

        val passwordButton = findViewById<Button>(R.id.password_btn)
        passwordButton.setOnClickListener {
            authenticateWithPassword()
        }
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Unlock using your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(
            this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showMessage("Authentication error: $errString")
                    vibrateDevice()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showMessage("Authentication Succeed")
                    startActivity(Intent(this@SecurityActivity, PrivateActivity::class.java))
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showMessage("Authentication failed.")
                    vibrateDevice()
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    private fun authenticateWithPassword() {
        if (keyguardManager.isKeyguardSecure) {
            showSecureLockScreenSettings()
        } else {
            showMessage("Device does not have a secure lock screen.")
        }
    }

    private fun showSecureLockScreenSettings() {
        val intent = keyguardManager.createConfirmDeviceCredentialIntent(
            "Unlock using your phone's password",
            "Confirm your password"
        )
        startActivityForResult(intent, KEYGUARD_REQUEST_CODE)
    }

    private fun vibrateDevice() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // Deprecated in API 26
            vibrator.vibrate(1000)
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == KEYGUARD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                showMessage("Authentication with password succeeded!")
                startActivity(Intent(this, PrivateActivity::class.java))
            } else {
                showMessage("Authentication with password failed.")
            }
        }
    }
}