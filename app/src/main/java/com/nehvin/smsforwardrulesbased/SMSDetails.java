package com.nehvin.smsforwardrulesbased;

/**
 * Created by Neha on 9/7/2017.
 */

public class SMSDetails {

    private String id;
    private String date_inserted;
    private String sender;
    private String sender_details;
    private String blocked;
    private String message;

    public SMSDetails(String id, String date_inserted, String sender, String sender_details, String blocked, String message) {
        this.id = id;
        this.date_inserted = date_inserted;
        this.sender = sender;
        this.sender_details = sender_details;
        this.blocked = blocked;
        this.message = message;
    }

    public SMSDetails(String sender, String sender_details, String message, String date_inserted) {
        this.sender = sender;
        this.sender_details = sender_details;
        this.message = message;
        this.date_inserted = date_inserted;
    }

    public SMSDetails(String sender, String sender_details, String blocked) {
        this.sender = sender;
        this.sender_details = sender_details;
        this.blocked = blocked;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate_inserted() {
        return date_inserted;
    }

    public void setDate_inserted(String date_inserted) {
        this.date_inserted = date_inserted;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender_details() {
        return sender_details;
    }

    public void setSender_details(String sender_details) {
        this.sender_details = sender_details;
    }

    public String getBlocked() {
        return blocked;
    }

    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SMS Details{" +
                "Date :'" + date_inserted + '\'' +
                ", Sender Details :'" + sender_details + '\'' +
                ", Message :'" + message + '\'' +
                '}';
    }
}
