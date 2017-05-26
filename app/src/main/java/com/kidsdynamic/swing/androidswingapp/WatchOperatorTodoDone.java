package com.kidsdynamic.swing.androidswingapp;

import android.util.Log;

import java.util.List;

/**
 * Created by weichigio on 2017/2/18.
 */

public class WatchOperatorTodoDone {
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private WatchOperator.finishListener mListener = null;
    private List<WatchTodo> mTodos;
    private WatchTodo mCurrentTodo;
    private int mTodoIndex = 0;

    WatchOperatorTodoDone(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
    }

    public void start(WatchOperator.finishListener listener, List<WatchTodo> todos) {
        mTodos = todos;
        mListener = listener;
        nextTodo(true);
    }

    private void nextTodo(boolean start) {
        if (start)
            mTodoIndex = 0;
        else
            mTodoIndex++;

        while (mTodoIndex < mTodos.size()) {
            mCurrentTodo = mTodos.get(mTodoIndex);
            if (mCurrentTodo.mStatus.equals(WatchTodo.STATUS_DONE))
                break;
            mCurrentTodo = null;
            mTodoIndex++;
        }

        if (mCurrentTodo == null) {
            if (mListener != null)
                mListener.onFinish(null);
        } else {
            mServerMachine.eventTodoDone(mEventTodoDoneListener, mCurrentTodo.mEventId, mCurrentTodo.mId);
        }

    }

    ServerMachine.eventTodoDoneListener mEventTodoDoneListener = new ServerMachine.eventTodoDoneListener() {
        @Override
        public void onSuccess(int statusCode) {
            mOperator.mWatchDatabase.TodoUpdate(mCurrentTodo);
            mTodoIndex++;
            mCurrentTodo = null;
            nextTodo(false);
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (mListener != null)
                mListener.onFailed(mServerMachine.getErrorMessage(command, statusCode), statusCode);
        }
    };
}