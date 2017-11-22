package com.example.asinghi.weatherproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;



public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "output.db";
    public static final String TABLE_PRODUCTS = "outputs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PRODUCTNAME = "json";

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                COLUMN_PRODUCTNAME + " TEXT " +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void add(Output output)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCTNAME , output.getJson());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PRODUCTS,null,values);
        db.close();

    }
    public void deleteProduct(String productName)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME + "=\"" + productName + "\";");

    }

    public String[] databasetoString()
    {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { COLUMN_PRODUCTNAME };


        Cursor cursor = db.query(TABLE_PRODUCTS, projection, null, null, null, null, null);

        int i=0;
        int length = cursor.getCount();

        String[] str = new String[length];
        while(cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_PRODUCTNAME));
            str[i] = itemId;
            i++;
        }
        cursor.close();
        Log.d("MSG" , " " + cursor.getColumnCount() );
        db.close();
        return str;
    }
}
