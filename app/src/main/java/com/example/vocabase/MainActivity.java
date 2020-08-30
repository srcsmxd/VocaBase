
package com.example.vocabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    AlertDialog.Builder builder, builder1;
    AlertDialog dialog, dialog1;
    Integer value;
    Intent myIntent;
    Button langButton;
    Integer SelLang;
    String selLanguage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startService(new Intent(this, ClipboardMonitorService.class));


        //https://stackoverflow.com/questions/51343550/how-to-give-notifications-on-android-on-specific-time-in-android-oreo
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        langButton = findViewById(R.id.LangButton);
        Integer langid = sharedPref.getInt("LangID",0);
        if(langid == 0){
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putInt("LangID", 1);
            spEditor.putString("Lang", "english");
            spEditor.apply();
//            Toast.makeText(this,"New", Toast.LENGTH_SHORT).show();
            langButton.setText("english");
            SelLang = 1;
            selLanguage = "english";
        }else{
//            Toast.makeText(this,"Old", Toast.LENGTH_SHORT).show();
            SelLang = langid;
            langButton.setText(sharedPref.getString("Lang"," "));
            selLanguage = sharedPref.getString("Lang"," ");
        }

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

// if user enabled daily notifications
        //if (dailyNotify) {
            //region Enable Daily Notifications
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, sharedPref.getInt("dailyNotificationHour", 20));
            calendar.set(Calendar.MINUTE, sharedPref.getInt("dailyNotificationMin", 15));
            calendar.set(Calendar.SECOND, 1);
            // if notification time is before selected time, send notification the next day
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }
            if (manager != null) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
            //To enable Boot Receiver class
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            //endregion
       // } else { //Disable Daily Notifications
         //   if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && manager != null) {
         //       manager.cancel(pendingIntent);
                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
         //   }
          //  pm.setComponentEnabledSetting(receiver,
           ///         PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
             //       PackageManager.DONT_KILL_APP);
  //      }




        DbHandler dbhelper = new DbHandler(this);

        value = 1;
        FillListView(1, SelLang);
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Filter");
//        String total = String.valueOf(dbhelper.miniStats(0));
//        String today = String.valueOf(dbhelper.miniStats(1));
//        String revise = String.valueOf(dbhelper.miniStats(2));
//        String frequent = String.valueOf(dbhelper.miniStats(3));
//        String[] animals = {"Latest("+total+")",
//                "Alphabetically", "Frequents("+frequent+")","Today("+today+")","Stats",
//                "Revise("+revise+")"};
        String[] animals = {"Latest",
                "Alphabetically", "Frequents","Today","Stats",
                "Revise"};


        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // horse
                        //Toast.makeText(getApplicationContext(),"1",Toast.LENGTH_SHORT).show();
                        value = 1;
                        FillListView(1, SelLang);
                        break;
                    case 1: // cow
                        //Toast.makeText(getApplicationContext(),"2",Toast.LENGTH_SHORT).show();
                        value = 2;
                        FillListView(2,SelLang);
                        break;
                    case 2: // cow
                        //Toast.makeText(getApplicationContext(),"3",Toast.LENGTH_SHORT).show();
                        value = 3;
                        FillListView(3, SelLang);
                        break;
                    case 3:
                        value = 4;
                        FillListView(4, SelLang);
                        break;
                    case 4:
                        myIntent = new Intent(MainActivity.this, VocabStats.class);
                        MainActivity.this.startActivity(myIntent);
                        break;
                    case 5:
                        myIntent = new Intent(MainActivity.this, ReviseActivity.class);
                        MainActivity.this.startActivity(myIntent);
                        break;

                }
            }
        });

