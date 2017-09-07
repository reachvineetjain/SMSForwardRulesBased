package com.nehvin.smsforwardrulesbased.data;

import android.provider.BaseColumns;

/**
 * Created by Neha on 8/31/2017.
 */

public class MessageSenderContract {

//    // The authority, which is how your code knows which Content Provider to access
//    public static final String AUTHORITY = "com.nehvin.smsforwardrulesbased";
//
//    // The base content URI = "content://" + <authority>
//    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
//
    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_MESSAGESENDER = "messagesender";

    public static final class MessageSenderEntry implements BaseColumns{

//        public static final Uri CONTENT_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MESSAGESENDER).build();
//
        // Task table and column names
        public static final String TABLE_NAME = PATH_MESSAGESENDER;

        // Since MessageSenderEntry implements the interface "BaseColumns", it has automatically added
        // "_ID" column in addition to the below columns
        public static final String COLUMN_DATE_ADDED = "date_inserted";  // date when record is inserted
        public static final String COLUMN_SENDER = "sender"; // details as per sms
        public static final String COLUMN_SENDER_DETAILS = "sender_details"; // details pulled from phonebok
        public static final String COLUMN_BLOCKED = "blocked"; // if blocked or unblocked
    }
}
