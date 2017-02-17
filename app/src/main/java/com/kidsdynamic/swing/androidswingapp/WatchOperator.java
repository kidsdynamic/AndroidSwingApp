package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

    WatchOperator(Context context) {
        mActivity = (ActivityMain) context;
        mWatchDatabase = new WatchDatabase(context);
        mRequestToList = new ArrayList<>();
        mRequestFromList = new ArrayList<>();
    }

    public void setRequestList(List<WatchContact.User> to, List<WatchContact.User> from) {
        mRequestToList = to;
        mRequestFromList = from;
    }

    //-------------------------------------------------------------------------
    public void resumeSync(WatchOperatorResumeSync.finishListener listener, String email, String password) {
        new WatchOperatorResumeSync(mActivity).start(listener, email, password);
    }

    public void replyToSubHost(WatchOperatorReplyToSubHost.finishListener listener, int subHostId, List<Integer> kidsId) {
        new WatchOperatorReplyToSubHost(mActivity).start(listener, subHostId, kidsId);
    }

    public void requestToSubHost(WatchOperatorRequestToSubHost.finishListener listener, String email) {
        new WatchOperatorRequestToSubHost(mActivity).start(listener, email);
    }

    public void setKid(WatchOperatorSetKid.finishListener listener, String name, String macId, Bitmap avatar) {
        new WatchOperatorSetKid(mActivity).start(listener, name, macId, avatar);
    }

    public void setKid(WatchOperatorSetKid.finishListener listener, int id, String name, Bitmap avatar) {
        new WatchOperatorSetKid(mActivity).start(listener, id, name, avatar);
    }

    public void deleteKid(WatchOperatorDeleteKid.finishListener listener, int id) {
        new WatchOperatorDeleteKid(mActivity).start(listener, id);
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

    /*
        void setKid(WatchContact.Kid kid) {
            WatchContact.Kid src = mWatchDatabase.KidGet(kid);
            if (src == null)
                mWatchDatabase.KidAdd(kid);
            else
                mWatchDatabase.KidUpdate(kid);

        }
    */
    void setFocusKid(WatchContact.Kid kid) {
        mWatchDatabase.KidSetFocus(kid);
    }

    WatchContact.Kid getFocusKid() {
        WatchContact.Kid kid = mWatchDatabase.KidGetFocus();
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

    void pushUploadItem(String macId, byte[] time, byte[] outdoor, byte[] indoor) {
        WatchDatabase.Upload uploadItem = new WatchDatabase.Upload();

        uploadItem.mMacId = macId;
        uploadItem.mTime = byteToDec(time[0], time[1], time[2], time[3]);
        uploadItem.mOutdoorActivity = rawString(outdoor);
        uploadItem.mIndoorActivity = rawString(indoor);

        mWatchDatabase.UploadItemAdd(uploadItem);
    }

    private String rawString(byte[] b) {
        return String.format(Locale.getDefault(), "%s,%d,%s,%s,%s,%s",
                byteToStr(b[0], b[1], b[2], b[3]),
                b[4],
                byteToStr(b[5], b[6], b[7], b[8]),
                byteToStr(b[9], b[10], b[11], b[12]),
                byteToStr(b[13], b[14], b[15], b[16]),
                byteToStr(b[17], b[18], b[19], b[20])
        );
    }

    private int byteToDec(byte b0, byte b1, byte b2, byte b3) {
        int dec;

        dec = b0 & 0xFF;
        dec |= (b1 << 8) & 0xFF00;
        dec |= (b2 << 16) & 0xFF0000;
        dec |= (b3 << 24) & 0xFF000000;

        return dec;
    }

    private String byteToStr(byte b0, byte b1, byte b2, byte b3) {
        return "" + byteToDec(b0, b1, b2, b3);
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
                    2017, 2, 18, 8, 30, 2017, 2, 18, 9, 10, WatchEvent.StockColorList[0].mColor,
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.NoticeAlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);

            list.add(new WatchEvent(0, getUser().mId, "Name",
                    2017, 2, 18, 10, 0, 2017, 2, 18, 10, 50, WatchEvent.StockColorList[1].mColor,
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.NoticeAlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);

            list.add(new WatchEvent(0, getUser().mId, "Name",
                    2017, 2, 18, 8, 30, 2017, 2, 18, 11, 30, WatchEvent.StockColorList[2].mColor,
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.NoticeAlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);

            list.add(new WatchEvent(0, getUser().mId, "Name",
                    2017, 2, 18, 10, 30, 2017, 2, 18, 11, 10, WatchEvent.StockColorList[3].mColor,
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.NoticeAlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);
        }
        //
        return list;
    }

    public WatchEvent getEvent(int id) {
        return mWatchDatabase.EventGet(id);
    }

    public boolean setEvent(WatchOperatorSetEvent.finishListener listener, WatchEvent event) {
        new WatchOperatorSetEvent(mActivity).start(listener, event);
        return true;
    }

    public void deleteEvent(WatchOperatorDeleteEvent.finishListener listener, int eventId) {
        new WatchOperatorDeleteEvent(mActivity).start(listener, eventId);
    }

    private Bitmap loadAvatar(String filename) {
        File file = new File(ServerMachine.GetAvatarFilePath() + "/" + filename);
        if (file.exists())
            return BitmapFactory.decodeFile(ServerMachine.GetAvatarFilePath() + "/" + filename);

        return null;
    }
}
