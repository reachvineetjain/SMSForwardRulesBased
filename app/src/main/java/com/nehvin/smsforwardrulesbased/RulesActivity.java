package com.nehvin.smsforwardrulesbased;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.nehvin.smsforwardrulesbased.data.MessageSenderContract;
import com.nehvin.smsforwardrulesbased.data.MessageSenderDBHelper;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class RulesActivity extends AppCompatActivity {


    private static RulesActivity inst;
    ArrayList<SMSDetails> senderMessagesList = new ArrayList<SMSDetails>();
    static Hashtable<String, SMSDetails> selectedSenderMessageList = new Hashtable<>();
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
                        MessageSenderContract.MessageSenderEntry._ID,
                        MessageSenderContract.MessageSenderEntry.COLUMN_SENDER,
                        MessageSenderContract.MessageSenderEntry.COLUMN_SENDER_DETAILS,
                        MessageSenderContract.MessageSenderEntry.COLUMN_BLOCKED
                },
                null, null, null, null, null);

        int senderDetailIndex;
        int senderBlockedIndex;
        int idIndex;
        int sender;

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

            idIndex = cursor.getColumnIndex(MessageSenderContract.MessageSenderEntry._ID);
            senderDetailIndex = cursor.getColumnIndex(MessageSenderContract.MessageSenderEntry.COLUMN_SENDER_DETAILS);
            senderBlockedIndex = cursor.getColumnIndex(MessageSenderContract.MessageSenderEntry.COLUMN_BLOCKED);
            sender = cursor.getColumnIndex(MessageSenderContract.MessageSenderEntry.COLUMN_SENDER);

            do{
                senderDetails.add(new SMSDetails(
                        cursor.getString(idIndex),null, cursor.getString(sender),
                        cursor.getString(senderDetailIndex), cursor.getString(senderBlockedIndex), null));
            }while(cursor.moveToNext());
        }

        return senderDetails;
    }


    public void saveRules(View view) {

        Log.i(TAG, "saveRules: "+selectedSenderMessageList.toString());

        if(selectedSenderMessageList.size()>0) {
//            String whereArgs[] = new String[selectedSenderMessageList.size()];
            ArrayList<String> whereArgs = new ArrayList<>();
            ContentValues cv = new ContentValues(selectedSenderMessageList.size());

            Iterator it = selectedSenderMessageList.keySet().iterator();
            while (it.hasNext()) {
                String id = (String) it.next();
                SMSDetails smsDetails = (SMSDetails)selectedSenderMessageList.get(id);
                cv.put(MessageSenderContract.MessageSenderEntry.COLUMN_BLOCKED, smsDetails.getBlocked());
                whereArgs.add(smsDetails.getId());
            }

            mDb.update(MessageSenderContract.MessageSenderEntry.TABLE_NAME, cv, "_id=?", whereArgs.toArray(new String[0]));
        }
        selectedSenderMessageList.clear();
    }
}