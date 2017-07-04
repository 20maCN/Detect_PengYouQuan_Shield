package com.example.swee1.pengyouquan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by swee on 2016/11/19.
 */

public class myDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "PengYouQuan";
    private static final String TABLE_NAME = "pengyouquan";
    private static final int DB_VERSION=1;
    public myDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_NAME +
                                " (id int PRIMARY KEY,friendID TEXT,description TEXT)";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    public int numOfData() {
        SQLiteDatabase db = getWritableDatabase();
        String CREATE_TABLE = "select count(*)from " + TABLE_NAME;
        Cursor c = db.rawQuery(CREATE_TABLE,null);
        if (c==null) return 0;
        c.moveToFirst();
        return (int) c.getLong(0);
    }
    public void insert2DB(int id, String friendID, String description ) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id",id);
        cv.put("friendID",friendID);
        cv.put("description",description);
        db.insert(TABLE_NAME,null,cv);
        db.close();
    }
    public void delete() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from "+TABLE_NAME);
        db.close();
    }

    public Cursor queryAllData() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLE_NAME,new String[] {"friendID","description"},null,null,null,null,null);
        return c;
    }

    public boolean ifHasData(String friendID) {
        SQLiteDatabase db = getWritableDatabase();
        String[] sectionArgs = {friendID};
        Cursor c = db.query(TABLE_NAME,new String [] {"friendID","description"},"friendID=?",sectionArgs,null,null,null);

        if ( c.moveToFirst() == false ) {
            return false;
        }
        return true;
    }

}
