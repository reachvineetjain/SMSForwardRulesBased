package com.nehvin.smsforwardrulesbased;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;

/**
 * Created by Neha on 11-Sep-17.
 */

public class SenderDetailAdapter extends ArrayAdapter<SMSDetails> {

    Context context;

    public SenderDetailAdapter(@NonNull Context context, ArrayList<SMSDetails> senderDetails) {
        super(context, 0, senderDetails);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null)
        {
            listItemView = LayoutInflater.from(context).inflate(R.layout.sender_details, parent, false);
        }

        SMSDetails senderDetails = getItem(position);
        assert senderDetails != null;

        CheckBox cbk = (CheckBox)listItemView.findViewById(R.id.sender_name);
        cbk.setText(senderDetails.getSender_details());

        if("0".equalsIgnoreCase(senderDetails.getBlocked())) {
            cbk.setChecked(false);
        }
        else {
            cbk.setChecked(true);
        }

        return listItemView;
    }
}