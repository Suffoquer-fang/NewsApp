package com.example.newsapplication.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;


public class MyDBOpenHelper extends SQLiteOpenHelper {


    private SQLiteDatabase db = null;

    private static MyDBOpenHelper instance = null;

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, null, 1);
//        this.db = getReadableDatabase();
    }

    public static MyDBOpenHelper getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                      int version)
    {
        if(instance == null)
            instance = new MyDBOpenHelper(context, name, factory, version);
        return instance;
    }

    public synchronized SQLiteDatabase getDatabase()
    {
        if(mOpenCounter.incrementAndGet() == 1) {

// Opening new database

            db = this.getReadableDatabase();

        }

        return db;

    }

    public synchronized void closeDatabase() {

        if (mOpenCounter.decrementAndGet() == 0) {

// Closing database
            db.close();

        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE News(_id INTEGER PRIMARY KEY AUTOINCREMENT, NewsId text unique, channel text, title text, author text, pubtime text, keywords blob, content text, isHis integer, isFav integer)");
        db.execSQL("CREATE TABLE SearchHis(_id INTEGER PRIMARY KEY AUTOINCREMENT, Time INTEGER, Content TEXT)");
        db.execSQL("CREATE TABLE BadKeys(_id INTEGER PRIMARY KEY AUTOINCREMENT, Content TEXT)");
        //this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public synchronized void insertHis(final ContentValues cv) {
        final SQLiteDatabase db = getDatabase();
        if (db.update("SearchHis", cv, "Content = ?", new String[]{cv.getAsString("Content")}) == 0)
            db.insert("SearchHis", null, cv);
    }

    public synchronized void deleteHis(final String str) {
        getDatabase().delete("SearchHis", "Content = ?", new String[]{str});
    }

    public synchronized Cursor query()
    {
        SQLiteDatabase tmp = getDatabase();
        Cursor ret = tmp.query("SearchHis", new String[]{"_id", "Content"}, null, null, null, null, "Time DESC");
        return ret;
    }

}


