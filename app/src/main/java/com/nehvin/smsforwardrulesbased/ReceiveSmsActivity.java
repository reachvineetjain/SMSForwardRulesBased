package com.nehvin.smsforwardrulesbased;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nehvin.smsforwardrulesbased.data.MessageSenderContract;
import com.nehvin.smsforwardrulesbased.data.MessageSenderDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReceiveSmsActivity extends Activity implements OnItemClickListener {

    private static ReceiveSmsActivity inst;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    ArrayList<String> senderList = new ArrayList<>();
    private SQLiteDatabase mDb;
    private static final String TAG = ReceiveSmsActivity.class.getSimpleName();

    public static ReceiveSmsActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_sms);
        smsListView = (ListView) findViewById(R.id.SMSList);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener(this);
        MessageSenderDBHelper dbHelper = new MessageSenderDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
        getCurrentList();

        refreshSmsInbox();
    }


    private ArrayList<String> getCurrentList()
    {
        senderList.clear();

        Cursor cursor = mDb.query(MessageSenderContract.MessageSenderEntry.TABLE_NAME,
                new String[]{MessageSenderContract.MessageSenderEntry.COLUMN_SENDER},
                null,
                null,
                null,
                null,
                MessageSenderContract.MessageSenderEntry.COLUMN_SENDER);

        int colSenderIndex = cursor.getColumnIndex(MessageSenderContract.MessageSenderEntry.COLUMN_SENDER);


        if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
        {
            do {
                senderList.add(cursor.getString(colSenderIndex));
                Log.i(TAG, "getCurrentList: "+cursor.getString(colSenderIndex));
            }while (cursor.moveToNext());
        }

        return senderList;
    }

    private long updateSenderInDB(SMSDetails smsDetails)
    {
        ContentValues cv = new ContentValues();
        cv.put(MessageSenderContract.MessageSenderEntry.COLUMN_SENDER, smsDetails.getSender());
        cv.put(MessageSenderContract.MessageSenderEntry.COLUMN_SENDER_DETAILS,
                smsDetails.getSender_details());
        long i = 0;
        try {
            mDb.beginTransaction();
            i = mDb.insertWithOnConflict(MessageSenderContract.MessageSenderEntry.TABLE_NAME, null, cv,
                    SQLiteDatabase.CONFLICT_IGNORE);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        return i;
    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null,
                null, null);

        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int timeMillis = smsInboxCursor.getColumnIndex("date");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Uri lookupUri = null;
        Cursor cursor = null;
        if (indexBody < 0 || !smsInboxCursor.moveToFirst())
            return;
        arrayAdapter.clear();
        do {
            String address = smsInboxCursor.getString(indexAddress);
            String senderPhoneBook = address;
            lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(senderPhoneBook));
            cursor = getBaseContext().getContentResolver().query(lookupUri,
                    new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},null,null,null);
            if(cursor != null && cursor.getCount() == 1 && cursor.moveToFirst())
                senderPhoneBook = (cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));

            SMSDetails smsDetails = new SMSDetails(address, senderPhoneBook);
            long i = updateSenderInDB(smsDetails);
            Log.i(TAG, "refreshSmsInbox: no of records updated"+i);

            String str = senderPhoneBook
                    +"\n" + format.format(new Date(smsInboxCursor.getLong(timeMillis)))
                    +"\n" + smsInboxCursor.getString(indexBody);
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }

//    public void updateList(final String smsMessage) {
//
//        arrayAdapter.insert(smsMessage, 0);
//        arrayAdapter.notifyDataSetChanged();
//    }

    public void updateList(SMSDetails smsDetails) {

        if (smsDetails != null) {
            StringBuilder smsMessageStr = new StringBuilder();
            smsMessageStr.append(smsDetails.getSender_details());
            smsMessageStr.append("\n");
            smsMessageStr.append(smsDetails.getDate_inserted());
            smsMessageStr.append("\n");
            smsMessageStr.append(smsDetails.getMessage());

            long i = updateSenderInDB(smsDetails);
            Log.i(TAG, "updateList: no of records updated"+i);

            arrayAdapter.insert(smsMessageStr.toString(), 0);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String[] smsMessages = smsMessagesList.get(pos).split("\n");
            String address = smsMessages[0];
            String smsMessage = "";
            for (int i = 1; i < smsMessages.length; ++i) {
                smsMessage += smsMessages[i];
            }

            String smsMessageStr = address + "\n";
            smsMessageStr += smsMessage;
            Toast.makeText(this, smsMessageStr, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToCompose(View view) {
        Intent intent = new Intent(ReceiveSmsActivity.this, SendSmsActivity.class);
        startActivity(intent);
    }
}