package com.romellbolton.putmeon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private static Context context;
    int PRIVATE_MODE = 0;
    private static String PREF_NAME;
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String CLIENT_ID = "8740928683fe4ab6be03091a875ac618";

    public SessionManager() {
    }

    public void createLoginSession(Context context, String apiUserName) {
        this.context = context;
        PREF_NAME = apiUserName;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_MULTI_PROCESS | PRIVATE_MODE);
        editor = pref.edit();
        editor.clear();
        editor.putBoolean(IS_LOGIN, true);
        editor.putString("id", apiUserName);
        editor.commit();
    }

    public String getToken() {
        return PREF_NAME;
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put("id", pref.getString("id", null));
        return user;
    }

    public String getClientId() {
        return CLIENT_ID;
    }

    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    public String getPrefName() {
        return PREF_NAME;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
