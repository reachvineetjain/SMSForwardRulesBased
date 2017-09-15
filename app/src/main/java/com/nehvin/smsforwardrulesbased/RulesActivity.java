package com.nehvin.smsforwardrulesbased;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nehvin.smsforwardrulesbased.data.MessageSenderContract;
import com.nehvin.smsforwardrulesbased.data.MessageSenderDBHelper;

import java.util.ArrayList;
import java.util.Hashtable;

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

        senderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(TAG, "onItemClick: adapterView.toString() : "+ adapterView.toString());
                Log.i(TAG, "onItemClick: view.toString() : "+view.toString());
                Log.i(TAG, "onItemClick: position : " + position);
                Log.i(TAG, "onItemClick: id :" + id);
                Toast.makeText(RulesActivity.this, "adapterView.toString() :"+adapterView.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        senderListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(TAG, "onItemClick: adapterView.toString() : "+ adapterView.toString());
                Log.i(TAG, "onItemClick: view.toString() : "+view.toString());
                Log.i(TAG, "onItemClick: position : " + position);
                Log.i(TAG, "onItemClick: id :" + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(TAG, "onItemClick: adapterView.toString() : "+ adapterView.toString());
            }
        });



// The below gives an exception and not to be used with ListView
//        senderListView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i(TAG, "onClick: view.toString() : "+view.toString());
//            }
//        });

    }

    public ArrayList<SMSDetails> getSenderDetails (){

        ArrayList<SMSDetails> senderDetails = new ArrayList<>();

        Cursor cursor = mDb.query(MessageSenderContract.MessageSenderEntry.TABLE_NAME,
                new String[] {
                        MessageSenderContract.MessageSenderEntry._ID,
                        MessageSenderContract.MessageSenderEntry.COLUMN_SENDER_DETAILS,
                        MessageSenderContract.MessageSenderEntry.COLUMN_BLOCKED
                },
                null, null, null, null, null);

        int senderDetailIndex;
        int senderBlockedIndex;
        int idIndex;

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            idIndex = cursor.getColumnIndex(MessageSenderContract.MessageSenderEntry._ID);
            senderDetailIndex = cursor.getColumnIndex(MessageSenderContract.MessageSenderEntry.COLUMN_SENDER_DETAILS);
            senderBlockedIndex = cursor.getColumnIndex(MessageSenderContract.MessageSenderEntry.COLUMN_BLOCKED);

            do{
                senderDetails.add(new SMSDetails(
                        cursor.getString(idIndex),null, null, cursor.getString(senderDetailIndex), cursor.getString(senderBlockedIndex), null));
            }while(cursor.moveToNext());
        }

        return senderDetails;
    }


    public void saveRules(View view) {

        Log.i(TAG, "saveRules: "+selectedSenderMessageList.toString());
    }
}
