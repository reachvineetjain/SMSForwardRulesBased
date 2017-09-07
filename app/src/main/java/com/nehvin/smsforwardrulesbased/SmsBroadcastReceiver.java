package com.nehvin.smsforwardrulesbased;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            StringBuilder smsMessageStr = new StringBuilder();
            SMSDetails smsDetails = null;
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();
                long timeMillis = smsMessage.getTimestampMillis();

                Date date = new Date(timeMillis);
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String dateText = format.format(date);
                String senderPhoneBook = address;
                Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                        Uri.encode(address));
                Cursor cursor = context.getContentResolver().query(lookupUri,
                        new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},null,null,null);
                if(cursor != null && cursor.getCount() == 1 && cursor.moveToFirst())
                    senderPhoneBook = (cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));

                smsDetails = new SMSDetails(address,senderPhoneBook,smsBody,dateText);
//                smsMessageStr.append(senderPhoneBook);
//                smsMessageStr.append(",\n");
//                smsMessageStr.append(dateText);
//                smsMessageStr.append(",\n");
//                smsMessageStr.append(smsBody);

//                smsMessageStr += senderPhoneBook +" at "+"\t"+ dateText + "\n";
//                smsMessageStr += smsBody + "\n";
            }
            Toast.makeText(context, smsMessageStr.toString(), Toast.LENGTH_SHORT).show();

            if (null != smsDetails) {
                //this will update the UI with message
                ReceiveSmsActivity inst = ReceiveSmsActivity.instance();
//            inst.updateList(smsMessageStr.toString());
                inst.updateList(smsDetails);
            }
        }
    }
}