package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by 03543 on 2017/1/23.
 */

public class WatchOperator {
    public WatchDatabase mWatchDatabase;
    private ActivityMain mActivity;
    private List<WatchContact.User> mRequestToList;
    private List<WatchContact.User> mRequestFromList;
    private List<WatchActivity> mWatchActivityList;

    WatchOperator(Context context) {
        mActivity = (ActivityMain) context;
        mWatchDatabase = new WatchDatabase(context);
        mRequestToList = new ArrayList<>();
        mRequestFromList = new ArrayList<>();
        mWatchActivityList = new ArrayList<>();
    }

    void setRequestList(List<WatchContact.User> to, List<WatchContact.User> from) {
        mRequestToList = to;
        mRequestFromList = from;
    }

    void setActivityList(List<WatchActivity> list) {
        mWatchActivityList = list;
        /*
        Log.d("swing", "download " + mWatchActivityList.size());
        for (WatchActivity act : mWatchActivityList) {
            Log.d("swing", "\tTime("+act.mTimestamp+") " + WatchOperator.getDefaultTimeString(act.mTimestamp) + " In " + act.mIndoor.mSteps + " Out " + act.mOutdoor.mSteps);
        }
        */
    }

    interface finishListener {
        void onFinish(Object arg);
        void onFailed(String Command, int statusCode);
    }

    //-------------------------------------------------------------------------
    public void resumeSync(WatchOperator.finishListener listener, String email, String password) {
        new WatchOperatorResumeSync(mActivity).start(listener, email, password);
    }

    public void replyToSubHost(WatchOperator.finishListener listener, int subHostId, List<Integer> kidsId) {
        new WatchOperatorReplyToSubHost(mActivity).start(listener, subHostId, kidsId);
    }

    public void requestToSubHost(WatchOperator.finishListener listener, String email) {
        new WatchOperatorRequestToSubHost(mActivity).start(listener, email);
    }

    public void setKid(WatchOperator.finishListener listener, String name, String macId, Bitmap avatar) {
        Log.d("OPERATOR", name + " macId " + macId + " ");
        new WatchOperatorSetKid(mActivity).start(listener, name, macId, avatar);
    }

    public void setKid(WatchOperator.finishListener listener, int id, String name, Bitmap avatar) {
        Log.d("OPERATOR", name + " Id " + id + " ");

        new WatchOperatorSetKid(mActivity).start(listener, id, name, avatar);
    }

    public void deleteKid(WatchOperator.finishListener listener, int id) {
        new WatchOperatorDeleteKid(mActivity).start(listener, id);
    }

    public void signUp(WatchOperator.finishListener listener, String email, String password, String firstName, String lastName, String phone, String zip, Bitmap avatar) {
        new WatchOperatorSignUp(mActivity).start(listener, email, password, firstName, lastName, phone, zip, avatar);
    }
    //-------------------------------------------------------------------------

    void ResetDatabase() {
        mWatchDatabase.ResetDatabase();
    }

    void setUser(WatchContact.User user) {
        WatchContact.User src = mWatchDatabase.UserGet();
        if (src == null)
            mWatchDatabase.UserAdd(user);
        else
            mWatchDatabase.UserUpdate(user);
    }

    WatchContact.User getUser() {
        WatchContact.User src = mWatchDatabase.UserGet();

        if (src != null) {
            src.mLabel = src.mFirstName + " " + src.mLastName;
            if (!src.mProfile.equals(""))
                src.mPhoto = loadAvatar(src.mProfile);
        }
        return src;
    }

    void clearKids() {
        mWatchDatabase.KidClear();
    }

    void setFocusKid(WatchContact.Kid kid) {
        mWatchDatabase.KidSetFocus(kid);
    }

