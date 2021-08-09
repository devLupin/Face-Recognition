package com.devlupin.detectiontest.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InfoDBHelper {
    private static final String DATABASE_NAME = "SCLAB";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    public static SQLiteDatabase rDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(Database.Info.SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+ Database.Info.TABLE_NAME);
            onCreate(db);
        }
    }

    public InfoDBHelper(Context context){
        this.mCtx = context;
    }

    public InfoDBHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        rDB = mDBHelper.getReadableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    public long insert(String name, String id, String pw, String ph_num, String email){
        ContentValues values = new ContentValues();
        values.put(Database.Info.NAME, name);
        values.put(Database.Info.ID, id);
        values.put(Database.Info.PW, pw);
        values.put(Database.Info.PH_NUM, ph_num);
        values.put(Database.Info.EMAIL, email);
        return mDB.insert(Database.Info.TABLE_NAME, null, values);
    }

    // empty is true
    public boolean isEmpty(String field, String arg){
        String sql = "SELECT * FROM " + Database.Info.TABLE_NAME + " WHERE " + field + "=" + "'" + arg + "'";
        Cursor cursor = rDB.rawQuery(sql, null);

        if(!cursor.moveToNext())
            return true;

        else
            return false;
    }

    public Cursor selectColumns(){
        return mDB.query(Database.Info.TABLE_NAME, null, null, null, null, null, null);
    }

    public boolean update(ContentValues values, String pw){
        return mDB.update(Database.Info.TABLE_NAME, values, Database.Info.PW+ "=" + pw, null) > 0;
    }

    // Delete Column
    public boolean delete(String id){
        return mDB.delete(Database.Info.TABLE_NAME, Database.Info.ID+ "=" + id, null) > 0;
    }
}