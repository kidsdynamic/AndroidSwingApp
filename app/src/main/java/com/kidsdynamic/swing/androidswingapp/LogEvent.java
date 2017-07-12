package com.kidsdynamic.swing.androidswingapp;

import com.google.firebase.analytics.FirebaseAnalytics;

public class LogEvent {

    public static class Event extends FirebaseAnalytics.Event {
        public static final String SWITCH_PAGE = "switch_page";
        public static final String SYNC_WATCH_DATA = "sync_watch_data";
        public static final String SYNC_BACKEND_DATA = "sync_backend_data";
        public static final String ACTION = "ACTION";
    }

    public static class UserProperty extends FirebaseAnalytics.UserProperty {
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String SIGNUP_DATE = "sign_up_date";
        public static final String KID_COUNT = "kid_count";
        public static final String MAC_ID_LIST = "mac_id";

    }
}