    WatchContact.Kid getFocusKid() {
        WatchContact.Kid kid = mWatchDatabase.KidGetFocus();
        if (kid == null) {
            List<WatchContact.Kid> kids = mWatchDatabase.KidGet();
            if (!kids.isEmpty()) {
                kid = kids.get(0);
                mWatchDatabase.KidSetFocus(kid);
            }
        }

        if (kid != null) {
            kid.mBound = true;
            kid.mLabel = kid.mName;
            kid.mPhoto = kid.mProfile.equals("") ? null : loadAvatar(kid.mProfile);
        }
        return kid;
    }

    List<WatchContact.Kid> getKids() {
        List<WatchContact.Kid> kids = mWatchDatabase.KidGet();
        for (WatchContact.Kid kid : kids) {
            kid.mBound = true;
            kid.mLabel = kid.mName;
            kid.mPhoto = kid.mProfile.equals("") ? null : loadAvatar(kid.mProfile);
        }

        return kids;
    }

    void deleteKids(List<WatchContact.Kid> kids) {
        for (WatchContact.Kid kid : kids) {
            mWatchDatabase.KidDelete(kid.mId);
        }
    }

    WatchContact.Kid getKid(int id) {
        List<WatchContact.Kid> kids = getKids();
        for (WatchContact.Kid kid : kids) {
            if (kid.mId == id)
                return kid;
        }

        return null;
    }

    List<WatchEvent> getEventsForSync(WatchContact.Kid kid) {
        Calendar cal = Calendar.getInstance();
        long startTimeStamp = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, 2);
        long endTimeStamp = cal.getTimeInMillis();
        List<WatchEvent> list = mWatchDatabase.EventGet(startTimeStamp, endTimeStamp);
        List<WatchEvent> rtn = new ArrayList<>();
        for (WatchEvent watchEvent : list) {
            if (watchEvent.containsKid(kid.mId))
                rtn.add(watchEvent);
        }

