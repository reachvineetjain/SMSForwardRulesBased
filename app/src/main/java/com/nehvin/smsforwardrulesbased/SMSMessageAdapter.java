package com.nehvin.smsforwardrulesbased;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Neha on 9/8/2017.
 */

public class SMSMessageAdapter extends ArrayAdapter<SMSDetails> {


    Context context;

    public SMSMessageAdapter(@NonNull Context context, ArrayList<SMSDetails> smsDetails) {
        super(context, 0, smsDetails);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null)
        {
            listItemView = LayoutInflater.from(context).inflate(R.layout.sms_detail_text, parent, false);
        }

        SMSDetails smsDetails = getItem(position);
        assert smsDetails != null;

        TextView tv_from = (TextView) listItemView.findViewById(R.id.from);
        tv_from.setText(smsDetails.getSender_details());

        TextView tv_date = (TextView)  listItemView.findViewById(R.id.datetime);
        tv_date.setText(smsDetails.getDate_inserted());

        TextView tv_message = (TextView) listItemView.findViewById(R.id.message);
        tv_message.setText(smsDetails.getMessage());

        return listItemView;
    }
}
