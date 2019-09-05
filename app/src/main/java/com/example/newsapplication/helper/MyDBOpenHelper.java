package com.example.newsapplication.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDBOpenHelper extends SQLiteOpenHelper {


    private SQLiteDatabase db = null;


    public MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, null, 1);
//        this.db = getReadableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE News(_id INTEGER PRIMARY KEY AUTOINCREMENT, NewsId text unique, channel text, title text, author text, pubtime text, Images blob, content text, isHis integer, isFav integer)");
        db.execSQL("CREATE TABLE SearchHis(_id INTEGER PRIMARY KEY AUTOINCREMENT, Time INTEGER, Content TEXT)");
        //this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertHis(final ContentValues cv) {
        final SQLiteDatabase db = getWritableDatabase();
        if (db.update("SearchHis", cv, "Content = ?", new String[]{cv.getAsString("Content")}) == 0)
            db.insert("SearchHis", null, cv);
    }

    public void deleteHis(final String str) {
        getWritableDatabase().delete("SearchHis", "Content = ?", new String[]{str});
    }

    public Cursor query()
    {
        SQLiteDatabase tmp = getReadableDatabase();
        Cursor ret = tmp.query("SearchHis", new String[]{"_id", "Content"}, null, null, null, null, "Time DESC");
        return ret;
    }

    public SQLiteDatabase getDb() {return db;}
}


