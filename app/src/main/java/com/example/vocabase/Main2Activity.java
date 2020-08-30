package com.example.vocabase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Main2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main2);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);

        Integer langid = sharedPref.getInt("LangID",0);
        String  language = sharedPref.getString("Lang","");

        Toast toast;
        DbHandler dbHandler = new DbHandler(Main2Activity.this);
        int todayCount = dbHandler.countWord("",langid, 1);

            CharSequence text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
            String sentence = text.toString().trim();
            String[] words = sentence.split(" ");
            if (words.length > 4) {
                //DbHandler dbHandler = new DbHandler(Main2Activity.this);
                if (dbHandler.UpdateUserDetails(sentence,langid) != 0) {
                    toast = Toast.makeText(getApplicationContext(), "sentence saved in "+language, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    toast = Toast.makeText(getApplicationContext(), "not saved in "+language, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } else {
                if (todayCount < 10) {
                String upperString = text.toString().substring(0, 1).toUpperCase() + text.toString().substring(1);
                //DbHandler dbHandler = new DbHandler(Main2Activity.this);
                dbHandler.insertUserDetails(upperString, langid);
                int cntWrd = dbHandler.countWord(upperString,langid, 0);
                if (cntWrd > 1) {
                    toast = Toast.makeText(getApplicationContext(), text.toString().toUpperCase() + " saved, repeated " + cntWrd + " times in "+language.toUpperCase(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                } else {
                    toast = Toast.makeText(getApplicationContext(), text.toString().toUpperCase() + " saved in "+language.toUpperCase(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }

            }
                else
                {
                    toast = Toast.makeText(getApplicationContext(), "daily limit of 10 words reached, cannot save", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
        }

        this.finish();
    }
    }

