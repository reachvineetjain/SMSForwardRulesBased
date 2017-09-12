package com.nehvin.smsforwardrulesbased;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.nehvin.smsforwardrulesbased.data.MessageSenderContract;
import com.nehvin.smsforwardrulesbased.data.MessageSenderDBHelper;

import java.util.ArrayList;

public class RulesActivity extends AppCompatActivity {


    private static RulesActivity inst;
    ArrayList<SMSDetails> senderMessagesList = new ArrayList<SMSDetails>();
    ListView senderListView;
    SenderDetailAdapter senderDetailAdapter;
    private SQLiteDatabase mDb;
    private static final String TAG = RulesActivity.class.getSimpleName();

    public static RulesActivity instance()
    {
        return inst;
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        MessageSenderDBHelper dbHelper = new MessageSenderDBHelper(this);
        mDb = dbHelper.getReadableDatabase();
        senderMessagesList = getSenderDetails();

        senderDetailAdapter = new SenderDetailAdapter(getBaseContext(), senderMessagesList);
        senderListView = (ListView)findViewById(R.id.senderList);
        senderListView.setAdapter(senderDetailAdapter);

    }

    public ArrayList<SMSDetails> getSenderDetails (){

        ArrayList<SMSDetails> senderDetails = new ArrayList<>();


        Cursor cursor = mDb.query(MessageSenderContract.MessageSenderEntry.TABLE_NAME,
                new String[] {
                        MessageSenderContract.MessageSenderEntry.COLUMN_SENDER_DETAILS,
                        MessageSenderContract.MessageSenderEntry.COLUMN_BLOCKED
                },
                null, null, null, null, null);

        int senderDetailIndex;
        int senderBlockedIndex;

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            senderDetailIndex = cursor.getColumnIndex(MessageSenderContract.MessageSenderEntry.COLUMN_SENDER_DETAILS);
            senderBlockedIndex = cursor.getColumnIndex(MessageSenderContract.MessageSenderEntry.COLUMN_BLOCKED);

            do{
                senderDetails.add(new SMSDetails(null,
                        cursor.getString(senderDetailIndex),
                        cursor.getString(senderBlockedIndex)));
            }while(cursor.moveToNext());
        }

        return senderDetails;
    }

}
