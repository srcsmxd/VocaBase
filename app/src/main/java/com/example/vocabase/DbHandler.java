package com.example.vocabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "vocabase";
    private static final String TABLE_Users = "vocabulary";
    private static final String KEY_ID = "id";
    private static final String KEY_WORD = "word";
    private static final String KEY_SENTENCE = "sentence";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    public static final String  LAST_REV_DATE = "lastrev";
    public static final String REV_COUNT = "revcnt";

    private static final String TABLE_Users1 = "usagelist";
    private static final String KEY_WORD_ID = "wordid";

    private static final String TABLE_Users3 = "langtable";
    private static final String KEY_LANG = "language";
    private static final String KEY_LANG_ID = "langid";


    public DbHandler(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_Users + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_LANG_ID + " INTEGER," +
                KEY_WORD + " TEXT," +
                KEY_SENTENCE + " TEXT," +
                KEY_DATE +" DEFAULT CURRENT_DATE," +
                KEY_TIME +" DEFAULT CURRENT_TIME," +
                REV_COUNT +" INTEGER DEFAULT 0,"+
                LAST_REV_DATE + " DEFAULT CURRENT_DATE" + ")";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + TABLE_Users1 + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_LANG_ID + " INTEGER," +
                KEY_WORD_ID + " INTEGER," +
                KEY_WORD + " TEXT," +
                KEY_SENTENCE + " TEXT," +
                KEY_DATE +" DEFAULT CURRENT_DATE," +
                KEY_TIME +" DEFAULT CURRENT_TIME" + ")";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + TABLE_Users3 + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_LANG + " TEXT ," +
                KEY_DATE +" DEFAULT CURRENT_DATE," +
                KEY_TIME +" DEFAULT CURRENT_TIME" + ")";
        db.execSQL(CREATE_TABLE);

        ContentValues cValues = new ContentValues();
        cValues.put(KEY_LANG, "english");
        db.insert(TABLE_Users3,null, cValues);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Users);
        // Create tables again
        onCreate(db);
    }
    // **** CRUD (Create, Read, Update, Delete) Operations ***** //

    // Adding new User Details
    void insertUserDetails(String name, Integer langid){
        //Get the Data Repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_LANG_ID, langid);
        cValues.put(KEY_WORD, name);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_Users,null, cValues);
        db.close();
    }
    public  int countWord(String name,Integer langid, Integer i){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        if(i == 0) {
            res = db.rawQuery("SELECT * FROM " + TABLE_Users + " WHERE word=? ", new String[]{name});
        }
        else if(i == 1){
            res = db.rawQuery("SELECT * FROM "+ TABLE_Users + " WHERE date = CURRENT_DATE AND langid=? ORDER BY id DESC",new String[]{String.valueOf(langid)});
        }
        else if(i == 2) {
            res = db.rawQuery("SELECT * FROM " + TABLE_Users1 + " WHERE word=?", new String[]{name});
        }



        return res.getCount();
    }
    public Cursor getLatest(Integer langid) {
        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor res = db.rawQuery("SELECT * FROM "+ TABLE_Users + " LEFT JOIN "+
//                TABLE_Users1 +" ON "+TABLE_Users1+".word=" +TABLE_Users+".word " +
//                "WHERE " +TABLE_Users1+".word IS NULL " +
//                "AND " + TABLE_Users + ".date!=CURRENT_DATE " +
//                "AND " +TABLE_Users+".langid=? " +
//                "ORDER BY " +TABLE_Users+".id DESC ",new String[]{String.valueOf(langid)});

        Cursor res = db.rawQuery("SELECT * FROM "+ TABLE_Users +
                " WHERE " +
                " langid=? AND " +
                " ( revcnt=0 AND " + " date!=CURRENT_DATE ) OR " +
                " ( revcnt=1 AND " + " julianday(lastrev) - julianday(CURRENT_DATE)  > 5 ) OR " +
                " ( revcnt=2 AND " + " julianday(lastrev) - julianday(CURRENT_DATE)  > 15 ) " +

                "ORDER BY id DESC ",new String[]{String.valueOf(langid)});

        return res;
    }
    public ArrayList<WordDetails> getAllData(Integer value, Integer selLang) {
        ArrayList<WordDetails> wordDetails = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM "+ TABLE_Users + " WHERE langid=? ORDER BY id DESC",new String[]{String.valueOf(selLang)});

        if (value == 1)
        {
            res = db.rawQuery("SELECT * FROM "+ TABLE_Users + " WHERE langid=? ORDER BY id DESC",new String[]{String.valueOf(selLang)});

        }
        else if (value == 2)
        {
             res = db.rawQuery("SELECT * FROM "+ TABLE_Users + " WHERE langid=? GROUP BY word ORDER BY word ASC",new String[]{String.valueOf(selLang)});

        }
        else if (value == 3)
        {
             res = db.rawQuery("SELECT * FROM "+ TABLE_Users + " WHERE langid=? GROUP BY word HAVING count(word) > 1 ORDER BY count(word) DESC",new String[]{String.valueOf(selLang)});
        }
        else if (value == 4)
        {
            res = db.rawQuery("SELECT * FROM "+ TABLE_Users + " WHERE langid=? AND date = CURRENT_DATE ORDER BY id DESC",new String[]{String.valueOf(selLang)});
        }
        while(res.moveToNext()) {
            String id = res.getString(0);   //0 is the number of id column in your database table
            String word = res.getString(2);
            String sentence = res.getString(3);
            WordDetails newDog = new WordDetails(id,word,sentence);
            wordDetails.add(newDog);
        }
        return wordDetails;
    }
    void insertUsage(Cursor inp, String sentence, Integer langid){
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys

        if(inp.getCount() > 0) {
            inp.moveToFirst();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_WORD_ID, inp.getString(0));
            cValues.put(KEY_LANG_ID,langid);
            cValues.put(KEY_WORD, inp.getString(2));
            cValues.put(KEY_SENTENCE, sentence);
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(TABLE_Users1,null, cValues);
            db.execSQL("UPDATE "+TABLE_Users+" SET revcnt = revcnt + 1, lastrev=CURRENT_DATE WHERE "+KEY_ID+"=" +inp.getString(0));
        }
        db.close();
    }

    // Get User Details
