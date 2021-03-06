package com.nehvin.smsforwardrulesbased;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nehvin.smsforwardrulesbased.data.MessageSenderContract;
import com.nehvin.smsforwardrulesbased.data.MessageSenderDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReceiveSmsActivity extends Activity implements OnItemClickListener {

    private static ReceiveSmsActivity inst;
    ArrayList<SMSDetails> smsMessagesList = new ArrayList<SMSDetails>();
    ListView smsListView;
    SMSMessageAdapter smsMessageAdapter;
//    ArrayList<SMSDetails> senderList = new ArrayList<>();
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


        String phoneNumber = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        Log.i(TAG, "onCreate: telephone number "+phoneNumber);
        TextView phoneNumberTV = (TextView)findViewById(R.id.dndPhoneTextView);
        phoneNumberTV.setText(phoneNumberTV.getText()+" "+ phoneNumber);
        smsMessageAdapter = new SMSMessageAdapter(getBaseContext(), smsMessagesList);
        smsListView = (ListView)findViewById(R.id.SMSList);
        smsListView.setAdapter(smsMessageAdapter);

//        smsListView = (ListView) findViewById(R.id.SMSList);
//
//        arrayAdapter = new ArrayAdapter<SMSDetails>(this, R.layout.sms_detail_text,
//                R.id.sms_details, smsMessagesList);
//        smsListView.setAdapter(arrayAdapter);

        smsListView.setOnItemClickListener(this);


        MessageSenderDBHelper dbHelper = new MessageSenderDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
//        getCurrentList();

        refreshSmsInbox();
    }


    public void refreshSmsInbox() {

        Cursor smsInboxCursor = null;
        try {
            ContentResolver contentResolver = getContentResolver();
            smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null,
                    null, null);

            if (smsInboxCursor != null ) {
                int indexBody = smsInboxCursor.getColumnIndex("body");
                int indexAddress = smsInboxCursor.getColumnIndex("address");
                int timeMillis = smsInboxCursor.getColumnIndex("date");
                SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.getDefault());
                Uri lookupUri = null;
                Cursor cursor = null;
                String address;
                String senderPhoneBook;
                String messageBody;
                String date_ins;
                if (indexBody < 0 || !smsInboxCursor.moveToFirst())
                    return;
                smsMessageAdapter.clear();
                do {
                    address = smsInboxCursor.getString(indexAddress);
                    senderPhoneBook = address;
                    messageBody = smsInboxCursor.getString(indexBody);
                    date_ins = format.format(new Date(smsInboxCursor.getLong(timeMillis)));
                    try {
                        lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                                Uri.encode(senderPhoneBook));
                        cursor = getBaseContext().getContentResolver().query(lookupUri,
                                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},null,null,null);
                        if(cursor != null && cursor.getCount() == 1 && cursor.moveToFirst())
                            senderPhoneBook = (cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }

                    SMSDetails smsDetails = null;
                    smsDetails = new SMSDetails(address, senderPhoneBook, messageBody, date_ins);

                    if ( address.equalsIgnoreCase(senderPhoneBook)) {
                        long i = updateSenderInDB(smsDetails);
                        Log.i(TAG, "refreshSmsInbox: no of records updated"+i);
                    }
                    smsMessageAdapter.add(smsDetails);

                    //            String str = senderPhoneBook
        //                    +"\n" + format.format(new Date(smsInboxCursor.getLong(timeMillis)))
        //                    +"\n" + smsInboxCursor.getString(indexBody);
                } while (smsInboxCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(smsInboxCursor != null)
                smsInboxCursor.close();
        }
    }

//    public void updateList(final String smsMessage) {
//
//        arrayAdapter.insert(smsMessage, 0);
//        arrayAdapter.notifyDataSetChanged();
//    }

    public void updateList(SMSDetails smsDetails) {

        if (smsDetails != null) {

            long i = updateSenderInDB(smsDetails);
            Log.i(TAG, "updateList: no of records updated"+i);

            smsMessageAdapter.insert(smsDetails, 0);
            smsMessageAdapter.notifyDataSetChanged();
        }
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

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            SMSDetails smsMessages = smsMessagesList.get(pos);
            String address = smsMessages.getSender_details();
            String smsMessage = smsMessages.getMessage();
//            for (int i = 1; i < smsMessages.length; ++i) {
//                smsMessage += smsMessages[i];
//            }
            String smsMessageStr = address + "\n\n";
            smsMessageStr += smsMessage;
            Toast.makeText(this, smsMessageStr, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToCompose(View view) {
        Intent intent = new Intent(ReceiveSmsActivity.this, RulesActivity.class);
        startActivity(intent);
    }
}