package com.example.android.loa.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.android.loa.network.models.User;

public class SessionPrefs {
    public static final String PREFS_NAME = "LOGIN_PREFS";
    public static final String PREF_USER_NAME = "PREF_USER_NAME";
    public static final String PREF_USER_MAIL = "PREF_USER_MAIL";
    public static final String PREF_USER_PHONE = "PREF_USER_PHONE";
    public static final String PREF_USER_TOKEN = "PREF_USER_TOKEN";

    private final SharedPreferences mPrefs;
    private boolean mIsLoggedIn = false;

    private static SessionPrefs INSTANCE;

    public static SessionPrefs get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SessionPrefs(context);
        }
        return INSTANCE;
    }

    private SessionPrefs(Context context) {
        mPrefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        mIsLoggedIn = !TextUtils.isEmpty(mPrefs.getString(PREF_USER_TOKEN, null));
    }

    public boolean isLoggedIn(){
        return mIsLoggedIn;
    }

    public void saveUser(User user) {
        if (user != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_USER_NAME, user.name);
            editor.putString(PREF_USER_MAIL, user.mail);
            editor.putString(PREF_USER_PHONE, user.phone);
            editor.putString(PREF_USER_TOKEN, user.token);
            editor.apply();

            if(!user.token.equals(null)){
                mIsLoggedIn = true;
            }
        }
    }

    public void logOut(){
        mIsLoggedIn = false;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_USER_NAME, null);
        editor.putString(PREF_USER_PHONE, null);
        editor.putString(PREF_USER_MAIL, null);
        editor.putString(PREF_USER_TOKEN, null);
        editor.apply();
    }

    public String getToken(){
        return mPrefs.getString(PREF_USER_TOKEN, null);
    }

    public String getName(){
        return mPrefs.getString(PREF_USER_NAME, null);
    }

    public void setToken(String token){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_USER_TOKEN, token);
        editor.apply();
        mIsLoggedIn=true;
    }

    public void setName(String token){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_USER_NAME, token);
        editor.apply();
      //  mIsLoggedIn=true;
    }

}
