package com.example.vocabase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class LangActivity extends AppCompatActivity {
    EditText etLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang);
        etLang = findViewById(R.id.etLang);
        FillLangLV();


    }
    public void LangSubmit(View view){
        String language = etLang.getText().toString().trim();
        DbHandler dbhelper = new DbHandler(this);
        if (language.length() > 0){
            if (dbhelper.insertLang(language) > 0) {
                Toast.makeText(this,language + " added",Toast.LENGTH_SHORT).show();
                FillLangLV();
                etLang.setText("");
            }
            else {
                Toast.makeText(this,"No language added",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this,"Enter the language",Toast.LENGTH_SHORT).show();
        }
    }
    public void FillLangLV(){
        ListView myListview = findViewById(R.id.lvLang);
        final DbHandler dbhelper = new DbHandler(this);
        final ArrayList<LangList> langList = dbhelper.getLangList();
        final CustomAdaptorLang myAdapter = new CustomAdaptorLang(langList, this);
        myListview.setAdapter(myAdapter);

        myListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                dbhelper.DeleteLang(Integer.parseInt(langList.get(position).getID()));
                                langList.remove(position);
                                myAdapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //N
                                // o button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(LangActivity.this);
                builder.setMessage("Deletes all the vocabulary of this language ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;
            }
        });
    }
}