//    public ArrayList<String> GetUsers(Integer value){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ArrayList<String> userList = new ArrayList<>();
//        String query =  query = "SELECT word FROM "+ TABLE_Users + " ORDER BY date DESC, time DESC";
//        if ( value == 1){
//            query = "SELECT word FROM "+ TABLE_Users + " ORDER BY date DESC, time DESC";
//        }
//        else if( value == 2 ){
//            query = "SELECT word FROM "+ TABLE_Users + " GROUP BY word ORDER BY word ASC";
//        }
//        else if( value == 3)
//        {
//            query = "SELECT word FROM "+ TABLE_Users + " GROUP BY word ORDER BY count(word) DESC";
//        }
//        Cursor cursor = db.rawQuery(query,null);
//        while (cursor.moveToNext()){
//           // HashMap<String,String> user = new HashMap<>();
//            //user.put("name",cursor.getString(cursor.getColumnIndex(KEY_WORD)));
//            userList.add(cursor.getString(cursor.getColumnIndex(KEY_WORD)));
//        }
//        //userList.add("");
//        return  userList;
//    }
    // Get User Details based on userid
//    public ArrayList<HashMap<String, String>> GetUserByUserId(int userid){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
//        String query = "SELECT name, location, designation FROM "+ TABLE_Users;
//        Cursor cursor = db.query(TABLE_Users, new String[]{KEY_NAME, KEY_LOC, KEY_DESG}, KEY_ID+ "=?",new String[]{String.valueOf(userid)},null, null, null, null);
//        if (cursor.moveToNext()){
//            HashMap<String,String> user = new HashMap<>();
//            user.put("name",cursor.getString(cursor.getColumnIndex(KEY_NAME)));
//            user.put("designation",cursor.getString(cursor.getColumnIndex(KEY_DESG)));
//            user.put("location",cursor.getString(cursor.getColumnIndex(KEY_LOC)));
//            userList.add(user);
//        }
//        return  userList;
//    }
    // Delete User Details
    public void DeleteUser(int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Users, KEY_ID+" = ?",new String[]{String.valueOf(userid)});
        db.close();
    }

    // Update User Details
    public int UpdateUserDetails(String sentence, Integer selLang) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_Users + " WHERE langid=? ORDER BY id DESC LIMIT 1", new String[]{String.valueOf(selLang)});
        String word = null;
        String id = null;
        int count = 0;
        while (res.moveToNext()) {
            id = res.getString(0);
            word = res.getString(2);
        }
        if (word!=null && sentence.toLowerCase().contains(word.toLowerCase()))
        {
            db = this.getWritableDatabase();
            ContentValues cVals = new ContentValues();
            cVals.put(KEY_SENTENCE, sentence);
            count = db.update(TABLE_Users, cVals, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        }
        return count;
    }

    public int miniStats(Integer i, Integer langid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        if (i == 0) {
            res = db.rawQuery("SELECT count(*) FROM " + TABLE_Users + " WHERE langid=?", new String[]{String.valueOf(langid)});
        } else if (i == 1) {
            res = db.rawQuery("SELECT count(*) FROM " + TABLE_Users + " WHERE date = CURRENT_DATE AND langid=?", new String[]{String.valueOf(langid)});
        } else if (i == 2){
             res = db.rawQuery("SELECT count(*) FROM "+ TABLE_Users + " LEFT JOIN "+
                    TABLE_Users1 +" ON "+TABLE_Users1+".word=" +TABLE_Users+".word WHERE " +TABLE_Users1+".word IS NULL ORDER BY " +TABLE_Users+".id DESC ",null);
        }else if (i == 3)
        {
            res = db.rawQuery("SELECT count(cnt) FROM (SELECT count(*) as cnt FROM "+ TABLE_Users + " WHERE langid=? GROUP BY word HAVING count(word) > 1) ", new String[]{String.valueOf(langid)});
        } else if (i == 4) {
            res = db.rawQuery("SELECT count(*) FROM " + TABLE_Users + " WHERE langid=? AND revcnt=1", new String[]{String.valueOf(langid)});
        }
        else if (i == 5) {
            res = db.rawQuery("SELECT count(*) FROM " + TABLE_Users + " WHERE langid=? AND revcnt=2", new String[]{String.valueOf(langid)});
        }
        else if (i == 6) {
            res = db.rawQuery("SELECT count(*) FROM " + TABLE_Users + " WHERE langid=? AND revcnt=3", new String[]{String.valueOf(langid)});
        }
        if(res.getCount() > 0) {
            res.moveToFirst();
        }
            return res.getInt(0);
    }
    public Cursor getLangs()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_Users3 + "", null);
        return res;
    }
    public ArrayList<LangList> getLangList() {
        ArrayList<LangList> langListArr = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM "+ TABLE_Users3 + "",null);

        while(res.moveToNext()) {
            String id = res.getString(0);   //0 is the number of id column in your database table
            String language = res.getString(1);
            LangList langlist = new LangList(id,language);
            langListArr.add(langlist);
        }
        return langListArr;
    }
    public long insertLang(String lang){
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_Users3 + " WHERE language=?", new String[]{lang.toLowerCase()});
        long newRowId = 0;
        if (res.getCount() == 0){
        if (lang.length() > 0 ) {
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_LANG, lang.toLowerCase());
            newRowId = db.insert(TABLE_Users3,null, cValues);
        }}
        db.close();
        return newRowId;
    }

    public void DeleteLang(Integer langid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Users, KEY_LANG_ID+" = ?",new String[]{String.valueOf(langid)});
        db.delete(TABLE_Users1, KEY_LANG_ID+" = ?",new String[]{String.valueOf(langid)});
        db.delete(TABLE_Users3, KEY_ID+" = ?",new String[]{String.valueOf(langid)});
        db.close();
    }

}