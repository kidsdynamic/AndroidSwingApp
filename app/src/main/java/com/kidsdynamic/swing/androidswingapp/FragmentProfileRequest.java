package com.kidsdynamic.swing.androidswingapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by 03543 on 2017/1/28.
 */

public class FragmentProfileRequest extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;

    private EditText mViewMail;
    private TextView mViewCount;
    private LinearLayout mViewRequestContainer;
    private LinearLayout mViewPendingContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_request, container, false);

        mViewMail = (EditText) mViewMain.findViewById(R.id.profile_request_mail);
        mViewCount = (TextView) mViewMain.findViewById(R.id.profile_request_count);
        mViewRequestContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_request_request_container);
        mViewPendingContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_request_watch_container);

        // Test
        addRequester(new WatchContact.Person(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), "Monster purple AAAAAAAAA AAAAAAAAAAAA AAAAAAAAAAAAA AAAAAAAAA"));
        addRequester(new WatchContact.Person(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_green), "Monster green"));
        addRequester(new WatchContact.Person(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_yellow), "Monster yellow"));

        addPending(new WatchContact.Device(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_yellow), "Yellow Monster", false));
        addPending(new WatchContact.Device(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_green), "Green Monster", false));
        addPending(new WatchContact.Device(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), "Purple Monster", false));
        ////////////

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Request", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    public String makeRequesterCount() {
        return String.format(Locale.getDefault(), "You Have %d Request", mViewRequestContainer.getChildCount());
    }

    public void addRequester(WatchContact.Person person) {
        View view = WatchContact.inflateRequester(mActivityMain, person);

        View viewAllow = view.findViewById(R.id.watch_contact_requester_allow);
        viewAllow.setTag(person);
        viewAllow.setOnClickListener(mRequesterAllowClick);

        View viewDeny = view.findViewById(R.id.watch_contact_requester_deny);
        viewDeny.setTag(person);
        viewDeny.setOnClickListener(mRequesterDenyClick);

        mViewRequestContainer.addView(view);

        mViewCount.setText(makeRequesterCount());
    }

    private View.OnClickListener mRequesterAllowClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.Person person = (WatchContact.Person) view.getTag();

            Log.d("xxx", person.mLabel + " Allow");
        }
    };

    private View.OnClickListener mRequesterDenyClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.Person person = (WatchContact.Person) view.getTag();

            Log.d("xxx", person.mLabel + " Deny");
        }
    };

    public void addPending(WatchContact.Device device) {
        View view = WatchContact.inflatePending(mActivityMain, device);

        mViewPendingContainer.addView(view);
    }

}
