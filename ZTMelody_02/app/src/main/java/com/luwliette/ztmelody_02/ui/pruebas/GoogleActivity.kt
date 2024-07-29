package com.luwliette.ztmelody_02.ui.pruebas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.luwliette.ztmelody_02.R
import com.luwliette.ztmelody_02.databinding.ActivityGoogleBinding

class GoogleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoogleBinding
    private var gso: GoogleSignInOptions? = null
    private var gsc: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        enableEdgeToEdge()

        // Inicializa el binding
        binding = ActivityGoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajuste de los insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa Google Sign-In y los componentes de la UI
        initGoogle()
        initUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1002) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    binding.tvUserName.text = account.displayName
                    binding.tvMail.text = account.email
                    Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
                    Log.d("GoogleActivitySSS", "Sign in successful: ${account.displayName}")
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                Toast.makeText(this, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("GoogleActivitySSS", "Sign in failed: ${e.message}, statusCode: ${e.statusCode}")
            }
        } else {
            Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show()
            Log.d("GoogleActivitySSS", "Sign in canceled, resultCode: $resultCode")
        }
    }

    private fun initGoogle() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso!!)
    }

    private fun initUI() {
        binding.btnSignIn.setOnClickListener {
            Log.d("GoogleActivitySSS", "SignIn button clicked")
            signIn()
        }
        binding.btnLogOut.setOnClickListener {
            Log.d("GoogleActivitySSS", "LogOut button clicked")
            signOut()
        }
    }

    private fun signOut() {
        gsc?.signOut()?.addOnCompleteListener {
            binding.tvMail.text = ""
            binding.tvUserName.text = ""
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            Log.d("GoogleActivitySSS", "Signed out successfully")
        }
    }

    private fun signIn() {
        val intent = gsc?.signInIntent
        if (intent != null) {
            startActivityForResult(intent, 1002)
        } else {
            Toast.makeText(this, "Sign in intent is null", Toast.LENGTH_SHORT).show()
            Log.d("GoogleActivitySSS", "Sign in intent is null")
        }
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
