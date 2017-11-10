package com.nehvin.smsforwardrulesbased;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;

import static com.nehvin.smsforwardrulesbased.RulesActivity.selectedSenderMessageList;

/**
 * Created by Neha on 11-Sep-17.
 */

public class SenderDetailAdapter extends ArrayAdapter<SMSDetails> {

    Context context;
    public static final String TAG = SenderDetailAdapter.class.getSimpleName();

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

        cbk.setTag(senderDetails);

        if(senderDetails.getSender_details().equalsIgnoreCase(senderDetails.getSender()))
            cbk.setText(senderDetails.getSender_details());
        else
            cbk.setText(senderDetails.getSender_details()+ "("+senderDetails.getSender()+")");

        if("0".equalsIgnoreCase(senderDetails.getBlocked())) {
            cbk.setChecked(false);
        }
        else {
            cbk.setChecked(true);
        }

        cbk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMSDetails senderDetail =  (SMSDetails) view.getTag();

                if(((CheckBox)view).isChecked())
                    senderDetail.setBlocked("1");
                else
                    senderDetail.setBlocked("0");

                if(selectedSenderMessageList != null &&
                        selectedSenderMessageList.size() > 0 &&
                        selectedSenderMessageList.containsKey(senderDetail.getId()))
                {
                    selectedSenderMessageList.remove(senderDetail.getId());
                    selectedSenderMessageList.put(senderDetail.getId(), senderDetail);
                }
                else
                {
                    selectedSenderMessageList.put(senderDetail.getId(), senderDetail);
                }
            }
        });


        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onItemClick: view.toString() : "+view.toString());
            }
        });

        return listItemView;
    }


}