package com.nehvin.smsforwardrulesbased;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsFormat = intentExtras.getString("format");
//            StringBuilder smsMessageStr = new StringBuilder();
            SMSDetails smsDetails = null;
            Cursor cursor ;
            SmsMessage smsMessage;
            for (int i = 0; i < sms.length; ++i) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], smsFormat);
                }else {
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                }

//                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();
                long timeMillis = smsMessage.getTimestampMillis();

                Date date = new Date(timeMillis);
                SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.getDefault());
                String dateText = format.format(date);
                String senderPhoneBook = address;
                cursor  = null;
                try {
                    Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                            Uri.encode(address));
                    cursor = context.getContentResolver().query(lookupUri,
                            new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},null,null,null);
                    if(cursor != null && cursor.getCount() == 1 && cursor.moveToFirst())
                        senderPhoneBook = (cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(cursor != null)
                        cursor.close();
                }

                smsDetails = new SMSDetails(address,senderPhoneBook,smsBody,dateText);
//                smsMessageStr.append(senderPhoneBook);
//                smsMessageStr.append(",\n");
//                smsMessageStr.append(dateText);
//                smsMessageStr.append(",\n");
//                smsMessageStr.append(smsBody);

//                smsMessageStr += senderPhoneBook +" at "+"\t"+ dateText + "\n";
//                smsMessageStr += smsBody + "\n";
            }
            Toast.makeText(context, smsDetails.toString(), Toast.LENGTH_SHORT).show();

            if (smsDetails != null) {
                //this will update the UI with message
                ReceiveSmsActivity inst = ReceiveSmsActivity.instance();
//            inst.updateList(smsMessageStr.toString());
                inst.updateList(smsDetails);
            }
        }
    }
}