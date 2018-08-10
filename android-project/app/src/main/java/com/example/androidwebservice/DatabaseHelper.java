package com.example.androidwebservice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Constants for Database name, table name, and column keywords
    public static final String DB_NAME = "android";
    public static final String TABLE_NAME = "keywords";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_KEYWORD = "keyword";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_SYNCED = "synced";

    //database version
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_KEYWORD + " VARCHAR NOT NULL UNIQUE, "
                + COLUMN_VALUE + " VARCHAR NOT NULL, "
                + COLUMN_SYNCED + " TINYINT "
                + ");";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS Persons";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public boolean addKeyword(String keyword, String value, boolean synced) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_KEYWORD, keyword);
        contentValues.put(COLUMN_VALUE, value);
        contentValues.put(COLUMN_SYNCED, synced);

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
        return true;
    }

    public void updateKeywordSynced(int id, boolean synced) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SYNCED, synced);
        sqLiteDatabase.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        sqLiteDatabase.close();
    }

    public Cursor getKeywords() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC;";
        return sqLiteDatabase.rawQuery(sql, null);
    }

    /**
     * @return All keywords that are not synchronized with the database.
     */
    public Cursor getUnsyncedKeywords() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SYNCED + "=0;";
        return sqLiteDatabase.rawQuery(sql, null);
    }
}
