package com.romellbolton.putmeon.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.romellbolton.putmeon.R
import com.romellbolton.putmeon.util.AppStatus.Companion.getInstance
import com.romellbolton.putmeon.util.SessionManager
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse

class LoginActivity : AppCompatActivity() {
    private var mContext: Context? = null
    private val mLoginActivity = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mContext = applicationContext
        val loginButton: Button
        loginButton = findViewById(R.id.action_login)
        loginButton.setOnClickListener {
            if (getInstance(applicationContext).isOnline) {
                val builder = AuthenticationRequest.Builder(CLIENT_ID,
                        AuthenticationResponse.Type.TOKEN,
                        REDIRECT_URI)
                builder.setScopes(arrayOf("streaming", "user-read-recently-played", "playlist-modify-public"))
                val request = builder.build()
                AuthenticationClient.openLoginActivity(mLoginActivity, REQUEST_CODE, request)
            } else {
                Toast.makeText(this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            if (response.type == AuthenticationResponse.Type.TOKEN) {
                val sessionManager = SessionManager()
                sessionManager.createLoginSession(mContext!!, response.accessToken)
                val intentLogin = Intent(this, TrackRecommendationActivity::class.java)
                intentLogin.putExtra("accessToken", response.accessToken)
                startActivity(intentLogin)
            }
        }
    }

    companion object {
        private const val CLIENT_ID = "64c66e853d144c5ebe5fc5f91175e887"
        private const val REDIRECT_URI = "putmeon://callback"
        private const val REQUEST_CODE = 0
    }
}