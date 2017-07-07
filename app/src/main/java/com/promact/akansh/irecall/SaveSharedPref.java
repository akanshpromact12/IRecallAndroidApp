package com.promact.akansh.irecall;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;

class SaveSharedPref
{
    private static final String PREF_ID_TOKEN = "idToken";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PHOTO_URI = "photoUri";

    static SharedPreferences getSharedPreferences(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    static void setPrefs(Context context, String idToken, String username, String email, String photoUri)
    {
        Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_ID_TOKEN, idToken);
        editor.putString(PREF_USERNAME, username);
        editor.putString(PREF_EMAIL, email);
        editor.putString(PREF_PHOTO_URI, photoUri);
        editor.apply();
    }

    static String getToken(Context context)
    {
        return getSharedPreferences(context).getString(PREF_ID_TOKEN, "");
    }

    static String getUsername(Context context)
    {
        return getSharedPreferences(context).getString(PREF_USERNAME, "");
    }

    static String getEmail(Context context)
    {
        return getSharedPreferences(context).getString(PREF_EMAIL, "");
    }

    static String getPhotoUri(Context context)
    {
        return getSharedPreferences(context).getString(PREF_PHOTO_URI, "");
    }
}
