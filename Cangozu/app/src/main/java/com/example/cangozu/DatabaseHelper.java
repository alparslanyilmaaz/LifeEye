package com.example.cangozu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "canGozu1.db";
    public static final String TABLE_NAME = "canGozuUser1_t";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "NAME";
    public static final String COL_2 = "SURNAME";
    public static final String COL_3 = "GENDER";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SURNAME TEXT, GENDER TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, String surname, String gender){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, name);
        cv.put(COL_2, surname);
        cv.put(COL_3, gender);

        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res= db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public boolean updateData(String id, String name, String surname, String gender){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_0, id);
        cv.put(COL_1, name);
        cv.put(COL_2, surname);
        cv.put(COL_3, gender);
        db.update(TABLE_NAME, cv, "ID = ?",new String[] {id});
        return true;
    }
}
