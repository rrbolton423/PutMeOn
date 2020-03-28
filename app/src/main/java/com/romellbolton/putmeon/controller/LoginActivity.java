package com.romellbolton.putmeon.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.romellbolton.putmeon.R;
import com.romellbolton.putmeon.util.AppStatus;
import com.romellbolton.putmeon.util.SessionManager;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class LoginActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "64c66e853d144c5ebe5fc5f91175e887";
    private static final String REDIRECT_URI = "putmeon://callback";
    private static final int REQUEST_CODE = 0;

    private Context mContext;
    private LoginActivity mLoginActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        Button loginButton;

        loginButton = (findViewById(R.id.action_login));
        loginButton.setOnClickListener(v -> {
            if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                        AuthenticationResponse.Type.TOKEN,
                        REDIRECT_URI);
                builder.setScopes(new String[]{"streaming", "user-read-recently-played", "playlist-modify-public"});
                AuthenticationRequest request = builder.build();
                AuthenticationClient.openLoginActivity(mLoginActivity, REQUEST_CODE, request);
            } else {
                Toast.makeText(this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                SessionManager sessionManager = new SessionManager();
                sessionManager.createLoginSession(mContext, response.getAccessToken());
                Intent intentLogin = new Intent(this, TrackRecommendationActivity.class);
                intentLogin.putExtra("accessToken", response.getAccessToken());
                startActivity(intentLogin);
            }
        }
    }
}
