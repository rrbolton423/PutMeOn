package com.romellbolton.putmeon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class LoginActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "64c66e853d144c5ebe5fc5f91175e887";
    private static final String REDIRECT_URI = "putmeon://callback";
    private Context context;
    private LoginActivity loginActivity = this;
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        Button loginButton;

        loginButton = (findViewById(R.id.action_login));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                        AuthenticationResponse.Type.TOKEN,
                        REDIRECT_URI);
                builder.setScopes(new String[]{"streaming", "user-read-private", "playlist-modify-public"});
                AuthenticationRequest request = builder.build();
                AuthenticationClient.openLoginActivity(loginActivity, REQUEST_CODE, request);
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
                sessionManager.createLoginSession(context, response.getAccessToken());
                Intent intentLogin = new Intent(this, HomeActivity.class);
                startActivity(intentLogin);
            }
        }
    }
}
