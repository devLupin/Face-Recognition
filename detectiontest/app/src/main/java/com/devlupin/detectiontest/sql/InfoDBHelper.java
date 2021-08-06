package com.devlupin.detectiontest.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InfoDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SCLAB";
    public static final int DATABASE_VERSION = 1;

    public InfoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQLite.Info.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQLite.Info.SQL_DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void insert(String name, String id, String pw, String ph_num, String email) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(SQLite.Info.NAME, name);
        values.put(SQLite.Info.ID, id);
        values.put(SQLite.Info.PW, pw);
        values.put(SQLite.Info.PH_NUM, ph_num);
        values.put(SQLite.Info.EMAIL, email);

        db.insert(SQLite.Info.TABLE_NAME, null, values);
    }

    public Cursor select() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + SQLite.Info.TABLE_NAME + ";";
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();
        return results;
    }

    public Cursor select(String field, String arg) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT " + field + " FROM " + SQLite.Info.TABLE_NAME + " WHERE " + field + "=" + "'" + arg + "'";
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();
        return results;
    }
}