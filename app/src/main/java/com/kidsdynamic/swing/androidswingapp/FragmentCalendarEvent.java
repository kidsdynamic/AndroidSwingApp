package com.kidsdynamic.swing.androidswingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/5.
 */

public class FragmentCalendarEvent extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private View mViewUserLine;
    private TextView mViewUserName;

    private View mViewAlarmLine;
    private TextView mEventAlarmName;
    private ViewShape mViewAlarmIcon;

    private View mViewAssignLine;
    private TextView mViewAssignName;
    private ViewCircle mViewAssignPhoto;
    private View mViewAssignOption;
    private LinearLayout mViewAssignContainer;

    private View mViewStartLine;
    private TextView mViewStart;

    private View mViewEndLine;
    private TextView mViewEnd;

    private View mViewColorLine;
    private ViewShape mViewColor;
    private View mViewColorOption;
    private LinearLayout mViewColorContainer;

    private View mViewRepeatLine;
    private TextView mViewRepeat;
    private View mViewRepeatOption;
    private LinearLayout mViewRepeatContainer;

    private View mViewDescriptionLine;
    private EditText mViewDescription;

    private View mViewTodoLine;
    private View mViewTodoAdd;
    private LinearLayout mViewTodoOption;
    private LinearLayout mViewTodoContainer;

    private View mViewButtonLine;
    private View mViewSave;
    private View mViewAdvance;

    private Dialog mProcessDialog = null;

    private long mDefaultDate = System.currentTimeMillis();
    private WatchEvent mEvent;

    private Calendar mCalendarDate;
    private Boolean isStarDate = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_event, container, false);

        InputMethodManager imm = (InputMethodManager) mActivityMain.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);

        // Line Creator
        mViewUserLine = mViewMain.findViewById(R.id.calendar_event_user_line);
        mViewUserName = (TextView) mViewMain.findViewById(R.id.calendar_event_user_name);

        // Line Alarm
        mViewAlarmLine = mViewMain.findViewById(R.id.calendar_event_alarm_line);
        mViewAlarmLine.setOnClickListener(mAlarmListener);
        mEventAlarmName = (TextView) mViewMain.findViewById(R.id.calendar_event_alarm_name);
        mViewAlarmIcon = (ViewShape) mViewMain.findViewById(R.id.calendar_event_alarm_icon);

        // Line Assign
        mViewAssignLine = mViewMain.findViewById(R.id.calendar_event_assign_line);
        mViewAssignLine.setOnClickListener(mAssignListener);
        mViewAssignName = (TextView) mViewMain.findViewById(R.id.calendar_event_assign_name);
        mViewAssignPhoto = (ViewCircle) mViewMain.findViewById(R.id.calendar_event_assign_photo);
        mViewAssignOption = mViewMain.findViewById(R.id.calendar_event_assign_option);
        mViewAssignContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_event_assign_container);

        // Line Start
        mViewStartLine = mViewMain.findViewById(R.id.calendar_event_start_line);
        mViewStartLine.setOnClickListener(mStartListener);
        mViewStart = (TextView) mViewMain.findViewById(R.id.calendar_event_start);

        // Line End
        mViewEndLine = mViewMain.findViewById(R.id.calendar_event_end_line);
        mViewEndLine.setOnClickListener(mEndListener);
        mViewEnd = (TextView) mViewMain.findViewById(R.id.calendar_event_end);

        // Line Color
        mViewColorLine = mViewMain.findViewById(R.id.calendar_event_color_line);
        mViewColorLine.setOnClickListener(mColorListener);
        mViewColor = (ViewShape) mViewMain.findViewById(R.id.calendar_event_color);
        mViewColorOption = mViewMain.findViewById(R.id.calendar_event_color_option);
        mViewColorContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_event_color_container);
        for (int color : WatchEvent.ColorList)
            addColor(color);

        // Line Repeat
        mViewRepeatLine = mViewMain.findViewById(R.id.calendar_event_repeat_line);
        mViewRepeatLine.setOnClickListener(mRepeatListener);
        mViewRepeat = (TextView) mViewMain.findViewById(R.id.calendar_event_repeat);
        mViewRepeatOption = mViewMain.findViewById(R.id.calendar_event_repeat_option);
        mViewRepeatContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_event_repeat_container);

        addRepeat(WatchEvent.REPEAT_NEVER);
        addRepeat(WatchEvent.REPEAT_DAILY);
        addRepeat(WatchEvent.REPEAT_WEEKLY);
        addRepeat(WatchEvent.REPEAT_MONTHLY);

        // Line Description
        mViewDescriptionLine = mViewMain.findViewById(R.id.calendar_event_description_line);
        mViewDescription = (EditText) mViewMain.findViewById(R.id.calendar_event_description);
        mViewDescription.addTextChangedListener(mDescriptionWatcher);

        // Line To-Do
        mViewTodoLine = mViewMain.findViewById(R.id.calendar_event_todo_line);
        mViewTodoAdd = mViewMain.findViewById(R.id.calendar_event_todo_add);
        mViewTodoAdd.setOnClickListener(mTodoListener);
        mViewTodoOption = (LinearLayout) mViewMain.findViewById(R.id.calendar_event_todo_option);
        mViewTodoContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_event_todo_container);

        // Line Button
        mViewButtonLine = mViewMain.findViewById(R.id.calendar_event_button_line);
        mViewSave = mViewMain.findViewById(R.id.calendar_event_save);
        mViewSave.setOnClickListener(mSaveListener);
        mViewAdvance = mViewMain.findViewById(R.id.calendar_event_advance);
        mViewAdvance.setOnClickListener(mAdvanceListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_calendar), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_calendar, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onResume() {
        super.onResume();

        // 由帶入的參數表示事件預設的日期, 顯示在UI之上
        if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);

        // 若Stack不為空, 表示Stack中保存當前需進行編輯的事件
        // 若為空, 表示需要新增一個新的事件
        if (mActivityMain.mEventStack.isEmpty()) {
            mEvent = new WatchEvent(mDefaultDate);
            mEvent.mUserId = mActivityMain.mOperator.getUser().mId;
        } else {
            mEvent = mActivityMain.mEventStack.pop();
        }

        loadWatchEvent();
    }

    @Override
    public void onPause() {
        if (mProcessDialog != null)
            mProcessDialog.dismiss();
        super.onPause();
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    @Override
    public void onToolbarAction2() {
        mProcessDialog = ProgressDialog.show(mActivityMain, "Processing", "Please wait...", true);
        mActivityMain.mOperator.deleteEvent(mDeleteEventListener, mEvent.mId);
    }

    // 啟動或關閉事件的Enabled狀態, 若enable時, 表示事件是可以被編輯的, UI欄位為開放狀態,
    // 若disable時, 表示事件不可以被編輯, UI欄位為關閉狀態, 通常因事件的擁有者為其它人時, 需為disable狀態
    private void viewEnable(boolean enable) {
        mViewAlarmLine.setEnabled(enable);
        mViewAssignLine.setEnabled(enable);
        mViewStartLine.setEnabled(enable);
        mViewEndLine.setEnabled(enable);
        mViewColorLine.setEnabled(enable);
        mViewRepeatLine.setEnabled(enable);
        mViewDescription.setEnabled(enable);
        mViewTodoAdd.setEnabled(enable);

        int count = mViewTodoContainer.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            ViewTodo viewTodo = (ViewTodo) mViewTodoContainer.getChildAt(idx);
            if (enable)
                viewTodo.setEditMode();
            else
                viewTodo.setLockMode();
        }

        mViewAlarmIcon.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        mViewTodoAdd.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        mViewButtonLine.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    // 啟動或關閉UI中, 刪除事件的按鈕, 若事件為新增的事件或是事件擁有者並非用戶本人,
    // 則不顯示刪除鈕, 若事件擁有人為用戶本人時.
    private void ViewDelete(boolean enable) {
        mActivityMain.toolbarSetIcon2(enable ? R.mipmap.icon_delete : ActivityMain.RESOURCE_HIDE);
    }

    // 啟動或關閉UI的Advance模式.
    private void viewAdvance(boolean visible) {
        if (visible) {
            mViewSave.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            mViewSave.requestLayout();

            mViewDescriptionLine.setVisibility(View.VISIBLE);
            mViewTodoLine.setVisibility(View.VISIBLE);
        } else {
            mViewSave.getLayoutParams().width = 0;  // by weight
            mViewSave.requestLayout();

            mViewDescriptionLine.setVisibility(View.INVISIBLE);
            mViewTodoLine.setVisibility(View.INVISIBLE);
        }

        viewTodoList(mViewTodoContainer.getChildCount() != 0);
    }

    // 關啟或關閉TodoList的編輯區
    private void viewTodoList(boolean visible) {
        if (visible) {
            mViewTodoOption.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;

            int count = mViewTodoContainer.getChildCount();
            for (int idx = 0; idx < count; idx++) {
                ViewTodo viewTodo = (ViewTodo) mViewTodoContainer.getChildAt(idx);
                viewTodo.setSeparatorVisibility(idx == (count - 1) ? View.INVISIBLE : View.VISIBLE);
            }
        } else {
            mViewTodoOption.getLayoutParams().height = 0;
        }

        mViewTodoOption.requestLayout();
    }

    private View addTodoView(WatchTodo todo) {
        ViewTodo viewTodo = new ViewTodo(mActivityMain);
        viewTodo.setTag(todo);
        viewTodo.load(todo);
        viewTodo.setEditMode();
        viewTodo.setOnEditListener(mTodoEditListener);

        int height = getResources().getDisplayMetrics().heightPixels / 15;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);

        mViewTodoContainer.addView(viewTodo, layoutParams);

        viewTodoList(true);

        return viewTodo;
    }

    private void delTodoView(WatchTodo todo) {
        ViewTodo viewTodo = (ViewTodo) mViewTodoContainer.findViewWithTag(todo);
        mViewTodoContainer.removeView(viewTodo);

        viewTodoList(mViewTodoContainer.getChildCount() != 0);
    }

    private void setAssign(int kid) {
        setAssign(mActivityMain.mOperator.getKid(kid));
    }

    private void setAssign(WatchContact.Kid kid) {
        if (kid == null) {
            mViewAssignPhoto.setBitmap(null);
            mViewAssignName.setText("");
        } else {
            mViewAssignPhoto.setBitmap(kid.mPhoto);
            String name = kid.mLabel;
            if (mEvent.mKids.size() > 1)
                name += "...";
            mViewAssignName.setText(name);
        }
    }

    private View addKid(WatchContact.Kid kid) {
        int size = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()));
        int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));

        ViewCircle viewCircle = new ViewCircle(mActivityMain);

        viewCircle.setTag(kid);
        viewCircle.setBitmap(kid.mPhoto);
        viewCircle.setStrokeWidth(mViewAssignPhoto.getStrokeWidth());
        viewCircle.setStrokeColorActive(ContextCompat.getColor(mActivityMain, R.color.color_orange_main));
        viewCircle.setStrokeColorNormal(ContextCompat.getColor(mActivityMain, R.color.color_white));
        viewCircle.setActive(mEvent.containsKid(kid.mId));
        viewCircle.setOnClickListener(mAssignOptionListener);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        layoutParams.setMarginStart(margin);
        layoutParams.setMarginEnd(margin);
        viewCircle.setLayoutParams(layoutParams);

        mViewAssignContainer.addView(viewCircle);

        return viewCircle;
    }

    private View addColor(int color) {
        ViewShape view = new ViewShape(mActivityMain);

        int size = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        view.setColor(color);
        view.setDesiredSize(size);
        view.setShape(ViewShape.SHAPE_CIRCLE);

        int padding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        view.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        layoutParams.gravity = Gravity.CENTER;

        view.setOnClickListener(mColorOptionListener);
        mViewColorContainer.addView(view, layoutParams);

        return view;
    }

    private void addRepeat(String repeat) {
        TextView view = new TextView(mActivityMain);

        if (repeat.contains(WatchEvent.REPEAT_MONTHLY)) {
            view.setText(getResources().getString(R.string.event_repeat_monthly));
            view.setTag(WatchEvent.REPEAT_MONTHLY);
        } else if (repeat.contains(WatchEvent.REPEAT_WEEKLY)) {
            view.setText(getResources().getString(R.string.event_repeat_weekly));
            view.setTag(WatchEvent.REPEAT_WEEKLY);
        } else if (repeat.contains(WatchEvent.REPEAT_DAILY)) {
            view.setText(getResources().getString(R.string.event_repeat_daily));
            view.setTag(WatchEvent.REPEAT_DAILY);
        } else {
            view.setText(getResources().getString(R.string.event_repeat_never));
            view.setTag(WatchEvent.REPEAT_NEVER);
        }

        view.setTextColor(ContextCompat.getColor(mActivityMain, R.color.color_gray_main));
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        view.setGravity(Gravity.CENTER);
        view.setPadding(0, 10, 0, 10);
        view.setOnClickListener(mRepeatOptionListener);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);

        mViewRepeatContainer.addView(view, layoutParams);
    }

    private View.OnClickListener mAlarmListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.mEventStack.push(mEvent);

            Bundle bundle = new Bundle();
            bundle.putInt("kidId", mEvent.mKids.get(0));
            mActivityMain.selectFragment(FragmentCalendarAlarm.class.getName(), bundle);
        }
    };

    private View.OnClickListener mAssignListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewAssignOption.getLayoutParams();
            if (params.height == 0)
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            else
                params.height = 0;

            mViewAssignOption.setLayoutParams(params);
        }
    };

    private View.OnClickListener mStartListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isStarDate = true;
            openDatePicker();

        }
    };

    private View.OnClickListener mEndListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isStarDate = false;
            openDatePicker();

        }
    };

    private View.OnClickListener mColorListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewColorOption.getLayoutParams();
            if (params.height == 0)
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            else
                params.height = 0;

            mViewColorOption.setLayoutParams(params);
        }
    };

    private View.OnClickListener mRepeatListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewRepeatOption.getLayoutParams();
            if (params.height == 0)
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            else
                params.height = 0;

            mViewRepeatOption.setLayoutParams(params);
        }
    };

    private View.OnClickListener mTodoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchTodo todo = new WatchTodo();
            mEvent.mTodoList.add(todo);
            addTodoView(todo);
        }
    };

    private View.OnClickListener mSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!loadAlarm()) {
                Toast.makeText(mActivityMain, R.string.calendar_event_please_select_event, Toast.LENGTH_SHORT).show();
                return;
            }

            mProcessDialog = ProgressDialog.show(mActivityMain, "Processing", "Please wait...", true);
            mActivityMain.mOperator.setEvent(mSetEventListener, mEvent);
        }
    };

    private View.OnClickListener mAdvanceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            viewAdvance(true);
        }
    };

    private View.OnClickListener mAssignOptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewCircle viewCircle = (ViewCircle) view;
            WatchContact.Kid kid = (WatchContact.Kid) view.getTag();

            if (viewCircle.getActive()) {
                if (mEvent.mKids.size() <= 1)    // least one.
                    return;

                mEvent.removeKid(kid.mId);
                viewCircle.setActive(false);
            } else {
                mEvent.insertKid(kid.mId, 0);
                viewCircle.setActive(true);
            }

            setAssign(mEvent.mKids.get(0));
        }
    };

    private View.OnClickListener mColorOptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewShape shape = (ViewShape) view;
            mViewColor.setColor(shape.getColor());

            mEvent.mColor = WatchEvent.colorToString(mViewColor.getColor());
        }
    };

    private View.OnClickListener mRepeatOptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView viewOption = (TextView) view;
            mViewRepeat.setText(viewOption.getText().toString());
            mEvent.mRepeat = (String) viewOption.getTag();
        }
    };

    private TextWatcher mDescriptionWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mEvent.mDescription = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private ViewTodo.OnEditListener mTodoEditListener = new ViewTodo.OnEditListener() {
        @Override
        public void onDelete(ViewTodo viewTodo, View view) {
            WatchTodo todo = (WatchTodo) viewTodo.getTag();
            delTodoView(todo);
            mEvent.mTodoList.remove(todo);
        }

        @Override
        public void onCheck(ViewTodo viewTodo, View view, boolean checked) {
            WatchTodo todo = (WatchTodo) viewTodo.getTag();
            viewTodo.save(todo);
        }

        @Override
        public void onText(ViewTodo viewTodo, View view, String text) {
            WatchTodo todo = (WatchTodo) viewTodo.getTag();
            viewTodo.save(todo);
        }
    };

    private void loadUser() {
        WatchContact.User me = mActivityMain.mOperator.getUser();

        if (mEvent.mUserId == me.mId)
            mViewUserName.setText(me.mFirstName + " " + me.mLastName);
        else
            mViewUserName.setText("Others (" + mEvent.mUserId + ")");
    }

    private boolean loadAlarm() {
        WatchEvent.Alarm[] alertList;
        WatchContact.Kid kid = mActivityMain.mOperator.getKid(mEvent.mKids.get(0));
        if (kid.mFirmwareVersion != null && kid.mFirmwareVersion.contains("KDV01")) {
            alertList = WatchEvent.AlarmList_new;
        } else {
            alertList = WatchEvent.AlarmList;

        }

        for (WatchEvent.Alarm target : alertList) {
            if (target.mId == mEvent.mAlert) {
                mEvent.mAlert = target.mId;
                mEvent.mName = getResources().getString(target.mName);   // multi-dependence issue, cause from KD.
                mEventAlarmName.setText(mEvent.mName);
                return true;
            }
        }

        return false;
    }

    private void loadAssign() {
        ArrayList<WatchContact.Kid> list = new ArrayList<>();

        list.addAll(mActivityMain.mOperator.getDeviceList());
        list.addAll(mActivityMain.mOperator.getSharedList());

        if (list.size() == 0)
            return;

        if (mEvent.mKids.size() == 0) { // assign is illegal
            WatchContact.Kid focusKid = mActivityMain.mOperator.getFocusKid();
            if (focusKid == null)
                mEvent.insertKid(list.get(0).mId, 0);
            else
                mEvent.insertKid(focusKid.mId, 0);
        }

        setAssign(mEvent.mKids.get(0));

        // Note: We create kids options here, to avoid
        // some kids create during fragment paused.
        mViewAssignContainer.removeAllViews();
        for (WatchContact.Kid kid : list)
            addKid(kid);
    }

    private void loadDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US);

        mViewStart.setText(simpleDateFormat.format(mEvent.mStartDate));
        mViewEnd.setText(simpleDateFormat.format(mEvent.mEndDate));
    }

    private void loadColor() {
        mViewColor.setColor(WatchEvent.stringToColor(mEvent.mColor));
    }

    private void loadRepeat() {
        String repeat = mEvent.mRepeat.toUpperCase();

        switch (repeat) {
            case WatchEvent.REPEAT_MONTHLY:
                mViewRepeat.setText(getResources().getString(R.string.event_repeat_monthly));
                break;

            case WatchEvent.REPEAT_WEEKLY:
                mViewRepeat.setText(getResources().getString(R.string.event_repeat_weekly));
                break;

            case WatchEvent.REPEAT_DAILY:
                mViewRepeat.setText(getResources().getString(R.string.event_repeat_daily));
                break;

            case WatchEvent.REPEAT_NEVER:
                mViewRepeat.setText(getResources().getString(R.string.event_repeat_never));
                break;
        }
    }

    private void loadDescription() {
        mViewDescription.setText(mEvent.mDescription);
    }

    private void loadTodo() {
        for (WatchTodo todo : mEvent.mTodoList)
            addTodoView(todo);
    }

    private void loadWatchEvent() {
        loadUser();
        loadAssign();
        loadDate();
        loadColor();
        loadRepeat();
        loadDescription();
        loadTodo();
        loadAlarm();

        viewAdvance(mEvent.mRepeat.length() != 0 || mEvent.mDescription.length() != 0 || mEvent.mTodoList.size() != 0);
        viewEnable(mActivityMain.mOperator.getUser().mId == mEvent.mUserId);
        ViewDelete(mActivityMain.mOperator.getUser().mId == mEvent.mUserId && mEvent.mId != 0);
    }

    WatchOperator.finishListener mSetEventListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Event Created");
            mActivityMain.FirebaseLog(LogEvent.Event.ACTION, bundle);

            mProcessDialog.dismiss();
            mActivityMain.popFragment();

            showSyncDialog();
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Event Creation Fail");
            mActivityMain.FirebaseLog(LogEvent.Event.ACTION, bundle);
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, Command, Toast.LENGTH_SHORT).show();
        }
    };

    WatchOperator.finishListener mDeleteEventListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Event Deleted");
            mActivityMain.FirebaseLog(LogEvent.Event.ACTION, bundle);
            mProcessDialog.dismiss();
            mActivityMain.popFragment();
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            Toast.makeText(mActivityMain, Command, Toast.LENGTH_SHORT).show();
        }
    };

    public void openDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                mDateSetListener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        if(mEvent.mStartDate != 0 && !isStarDate) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(mEvent.mStartDate);
            dpd.setMinDate(cal);
        } else {
            dpd.setMinDate(now);
        }

        dpd.setAccentColor(ContextCompat.getColor(mViewMain.getContext(), R.color.color_orange_deep));
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void openTimePicker() {
        Calendar now = Calendar.getInstance();

        int minutes = now.get(Calendar.MINUTE);
        if(!isStarDate) {
            minutes = 0;
        }

        TimePickerDialog dpd = TimePickerDialog.newInstance(
                mTimeSetListener,
                now.get(Calendar.HOUR),
                minutes,
                false
        );

        now.add(Calendar.MINUTE, 5);

        if(mCalendarDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                mCalendarDate.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                mCalendarDate.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
            dpd.setMinTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), 0);
        }
        dpd.setAccentColor(ContextCompat.getColor(mViewMain.getContext(), R.color.color_orange_deep));
        dpd.show(getFragmentManager(), "Timepickerdialog");
    }

    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
            mCalendarDate.set(mCalendarDate.get(Calendar.YEAR), mCalendarDate.get(Calendar.MONTH), mCalendarDate.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);

            if(isStarDate) {
                mEvent.mStartDate = mCalendarDate.getTimeInMillis();
                mCalendarDate.add(Calendar.MINUTE, 10);
                mEvent.mEndDate = mCalendarDate.getTimeInMillis();
            } else {
                mEvent.mEndDate = mCalendarDate.getTimeInMillis();
            }

            loadDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss", Locale.US);
            Log.d("Time-Picker -", sdf.format(mCalendarDate.getTime()));
        }
    };

    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            mCalendarDate = Calendar.getInstance();
            mCalendarDate.set(year, monthOfYear, dayOfMonth);
            openTimePicker();
        }
    };


    private void showSyncDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivityMain, R.style.AppDialogStyle);
        builder.setTitle("");
        builder.setMessage(R.string.dialog_sync_content);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();

        // display dialog
        dialog.show();

        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setBackgroundColor(getResources().getColor(R.color.color_orange_main));
        nbutton.setTextColor(Color.WHITE);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) nbutton.getLayoutParams();
        params.rightMargin = 50;
        nbutton.setLayoutParams(params);

        TextView msgText = (TextView) dialog.findViewById(android.R.id.message);
        msgText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

    }

}
