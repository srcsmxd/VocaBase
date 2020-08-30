package com.example.vocabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

public class VocabStats extends AppCompatActivity {
    Integer langid;
    String selLanguage;
    TextView tvTotal, tvFreq, tvToday, tvRev1, tvRev2, tvRev3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocab_stats);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        langid = sharedPref.getInt("LangID",0);
        selLanguage = sharedPref.getString("Lang"," ");

        DbHandler dbhelper = new DbHandler(this);
        tvTotal = findViewById(R.id.tvTotal);
        tvFreq = findViewById(R.id.tvFreq);
        tvToday = findViewById(R.id.tvToday);
        tvRev1 = findViewById(R.id.tvRev1);
        tvRev2 = findViewById(R.id.tvRev2);
        tvRev3 = findViewById(R.id.tvRev3);


        tvTotal.setText(String.valueOf(dbhelper.miniStats(0,langid)) + " words");
        tvToday.setText(String.valueOf(dbhelper.miniStats(1,langid)) + " words today");
        tvFreq.setText(String.valueOf(dbhelper.miniStats(3,langid)) + " frequent words");
        tvRev1.setText(String.valueOf(dbhelper.miniStats(4,langid)) + " words revised once");
        tvRev2.setText(String.valueOf(dbhelper.miniStats(5,langid)) + " words revised twice");
        tvRev3.setText(String.valueOf(dbhelper.miniStats(6,langid)) + " words revised thrice");

    }
}
