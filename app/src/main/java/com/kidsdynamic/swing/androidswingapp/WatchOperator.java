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
    //private List<WatchActivity> mWatchActivityList;

    WatchOperator(Context context) {
        mActivity = (ActivityMain) context;
        mWatchDatabase = new WatchDatabase(context);
        mRequestToList = new ArrayList<>();
        mRequestFromList = new ArrayList<>();
        //mWatchActivityList = new ArrayList<>();
    }

    void setRequestList(List<WatchContact.User> to, List<WatchContact.User> from) {
        mRequestToList = to;
        mRequestFromList = from;
    }

    void setActivityList(int kidId, List<WatchActivity> list) {
        mWatchDatabase.activityDeleteByKidId(kidId);
        mWatchDatabase.activityImport(list);
        mWatchDatabase.UploadItemRemoveDone();
        //mWatchActivityList = list;
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

    public void replyToSubHost(WatchOperator.finishListener listener, int subHostId, List<Integer> accepts, List<Integer> removes) {
        new WatchOperatorReplyToSubHost(mActivity).start(listener, subHostId, accepts, removes);
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
        cal.add(Calendar.MONTH, 1);
        long endTimeStamp = cal.getTimeInMillis();

        List<WatchEvent> list = mWatchDatabase.EventGet(startTimeStamp, endTimeStamp);
        List<WatchEvent> rtn = new ArrayList<>();
        Log.d("Sync", "!!!!!!!!! ignore kid !!!!!!!! " + startTimeStamp);
        for (WatchEvent watchEvent : list) {
            //if (watchEvent.containsKid(kid.mId))
            //    rtn.add(watchEvent);

            if (watchEvent.mAlertTimeStamp > startTimeStamp)//watchEvent.containsKid(kid.mId)
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

    static long getLocalTimeStamp(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
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

    static String getUtcTimeString(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        date.setTime(timeStamp);
        return format.format(date);
    }

    static String getLocalTimeString(long timeStamp) {
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

    private class bitmapCache {
        Bitmap mBitmap;
        String mFilename;

        bitmapCache(String filename) {
            mFilename = filename;
            decode();
        }

        void decode() {
            File file = new File(ServerMachine.GetAvatarFilePath() + "/" + mFilename);
            if (file.exists()) {
                Log.d("WatchOperator", "Load bitmap " + file.getAbsolutePath());
                mBitmap = BitmapFactory.decodeFile(ServerMachine.GetAvatarFilePath() + "/" + mFilename);
            } else {
                mBitmap = null;
            }
        }
    }

    private ArrayList<bitmapCache> mBitmapCacheList = new ArrayList<>();

    public void ResetBitmapCache() {
        for (bitmapCache bc : mBitmapCacheList) {
            if (bc.mBitmap != null)
                bc.mBitmap.recycle();
        }
        mBitmapCacheList = new ArrayList<>();
    }

    private Bitmap loadAvatar(String filename) {
        for (bitmapCache bc : mBitmapCacheList) {
            if (bc.mFilename.equals(filename)) {
                if (bc.mBitmap == null)
                    bc.decode();
                return bc.mBitmap;
            }
        }
        bitmapCache bc = new bitmapCache(filename);
        mBitmapCacheList.add(bc);

        return bc.mBitmap;
    }

    public void todoDone(WatchOperator.finishListener listener, List<WatchTodo> todos) {
        new WatchOperatorTodoDone(mActivity).start(listener, todos);
    }

    public void updateActivity(WatchOperator.finishListener listener, int kid) {
        new WatchOperatorUpdateActivity(mActivity).start(listener, kid);
    }

    public List<WatchActivity> loadActivityWithLocal(WatchContact.Kid kid) {
        List<WatchActivity> rtn = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long end = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        cal.add(Calendar.SECOND, 1);
        long start = cal.getTimeInMillis();

        start = (start / 1000) * 1000;
        end = (end / 1000) * 1000;

        while (start < end) {
            //Log.d("swing", "Start Time(" + start + ") " + WatchOperator.getDefaultTimeString(start));
            rtn.add(new WatchActivity(kid == null ? 0 : kid.mId, start));
            start += 86400000;
        }

        Collections.reverse(rtn);

        if (kid == null)
            return rtn;

        List<WatchActivity> exportList = mWatchDatabase.activityExport(kid.mId);

        for (WatchActivity exportActivity : exportList) {
            for (WatchActivity preloadActivity : rtn) {
                long actEnd = preloadActivity.mIndoor.mTimestamp + 86400000;
                if (exportActivity.mIndoor.mTimestamp >= preloadActivity.mIndoor.mTimestamp && exportActivity.mIndoor.mTimestamp < actEnd) {
                    preloadActivity.mIndoor.mSteps += exportActivity.mIndoor.mSteps;
                    preloadActivity.mOutdoor.mSteps += exportActivity.mOutdoor.mSteps;
                    break;
                }
            }
        }

        List<WatchActivityRaw> uploadList = mWatchDatabase.UploadItemGet(kid.mMacId);
        for (WatchActivityRaw raw : uploadList) {
            for (WatchActivity preloadActivity : rtn) {
                long actEnd = preloadActivity.mIndoor.mTimestamp + 86400000;
                long rawTime = raw.mTime;
                rawTime *= 1000;

                if (rawTime >= preloadActivity.mIndoor.mTimestamp && rawTime < actEnd) {
                    String[] arg = raw.mIndoor.split(",");
                    int indoor = Integer.valueOf(arg[2]);
                    arg = raw.mOutdoor.split(",");
                    int outdoor = Integer.valueOf(arg[2]);

                    preloadActivity.mIndoor.mSteps += indoor;
                    preloadActivity.mOutdoor.mSteps += outdoor;

                    break;
                }
            }
        }

        return rtn;
    }

    public WatchActivity getActivityOfDay() {
        List<WatchActivity> list = loadActivityWithLocal(getFocusKid());

        return list.get(0);
    }

    public List<WatchActivity> getActivityOfWeek() {
        List<WatchActivity> rtn = new ArrayList<>();
        List<WatchActivity> list = loadActivityWithLocal(getFocusKid());

        for (int idx = 0; idx < 7; idx++)
            rtn.add(list.get(idx));

        Collections.reverse(rtn);

        return rtn;
    }

    public List<WatchActivity> getActivityOfMonth() {
        List<WatchActivity> rtn = new ArrayList<>();
        List<WatchActivity> list = loadActivityWithLocal(getFocusKid());

        for (int idx = 0; idx < 30; idx++)
            rtn.add(list.get(idx));
        Collections.reverse(rtn);

        return rtn;
    }

    public List<WatchActivity> getActivityOfYear() {
        List<WatchActivity> rtn = new ArrayList<>();
        long startTimestamp;
        long endTimestamp;

        WatchContact.Kid kid = getFocusKid();
        List<WatchActivity> list;
        if (kid == null)
            list = new ArrayList<>();
        else
            list = mWatchDatabase.activityExport(kid.mId);

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

            for (WatchActivity src : list)
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

    public String mFocusBatteryName = "";
    public int mFocusBatteryValue = 0;
}
