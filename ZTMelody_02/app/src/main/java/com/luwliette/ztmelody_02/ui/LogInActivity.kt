package com.luwliette.ztmelody_02.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luwliette.ztmelody_02.MainActivity
import com.luwliette.ztmelody_02.R

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usernameField = findViewById<EditText>(R.id.text_1)
        val passwordField = findViewById<EditText>(R.id.text_2)
        val button = findViewById<AppCompatButton>(R.id.btnContinue)

//        // Add TextWatcher to username field
//        usernameField.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (s.isNullOrEmpty()) {
//                    usernameField.setBackgroundColor(Color.TRANSPARENT)
//                } else {
//                    usernameField.setBackgroundResource(android.R.drawable.edit_text)
//                }
//            }
//            override fun afterTextChanged(s: Editable?) {}
//        })
//
//        // Add TextWatcher to password field
//        passwordField.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (s.isNullOrEmpty()) {
//                    passwordField.setBackgroundColor(Color.TRANSPARENT)
//                } else {
//                    passwordField.setBackgroundResource(android.R.drawable.edit_text)
//                }
//            }
//            override fun afterTextChanged(s: Editable?) {}
//        })

        button.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            if (username == "Heros" && password == "mylover") {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
