package com.kidsdynamic.swing.androidswingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentSignupProfile extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCircle mViewPhoto;
    private EditText mViewFirstName;
    private EditText mViewLastName;
    private EditText mViewPhone;
    private ImageView mViewBack;
    private Button mSignupButton;

    AlertDialog mDialog;

    private Uri mPhotoUri;

    private String mRegisterMail = null;
    private String mRegisterPassword = null;
    private Bitmap mRegisterAvatar = null;
    private Dialog processDialog = null;

    public final static int ACTIVITY_RESULT_CAMERA_REQUEST = 1888;
    public final static int ACTIVITY_RESULT_PHOTO_PICK = 9111;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_signup_profile, container, false);

        mViewPhoto = (ViewCircle) mViewMain.findViewById(R.id.signup_profile_photo);
        mViewPhoto.setOnClickListener(mPhotoClickListener);

        mViewFirstName = (EditText) mViewMain.findViewById(R.id.signup_profile_first);
        mViewFirstName.setOnEditorActionListener(mEdittextActionListener);

        mViewLastName = (EditText) mViewMain.findViewById(R.id.signup_profile_last);
        mViewLastName.setOnEditorActionListener(mEdittextActionListener);

        mViewPhone = (EditText) mViewMain.findViewById(R.id.signup_profile_phone);
        mViewPhone.setOnEditorActionListener(mEdittextActionListener);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        mSignupButton = (Button) mViewMain.findViewById(R.id.signup_profile_submit);
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupSubmit();
            }
        });

        return mViewMain;
    }

    @Override
    public void onPause() {
        if (processDialog != null) {
            processDialog.dismiss();
            processDialog = null;
        }
        super.onPause();
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        mRegisterMail = mActivityMain.mConfig.getString(ActivityConfig.KEY_MAIL);
        mRegisterPassword = mActivityMain.mConfig.getString(ActivityConfig.KEY_PASSWORD);

        if (!mActivityMain.mBitmapStack.isEmpty()) {
            mRegisterAvatar = mActivityMain.mBitmapStack.pop();
            mViewPhoto.setBitmap(mRegisterAvatar);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode != Activity.RESULT_OK)
            return;

        Bitmap bitmap;
        Uri uri;

        if (requestCode == ACTIVITY_RESULT_CAMERA_REQUEST) {
            uri = mPhotoUri;

            mActivityMain.getContentResolver().notifyChange(uri, null);
            ContentResolver cr = mActivityMain.getContentResolver();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                mActivityMain.mBitmapStack.push(bitmap);
                mActivityMain.selectFragment(FragmentPhotoClip.class.getName(), null);
            } catch (IOException e) {
                Log.d("swing", "FragmentSignupProfile(Camera):" + Log.getStackTraceString(e));
            }

        } else if (requestCode == ACTIVITY_RESULT_PHOTO_PICK) {
            uri = intent.getData();

            mActivityMain.getContentResolver().notifyChange(uri, null);
            ContentResolver cr = mActivityMain.getContentResolver();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                mActivityMain.mBitmapStack.push(bitmap);
                mActivityMain.selectFragment(FragmentPhotoClip.class.getName(), null);
            } catch (IOException e) {
                Log.d("swing", "FragmentSignupProfile(Pick):" + Log.getStackTraceString(e));
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onToolbarAction1();
        }
    };

    private void signupSubmit() {
        String firstName = mViewFirstName.getText().toString();
        String lastName = mViewLastName.getText().toString();
        String phoneNumber = mViewPhone.getText().toString();

        if (mRegisterMail != null && !mRegisterMail.equals("") &&
                mRegisterPassword != null && !mRegisterPassword.equals("") &&
                !firstName.equals("") && !lastName.equals("")) {

            View view = mViewMain.getRootView();
            InputMethodManager imm = (InputMethodManager) mViewMain.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            processDialog = ProgressDialog.show(mActivityMain,
                    getResources().getString(R.string.signup_profile_processing),
                    getResources().getString(R.string.signup_profile_wait), true);

            mActivityMain.mOperator.signUp(
                    mSignUpListener,
                    mRegisterMail,
                    mRegisterPassword,
                    firstName,
                    lastName,
                    phoneNumber,
                    "",
                    mRegisterAvatar);
        }
    }

    private EditText.OnEditorActionListener mEdittextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signupSubmit();
            }

            return false;
        }
    };
    WatchOperator.finishListener mSignUpListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            processDialog.dismiss();
            mActivityMain.clearFragment(FragmentWatchHave.class.getName(), null);
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            processDialog.dismiss();
        }
    };

    private View.OnClickListener mPhotoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDialog = new AlertDialog.Builder(mActivityMain).create();

            LayoutInflater myLayout = LayoutInflater.from(mActivityMain);
            View dialogView = myLayout.inflate(R.layout.dialog_photo, null);
            mDialog.setView(dialogView);

            (dialogView.findViewById(R.id.dialog_photo_take)).setOnClickListener(mDialogTakeClickListener);
            (dialogView.findViewById(R.id.dialog_photo_library)).setOnClickListener(mDialogLibraryClickListener);
            (dialogView.findViewById(R.id.dialog_photo_cancel)).setOnClickListener(mDialogCancelClickListener);

            mDialog.setCancelable(false);
            mDialog.show();
        }
    };

    private View.OnClickListener mDialogTakeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDialog.dismiss();

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(mActivityMain.getPackageManager()) != null) {
                File photoFile = null;
                File storageDir;
                try {
                    storageDir = mActivityMain.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    photoFile = File.createTempFile("photo", ".jpg", storageDir);
                    boolean detr = photoFile.delete();
                } catch (IOException e) {
                    Log.d("swing", "mDialogTakeClickListener Exception: " + Log.getStackTraceString(e));
                }

                if (photoFile != null) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Log.d("ID: ", BuildConfig.APPLICATION_ID + ".provider");
                        mPhotoUri = FileProvider.getUriForFile((ActivityMain) getActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", photoFile);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        mPhotoUri = Uri.fromFile(photoFile);
                    }

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                    startActivityForResult(intent, ACTIVITY_RESULT_CAMERA_REQUEST);
                }
            }
        }
    };

    private View.OnClickListener mDialogLibraryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDialog.dismiss();

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_RESULT_PHOTO_PICK);
        }
    };

    private View.OnClickListener mDialogCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDialog.dismiss();
        }
    };

}
