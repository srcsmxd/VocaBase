
package com.example.vocabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ReviseActivity extends AppCompatActivity {
    ToggleButton b;
    TextView tvUsage,word;
    EditText etUsage;
    DbHandler dbhelper;
    RelativeLayout rl;
    Cursor latst;
    Integer langid;
    String language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);
        b = findViewById(R.id.toggleButton);
        word = findViewById(R.id.word);
        tvUsage = findViewById(R.id.usage);
        etUsage = findViewById(R.id.etUsage);
        rl = findViewById(R.id.rl);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(b.isChecked()) {
                    tvUsage.setVisibility(View.VISIBLE);
                }
                else {
                    tvUsage.setVisibility(View.GONE);
                }
            }
        });
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        langid = sharedPref.getInt("LangID",0);
        language = sharedPref.getString("Lang", "");
        FillData();
    }

    public void FillData(){
        dbhelper = new DbHandler(this);
        latst = dbhelper.getLatest(langid);
            if (latst.getCount() > 0) {
                latst.moveToFirst();
                word.setText(latst.getString(2));
                tvUsage.setText(latst.getString(3));
            }
            else
            {
                word.setText("");
                tvUsage.setText("");
                rl.setVisibility(View.INVISIBLE);
                Toast.makeText(this,"no " + language +" words to revise", Toast.LENGTH_SHORT).show();
            }
    }
    public void sumbitSentence(View view) {
        String sentence = etUsage.getText().toString().trim();
        String word = latst.getString(2);
        if (sentence.split(" ").length >= 3) {
            if (!sentence.equals("") && sentence.toLowerCase().contains(word.toLowerCase())) {
                dbhelper.insertUsage(latst, sentence,langid);
                etUsage.setText("");
                FillData();
            } else {
                Toast.makeText(this, "write a sentence using the word", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "write atleast with 3 words", Toast.LENGTH_SHORT).show();
        }
    }
}
