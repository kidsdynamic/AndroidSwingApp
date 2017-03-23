package com.kidsdynamic.swing.androidswingapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by weichigio on 2017/2/16.
 */

public class WatchOperatorSetEvent {
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private WatchOperator.finishListener mListener = null;
    private int mTimeZoneOffset;

    WatchOperatorSetEvent(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
    }

    public void start(WatchOperator.finishListener listener, WatchEvent event) {

        Calendar now = Calendar.getInstance();
        int offset = now.getTimeZone().getOffset(now.getTimeInMillis());
        mTimeZoneOffset = offset / 60 / 1000;

        mListener = listener;
        List<String> todos = new ArrayList<>();
        for (WatchTodo todo : event.mTodoList)
            todos.add(todo.mText);

        if (event.mId == 0) {
            mServerMachine.eventAdd(
                    mEventAddListener,
                    event.mKids,
                    event.mName,
                    WatchOperator.getLocalTimeString(event.mStartDate), // #032217-2 Local
                    WatchOperator.getLocalTimeString(event.mEndDate), // #032217-2 Local
                    event.mColor,
                    event.mDescription,
                    event.mAlert,
                    event.mRepeat,
                    mTimeZoneOffset,
                    todos);
        } else {
            mServerMachine.eventUpdate(
                    mEventUpdateListener,
                    event.mId,
                    event.mName,
                    WatchOperator.getLocalTimeString(event.mStartDate), // #032217-2 Local
                    WatchOperator.getLocalTimeString(event.mEndDate), // #032217-2 Local
                    event.mColor,
                    event.mDescription,
                    event.mAlert,
                    event.mRepeat,
                    mTimeZoneOffset,
                    todos);
        }
    }

    ServerMachine.eventAddListener mEventAddListener = new ServerMachine.eventAddListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.event.add.response response) {
            ServerGson.eventData eventData = response.event;
            WatchEvent watchEvent = new WatchEvent();

            watchEvent.mId = eventData.id;
            watchEvent.mUserId = eventData.user.id;
            for (ServerGson.kidData kidData : eventData.kid)
                watchEvent.mKids.add(kidData.id);
            watchEvent.mName = eventData.name;
            watchEvent.mStartDate = WatchOperator.getLocalTimeStamp(eventData.startDate); // #032217-2 Local
            watchEvent.mEndDate = WatchOperator.getLocalTimeStamp(eventData.endDate); // #032217-2 Local
            watchEvent.mColor = eventData.color;
            watchEvent.mStatus = eventData.status;
            watchEvent.mDescription = eventData.description;
            watchEvent.mAlert = eventData.alert;
            watchEvent.mRepeat = eventData.repeat;
            watchEvent.mTimezoneOffset = eventData.timezoneOffset;
            watchEvent.mDateCreated = WatchOperator.getTimeStamp(eventData.dateCreated);
            watchEvent.mLastUpdated = WatchOperator.getTimeStamp(eventData.lastUpdated);
            if (eventData.todo != null) {
                for (ServerGson.todoData todoData : eventData.todo) {
                    watchEvent.mTodoList.add(
                            new WatchTodo(
                                    todoData.id,
                                    eventData.user.id,
                                    eventData.id,
                                    todoData.text,
                                    todoData.status,
                                    WatchOperator.getTimeStamp(todoData.dateCreated),
                                    WatchOperator.getTimeStamp(todoData.lastUpdated)
                            )
                    );
                }
            }
            mOperator.mWatchDatabase.EventAdd(watchEvent);
            if (mListener != null)
                mListener.onFinish(null);
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (mListener != null)
                mListener.onFailed(mServerMachine.getErrorMessage(command, statusCode), statusCode);
        }
    };

    ServerMachine.eventUpdateListener mEventUpdateListener = new ServerMachine.eventUpdateListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.event.update.response response) {
            ServerGson.eventData eventData = response.event;
            WatchEvent watchEvent = new WatchEvent();

            watchEvent.mId = eventData.id;
            watchEvent.mUserId = eventData.user.id;
            for (ServerGson.kidData kidData : eventData.kid)
                watchEvent.mKids.add(kidData.id);
            watchEvent.mName = eventData.name;
            watchEvent.mStartDate = WatchOperator.getLocalTimeStamp(eventData.startDate);
            watchEvent.mEndDate = WatchOperator.getLocalTimeStamp(eventData.endDate);
            watchEvent.mColor = eventData.color;
            watchEvent.mStatus = eventData.status;
            watchEvent.mDescription = eventData.description;
            watchEvent.mAlert = eventData.alert;
            watchEvent.mRepeat = eventData.repeat;
            watchEvent.mTimezoneOffset = eventData.timezoneOffset;
            watchEvent.mDateCreated = WatchOperator.getTimeStamp(eventData.dateCreated);
            watchEvent.mLastUpdated = WatchOperator.getTimeStamp(eventData.lastUpdated);
            if (eventData.todo != null) {
                for (ServerGson.todoData todoData : eventData.todo) {
                    watchEvent.mTodoList.add(
                            new WatchTodo(
                                    todoData.id,
                                    eventData.user.id,
                                    eventData.id,
                                    todoData.text,
                                    todoData.status,
                                    WatchOperator.getTimeStamp(todoData.dateCreated),
                                    WatchOperator.getTimeStamp(todoData.lastUpdated)
                            )
                    );
                }
            }
            mOperator.mWatchDatabase.EventUpdate(watchEvent);
            if (mListener != null)
                mListener.onFinish(null);
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (mListener != null)
                mListener.onFailed(mServerMachine.getErrorMessage(command, statusCode), statusCode);
        }
    };

}
