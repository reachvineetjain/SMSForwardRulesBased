package com.nehvin.smsforwardrulesbased.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Neha on 9/6/2017.
 */

public class MessageSenderDBHelper extends SQLiteOpenHelper {


    // The database name
    private static final String DATABASE_NAME = "messagesender.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;


    public MessageSenderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MESSAGESENDER_TABLE = "CREATE TABLE IF NOT EXISTS "  +
                MessageSenderContract.MessageSenderEntry.TABLE_NAME + " (" +
                MessageSenderContract.MessageSenderEntry._ID  + " INTEGER PRIMARY KEY , " +
                MessageSenderContract.MessageSenderEntry.COLUMN_DATE_ADDED + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP ," +
                MessageSenderContract.MessageSenderEntry.COLUMN_SENDER + " TEXT NOT NULL UNIQUE , " +
                MessageSenderContract.MessageSenderEntry.COLUMN_SENDER_DETAILS + " TEXT NOT NULL , " +
                MessageSenderContract.MessageSenderEntry.COLUMN_BLOCKED + " INTEGER NOT NULL DEFAULT 0);";

        sqLiteDatabase.execSQL(SQL_CREATE_MESSAGESENDER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MessageSenderContract.MessageSenderEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
