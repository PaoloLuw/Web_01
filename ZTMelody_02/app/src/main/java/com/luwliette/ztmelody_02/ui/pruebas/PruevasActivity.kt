package com.luwliette.ztmelody_02.ui.pruebas

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.luwliette.ztmelody_02.R

class PruevasActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 100
    private val TAG = "PruevasActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pruevas)

        textView = findViewById(R.id.textHello) // Asegúrate de que el ID coincida con tu layout

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                // Tu ID de cliente del servidor, no el ID de cliente de Android.
                .setServerClientId("37432480717-09l4g03qda3lhcdkavdr5tec3g296lp1.apps.googleusercontent.com")
                // Mostrar solo cuentas previamente usadas para iniciar sesión.
                .setFilterByAuthorizedAccounts(false)
                .build())
            // Iniciar sesión automáticamente cuando se recupere exactamente una credencial.
            .setAutoSelectEnabled(true)
            .build()
    }

    fun buttonGoogleSignIn(view: View) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this, OnSuccessListener { result: BeginSignInResult ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "No se pudo iniciar la interfaz de One Tap: " + e.localizedMessage)
                }
            })
            .addOnFailureListener(this, OnFailureListener { e: Exception ->
                // No se encontraron credenciales guardadas. Iniciar el flujo de registro de One Tap,
                // o no hacer nada y continuar presentando la interfaz de usuario desconectada.
                Log.d(TAG, e.localizedMessage)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_ONE_TAP) {
            try {
                val credential: SignInCredential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                val username = credential.id
                val password = credential.password
                textView.text = "Autenticación realizada.\nEl nombre de usuario es $username"
                if (idToken != null) {
                    // Obtuvimos un ID token de Google. Utilízalo para autenticar
                    // con tu backend.
                    Log.d(TAG, "ID token obtenido.")
                } else if (password != null) {
                    // Obtuvimos un nombre de usuario y contraseña guardados. Utilízalos para autenticar
                    // con tu backend.
                    Log.d(TAG, "Contraseña obtenida.")
                }
            } catch (e: ApiException) {
                textView.text = e.toString()
            }
        }
    }
}
