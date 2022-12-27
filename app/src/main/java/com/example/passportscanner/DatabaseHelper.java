package com.example.passportscanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper databaseHelper;

    public static final String TABLE_NAME = "Scanned";
    public static final String TIMESTAMP = "timestamp";
    public static final String ID = "id";
    public static final String SCANNED_TEXT = "text";
    public static final String TYPE = "type";

    private DatabaseHelper(Context context) {
        super(context, "UserData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // add code to create tables
        //create_table for Employee
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY  AUTOINCREMENT," + SCANNED_TEXT + " TEXT," + TIMESTAMP + " INTEGER," + TYPE + " TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop Table if EXISTS Scanned");
    }

    public static DatabaseHelper getInstance(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    public void insertData(ScannedDataModel model) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //#################################
        ContentValues contentValues = new ContentValues();
        contentValues.put(SCANNED_TEXT, model.getText());
        contentValues.put(TIMESTAMP, model.getTimestamp());
        contentValues.put(TYPE, model.getType());

        // get the auto-incremented ID
        sqLiteDatabase.insert("Scanned", null, contentValues);
    }

    public ArrayList<ScannedDataModel> loadAllScannedItems() {

        ArrayList<ScannedDataModel> scannedItems = new ArrayList<>();
        SQLiteDatabase mDb = this.getReadableDatabase();
        Cursor cursor = mDb.rawQuery("select * from " + TABLE_NAME, null);

        try {
            int scannedTextColumnIndex = cursor.getColumnIndex(SCANNED_TEXT);
            int timestampColumnIndex = cursor.getColumnIndex(TIMESTAMP);
            int typeColumnIndex = cursor.getColumnIndex(TYPE);
            while (cursor.moveToNext()) {
                String text = cursor.getString(scannedTextColumnIndex);
                long timestamp = cursor.getLong(timestampColumnIndex);
                String type = cursor.getString(typeColumnIndex);
                ScannedDataModel model = new ScannedDataModel(timestamp, text, type);
                scannedItems.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            mDb.close();
        }
        return scannedItems;
    }
}
