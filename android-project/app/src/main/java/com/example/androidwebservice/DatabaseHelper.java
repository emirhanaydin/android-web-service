package com.example.androidwebservice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "android";
    public static final String TABLE_NAME = "names";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STATUS = "status";

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
                + COLUMN_NAME + " VARCHAR NOT NULL, "
                + COLUMN_STATUS + " TINYINT "
                + ");";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS Persons";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public boolean addName(String name, int status) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_STATUS, status);

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
        return true;
    }

    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        sqLiteDatabase.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        sqLiteDatabase.close();
        return true;
    }

    public Cursor getNames() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC;";
        return sqLiteDatabase.rawQuery(sql, null);
    }

    /**
     * @return All names that are not synchronized with the database.
     */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + "=0;";
        return sqLiteDatabase.rawQuery(sql, null);
    }
}