// create and show the alert dialog
        dialog = builder.create();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

            }
        });
    }

    public void FillListView(final Integer val, final Integer selLang){
        ListView myListview = findViewById(R.id.user_list);
        final DbHandler dbhelper = new DbHandler(this);

        final ArrayList<WordDetails> wordDetails = dbhelper.getAllData(val, selLang);

        final CustomAdaptor myAdapter = new CustomAdaptor(wordDetails, this);
        myListview.setAdapter(myAdapter);
        myListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = "";
                if (selLanguage.equals("english")){
                    url = "https://www.google.com/search?q=define+"+wordDetails.get(position).getWord().toLowerCase();
                }
                else{
                    url = "https://www.google.com/search?q=define+"+wordDetails.get(position).getWord().toLowerCase()+" in "+selLanguage;
                }

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(MainActivity.this, Uri.parse(url));
        }
        });
        myListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                if (val == 1)
                                {
                                    dbhelper.DeleteUser(Integer.parseInt(wordDetails.get(position).getID()));
                                    wordDetails.remove(position);
                                    myAdapter.notifyDataSetChanged();
                                    Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();

                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Delete through filter -> latest",Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //N
                                // o button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Delete ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FillListView(value, SelLang);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        File sd = null;
        File dbFile = null;
        String dstPath = null;
        File dst = null;
        File dst1 = null;
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            switch (item.getItemId()) {
                case 1212://R.id.item1:
//                String outFileName = ;
                    dbFile = new File("/data/data/" + getPackageName() + "/databases/vocabase");
                    dstPath = Environment.getExternalStorageDirectory() + File.separator + "VocaBase" + File.separator;
                    dst = new File(dstPath);
                    dst1 = new File(dst.getPath() + File.separator + "vocabase");

                    if (dst1.exists()) {
                        final File finalDbFile = dbFile;
                        final File finalDst = dst;
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        try {
                                            exportFile(finalDbFile, finalDst);
                                        } catch (IOException e) {

                                            e.printStackTrace();
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("File already exists, Do you want to overwrite ?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    } else {
                        try {
                            exportFile(dbFile, dst);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 123123: //R.id.item2:
                    dbFile = new File(Environment.getExternalStorageDirectory() + File.separator + "VocaBase" + File.separator + "vocabase");
                    dstPath = "/data/data/" + getPackageName() + "/databases/";
                    dst = new File(dstPath);
                    dst1 = new File(dst.getPath() + File.separator + "vocabase");

                    if (dst1.exists()) {
                        final File finalDbFile = dbFile;
                        final File finalDst = dst;
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        try {
                                            exportFile(finalDbFile, finalDst);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("File already exists, Do you want to overwrite ?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    } else {
                        try {
                            exportFile(dbFile, dst);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.item3:
                    myIntent = new Intent(MainActivity.this, LangActivity.class);
                    MainActivity.this.startActivity(myIntent);
                    break;

                 default:
                    return super.onOptionsItemSelected(item);
            }
//        }
//        else
//        {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
//                    1);
//        }
        return true;
    }
    private File exportFile(File src, File dst) throws IOException {
            //if folder does not exist
        if (!dst.exists()) {
            if (!dst.mkdir()){
                return null;
            }
        }

       // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File expFile = new File(dst.getPath() + File.separator + "vocabase");
            FileChannel inChannel = null;
            FileChannel outChannel = null;

            try {
                inChannel = new FileInputStream(src).getChannel();
                outChannel = new FileOutputStream(expFile).getChannel();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                inChannel.transferTo(0, inChannel.size(), outChannel);
                Toast.makeText(this,"Done",Toast.LENGTH_SHORT).show();
            } finally {
                if (inChannel != null)
                    inChannel.close();
                if (outChannel != null)
                    outChannel.close();
            }


        return expFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  Toast.makeText(this,"Permission Granted, now you can backup or restore",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"Permission Diened",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public void LangChange(View view) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor spEditor = sharedPref.edit();
        final ArrayList<String> ArrStr = new ArrayList<>();
        DbHandler dbhelper = new DbHandler(this);

        final Cursor alllang = dbhelper.getLangs();
        while (alllang.moveToNext()) {
            ArrStr.add(alllang.getString(1));
        }



        int ArLen = ArrStr.size();
        String[] animals = new String[ArLen];
        int i = 0;
        final ArrayList<Integer> langids = new ArrayList<>();
        for (String ele:ArrStr){
            animals[i] =  ele.substring(0, 1).toUpperCase() + ele.substring(1);
            langids.add(i);
            i++;
        }
        langButton = findViewById(R.id.LangButton);

        builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setTitle("Language");

        alllang.moveToFirst();
        builder1.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        alllang.move(which);
                        langButton.setText(ArrStr.get(which));
                        spEditor.putInt("LangID", Integer.parseInt(alllang.getString(0)));
                        spEditor.putString("Lang", alllang.getString(1));
                        spEditor.apply();
                        SelLang = Integer.parseInt(alllang.getString(0));
                        selLanguage = alllang.getString(1);
                        FillListView(1, Integer.parseInt(alllang.getString(0)));
                        break;
                    case 1:
                        alllang.move(which);
                        langButton.setText(ArrStr.get(which));
                        spEditor.putInt("LangID", Integer.parseInt(alllang.getString(0)));
                        spEditor.putString("Lang", alllang.getString(1));
                        spEditor.apply();
                        SelLang = Integer.parseInt(alllang.getString(0));
                        FillListView(1, Integer.parseInt(alllang.getString(0)));
                        selLanguage = alllang.getString(1);
                        break;
                    case 2:
                        alllang.move(which);
                        langButton.setText(ArrStr.get(which));
                        spEditor.putInt("LangID", Integer.parseInt(alllang.getString(0)));
                        spEditor.putString("Lang", alllang.getString(1));
                        spEditor.apply();
                        SelLang = Integer.parseInt(alllang.getString(0));
                        FillListView(1, Integer.parseInt(alllang.getString(0)));
                        selLanguage = alllang.getString(1);
                        break;
                    case 3:
                        alllang.move(which);
                        langButton.setText(ArrStr.get(which));
                        spEditor.putInt("LangID", Integer.parseInt(alllang.getString(0)));
                        spEditor.putString("Lang", alllang.getString(1));
                        spEditor.apply();
                        SelLang = Integer.parseInt(alllang.getString(0));
                        FillListView(1, Integer.parseInt(alllang.getString(0)));
                        selLanguage = alllang.getString(1);
                        break;
                    case 4:
                        alllang.move(which);
                        langButton.setText(ArrStr.get(which));
                        spEditor.putInt("LangID", Integer.parseInt(alllang.getString(0)));
                        spEditor.putString("Lang", alllang.getString(1));
                        spEditor.apply();
                        SelLang = Integer.parseInt(alllang.getString(0));
                        FillListView(1, Integer.parseInt(alllang.getString(0)));
                        selLanguage = alllang.getString(1);
                        break;
                    case 5:
                        alllang.move(which);
                        langButton.setText(ArrStr.get(which));
                        spEditor.putInt("LangID", Integer.parseInt(alllang.getString(0)));
                        spEditor.putString("Lang", alllang.getString(1));
                        spEditor.apply();
                        SelLang = Integer.parseInt(alllang.getString(0));
                        FillListView(1, Integer.parseInt(alllang.getString(0)));
                        selLanguage = alllang.getString(1);
                        break;

                }
                Toast.makeText(getApplicationContext(),alllang.getString(1) + " is selected",Toast.LENGTH_SHORT).show();
            }
        });
// create and show the alert dialog
        dialog1 = builder1.create();
        dialog1.show();
    }
}
