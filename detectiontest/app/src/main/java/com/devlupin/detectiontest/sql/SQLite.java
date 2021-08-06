package com.devlupin.detectiontest.sql;

import android.provider.BaseColumns;

public final class SQLite {
    private SQLite() {

    }

    public static class Info implements BaseColumns {
        public static final String TABLE_NAME = "INFO";
        public static final String NAME = "NAME";
        public static final String ID = "ID";
        public static final String PW = "PW";
        public static final String PH_NUM = "PH_NUM";
        public static final String EMAIL = "EMAIL";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        NAME + " TEXT," +
                        ID + " TEXT PRIMARY KEY," +
                        PW + " TEXT," +
                        PH_NUM + " TEXT," +
                        EMAIL + " TEXT" +
                        ")";

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
