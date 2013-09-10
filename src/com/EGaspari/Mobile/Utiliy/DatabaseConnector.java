package com.EGaspari.Mobile.Utiliy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseConnector {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = Constant.DATABASE_NAME;
    private static final String TABLE_QRCODES = Constant.TABLE_QRCODE_NAME;
    private static final String KEY_QRCODE_ID = "id";
    private static final String KEY_QRCODE_NOME = "nome";
    private static final String KEY_QRCODE_DATA = "data";
    private static final String KEY_QRCODE_URL = "url";
    private SQLiteDatabase database;
    private DatabaseOpenHelper dbOpenHelper;

    public DatabaseConnector(Context context) {
        dbOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    public void openWritableMode() throws SQLException {
        //open database in reading/writing mode
        database = dbOpenHelper.getWritableDatabase();
    }
    public void openReadableMode() throws SQLException {
        database = dbOpenHelper.getReadableDatabase();
    }

    public void close() {
        if (database != null) {
            database.close();
        }
    }

    public void insertQrCode(String name, String data, String url) {
        ContentValues newCon = new ContentValues();
        newCon.put(KEY_QRCODE_NOME, name);
        newCon.put(KEY_QRCODE_DATA, data);
        newCon.put(KEY_QRCODE_URL, url);
        openWritableMode();
        database.insert(TABLE_QRCODES, null, newCon);
        close();
    }

    public Cursor getAllContacts() {   
        openReadableMode();
        Cursor c = database.query(TABLE_QRCODES, new String[]{KEY_QRCODE_ID, KEY_QRCODE_NOME, KEY_QRCODE_DATA, KEY_QRCODE_URL},
                null, null, null, null, KEY_QRCODE_DATA);
        return c;
    }

    public Cursor getOneContact(long id) {
        return database.query("country", null, "_id=" + id, null, null, null, null);
    }

    public void deleteContact(long id) {
        //open();
        database.delete("country", "_id=" + id, null);
        close();
    }
    
    
    private class DatabaseOpenHelper extends SQLiteOpenHelper {

    public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_QRCODES + " ( " + KEY_QRCODE_ID + " integer primary key autoincrement, " 
                + KEY_QRCODE_NOME + "," + KEY_QRCODE_DATA + ","  + KEY_QRCODE_URL + ");";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

}
