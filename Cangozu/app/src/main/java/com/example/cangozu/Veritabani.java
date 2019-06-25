package com.example.cangozu;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Veritabani extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "users";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String GENDER = "gender";


    public Veritabani(Context context) {

            super(context , DATABASE_NAME , null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + DATABASE_TABLE + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NAME + " TEXT ,"
                + SURNAME + " TEXT,"
                + GENDER + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }


     public void addUser(String name, String surname,String gender) {
        //Databese veri eklemek için
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(SURNAME, surname);
        values.put(GENDER, gender);

        db.insert(DATABASE_TABLE, null, values);
        db.close();  //Database Bağlantısını kapattık*/
    }

    public void resetTables(){
        //Bunuda uygulamada kullanmıyoruz. Tüm verileri siler. tabloyu resetler.
        //Kullanıcı dısında biz kullanmak ısteyebılırız.
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(DATABASE_TABLE, null, null);
        db.close();
    }

    public void userUpdate(String name, String surname ,String gender,int id) {

        //Bu methodda ise var olan veriyi güncelliyoruz(update)
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(SURNAME, surname);
        values.put(GENDER, gender);


        // updating row
        db.update(DATABASE_TABLE, values, ID + " = ?",
                new String[] { String.valueOf(id) });
    }


    public void deleteUser(int id){  //id si belli olan row u silmek için

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE İF EXİSTS"   + DATABASE_TABLE);
        onCreate(db);
    }
}
