package com.example.byeongwoo.handroad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPrefernces;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String ID = "ID";

    public SessionManager(Context context) {
        this.context = context;
        sharedPrefernces = context.getSharedPreferences(PREF_NAME ,PRIVATE_MODE);
        editor = sharedPrefernces.edit();
    }

    public void createSession(String name, String id){
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(ID, id );
        editor.apply();

    }

    public boolean isLoggin(){
        return sharedPrefernces.getBoolean(LOGIN, false);
    }

    public void  checkLogin(){
        if(!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((mainActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(NAME, sharedPrefernces.getString(NAME, null));
        user.put(ID , sharedPrefernces.getString(ID, null));
        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((mainActivity) context).finish();
    }
}