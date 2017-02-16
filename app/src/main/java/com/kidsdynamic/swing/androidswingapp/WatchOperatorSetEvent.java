package com.kidsdynamic.swing.androidswingapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weichigio on 2017/2/16.
 */

public class WatchOperatorSetEvent {
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private finishListener mListener = null;

    WatchOperatorSetEvent(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
    }

    interface finishListener {
        void onFinish(String msg);
    }

    public void start(finishListener listener, WatchEvent event) {
        mListener = listener;
        List<String> todos = new ArrayList<>();
        for (WatchTodo todo : event.mTodoList)
            todos.add(todo.mText);

        if (event.mId == 0) {
            mServerMachine.eventAdd(
                    mEventAddListener,
                    event.mKids,
                    event.mName,
                    WatchOperator.getTimeString(event.mStartDate),
                    WatchOperator.getTimeString(event.mEndDate),
                    event.mColor,
                    event.mDescription,
                    event.mAlert,
                    event.mCity,
                    event.mState,
                    event.mRepeat,
                    0,
                    todos);
        } else {
            mServerMachine.eventUpdate(
                    mEvemtUpdateListener,
                    event.mId,
                    event.mName,
                    WatchOperator.getTimeString(event.mStartDate),
                    WatchOperator.getTimeString(event.mEndDate),
                    event.mColor,
                    event.mDescription,
                    event.mAlert,
                    event.mCity,
                    event.mState,
                    event.mRepeat,
                    0,
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
            watchEvent.mStartDate = WatchOperator.getTimeStamp(eventData.startDate);
            watchEvent.mEndDate = WatchOperator.getTimeStamp(eventData.endDate);
            watchEvent.mColor = eventData.color;
            watchEvent.mStatus = eventData.status;
            watchEvent.mDescription = eventData.description;
            watchEvent.mAlert = eventData.alert;
            watchEvent.mCity = eventData.city;
            watchEvent.mState = eventData.state;
            watchEvent.mRepeat = eventData.repeat;
            watchEvent.mTimezoneOffset = eventData.timezoneOffset;
            watchEvent.mDateCreated = WatchOperator.getTimeStamp(eventData.dateCreated);
            watchEvent.mLastUpdated = WatchOperator.getTimeStamp(eventData.lastUpdated);
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
            mOperator.mWatchDatabase.EventAdd(watchEvent);
            if (mListener != null)
                mListener.onFinish("");
        }

        @Override
        public void onFail(int statusCode) {
            if (mListener != null)
                mListener.onFinish("Add event NG " + statusCode);
        }
    };

    ServerMachine.eventUpdateListener mEvemtUpdateListener = new ServerMachine.eventUpdateListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.event.update.response response) {
            ServerGson.eventData eventData = response.event;
            WatchEvent watchEvent = new WatchEvent();

            watchEvent.mId = eventData.id;
            watchEvent.mUserId = eventData.user.id;
            for (ServerGson.kidData kidData : eventData.kid)
                watchEvent.mKids.add(kidData.id);
            watchEvent.mName = eventData.name;
            watchEvent.mStartDate = WatchOperator.getTimeStamp(eventData.startDate);
            watchEvent.mEndDate = WatchOperator.getTimeStamp(eventData.endDate);
            watchEvent.mColor = eventData.color;
            watchEvent.mStatus = eventData.status;
            watchEvent.mDescription = eventData.description;
            watchEvent.mAlert = eventData.alert;
            watchEvent.mCity = eventData.city;
            watchEvent.mState = eventData.state;
            watchEvent.mRepeat = eventData.repeat;
            watchEvent.mTimezoneOffset = eventData.timezoneOffset;
            watchEvent.mDateCreated = WatchOperator.getTimeStamp(eventData.dateCreated);
            watchEvent.mLastUpdated = WatchOperator.getTimeStamp(eventData.lastUpdated);
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
            mOperator.mWatchDatabase.EventUpdate(watchEvent);
            if (mListener != null)
                mListener.onFinish("");
        }

        @Override
        public void onFail(int statusCode) {
            if (mListener != null)
                mListener.onFinish("Update event NG " + statusCode);
        }
    };

}