        return rtn;
    }

    void pushUploadItem(WatchActivityRaw uploadItem) {
        mWatchDatabase.UploadItemAdd(uploadItem);
    }

    static long getTimeStamp(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = format.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            date = null;
        }
        if (date == null)
            return 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.getTimeInMillis();
    }

    static String getTimeString(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        date.setTime(timeStamp);
        return format.format(date);
    }

    static String getDefaultTimeString(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date date = new Date();
        date.setTime(timeStamp);
        return format.format(date);
    }

    public ArrayList<WatchContact.Kid> getDeviceList() {
        WatchContact.User user = getUser();
        List<WatchContact.Kid> src = getKids();
        ArrayList<WatchContact.Kid> rtn = new ArrayList<>();

        for (WatchContact.Kid kid : src) {
            if (kid.mUserId == user.mId)
                rtn.add(kid);
        }
        return rtn;
    }

    public ArrayList<WatchContact.Kid> getSharedList() {
        WatchContact.User user = getUser();
        List<WatchContact.Kid> src = getKids();
        ArrayList<WatchContact.Kid> rtn = new ArrayList<>();

        for (WatchContact.Kid kid : src) {
            if (kid.mUserId != user.mId)
                rtn.add(kid);
        }
        return rtn;
    }

    public List<WatchContact.User> getRequestFromList() {
        for (WatchContact.User user : mRequestFromList) {
            if (user.mPhoto == null && !user.mProfile.equals("")) {
                user.mPhoto = loadAvatar(user.mProfile);
            }
        }

        return mRequestFromList;
    }

    public List<WatchContact.User> getRequestToList() {
        for (WatchContact.User user : mRequestToList) {
            if (user.mPhoto == null && !user.mProfile.equals(""))
                user.mPhoto = loadAvatar(user.mProfile);
        }

        return mRequestToList;
    }

    public List<WatchEvent> getEventList(long start, long end) {
        List<WatchEvent> list = mWatchDatabase.EventGet(start, end);
        // Test
        if (false) {
            list.add(new WatchEvent(0, getUser().mId, "Name",
                    2017, 2, 18, 8, 30, 2017, 2, 18, 9, 10, WatchEvent.ColorList[0],
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);
            list.get(list.size() - 1).mTodoList = Arrays.asList(
                    new WatchTodo(1, getUser().mId, 0, "1 Todo todo todo todo", WatchTodo.STATUS_DONE),
                    new WatchTodo(2, getUser().mId, 0, "2 Todo todo todo todo", WatchTodo.STATUS_PENDING)
                    );

            list.add(new WatchEvent(0, getUser().mId, "Name",
                    2017, 2, 18, 10, 0, 2017, 2, 18, 10, 50, WatchEvent.ColorList[1],
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);

            list.add(new WatchEvent(0, getUser().mId, "Name",
                    2017, 2, 18, 8, 30, 2017, 2, 18, 11, 30, WatchEvent.ColorList[2],
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);

            list.add(new WatchEvent(0, getUser().mId, "Name",
                    2017, 2, 18, 10, 30, 2017, 2, 18, 11, 10, WatchEvent.ColorList[3],
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);
        }
        //
        return list;
    }

    public WatchEvent getEvent(int id) {
        return mWatchDatabase.EventGet(id);
    }

    public void setEvent(WatchOperator.finishListener listener, WatchEvent event) {
        new WatchOperatorSetEvent(mActivity).start(listener, event);
    }

    public void deleteEvent(WatchOperator.finishListener listener, int eventId) {
        new WatchOperatorDeleteEvent(mActivity).start(listener, eventId);
    }

    private Bitmap loadAvatar(String filename) {
        File file = new File(ServerMachine.GetAvatarFilePath() + "/" + filename);
        if (file.exists())
            return BitmapFactory.decodeFile(ServerMachine.GetAvatarFilePath() + "/" + filename);

        return null;
    }

    public void todoDone(WatchOperator.finishListener listener, List<WatchTodo> todos) {
        new WatchOperatorTodoDone(mActivity).start(listener, todos);
    }

    public void updateActivity(WatchOperator.finishListener listener, int kid) {
        new WatchOperatorUpdateActivity(mActivity).start(listener, kid);
    }

    public WatchActivity getActivityOfDay() {
        if(mWatchActivityList.isEmpty()) {
            return new WatchActivity();
        }

        return mWatchActivityList.get(0);
    }

    public List<WatchActivity> getActivityOfWeek() {
        List<WatchActivity> rtn = new ArrayList<>();

        for (int idx = 0; idx < 7; idx++) {
            rtn.add(mWatchActivityList.get(idx));
        }
        Collections.reverse(rtn);

        return rtn;
    }

    public List<WatchActivity> getActivityOfMonth() {
        List<WatchActivity> rtn = new ArrayList<>();

        for (int idx = 0; idx < 30; idx++) {
            rtn.add(mWatchActivityList.get(idx));
        }
        Collections.reverse(rtn);

        return rtn;
    }

    public List<WatchActivity> getActivityOfYear() {
        List<WatchActivity> rtn = new ArrayList<>();
        long startTimestamp;
        long endTimestamp;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.SECOND, -1);
        cal.add(Calendar.MONTH, 2);
        endTimestamp = cal.getTimeInMillis();

        cal.add(Calendar.SECOND, 1);
        cal.add(Calendar.MONTH, -1);
        startTimestamp = cal.getTimeInMillis();

        for (int idx = 0; idx < 12; idx++) {
            //Log.d("swing", "Start Time("+startTimestamp+") " + WatchOperator.getDefaultTimeString(startTimestamp));
            //Log.d("swing", "End Time("+endTimestamp+") " + WatchOperator.getDefaultTimeString(endTimestamp));
            WatchActivity watchActivity = new WatchActivity(0, startTimestamp);

            for (WatchActivity src : mWatchActivityList)
                watchActivity.addInTimeRange(src, startTimestamp, endTimestamp);
            rtn.add(watchActivity);

            cal.setTimeInMillis(startTimestamp);
            cal.add(Calendar.MONTH, 1);
            startTimestamp = cal.getTimeInMillis();

            cal.setTimeInMillis(endTimestamp);
            cal.add(Calendar.MONTH, 1);
            endTimestamp = cal.getTimeInMillis();
        }
        return rtn;
    }
}
