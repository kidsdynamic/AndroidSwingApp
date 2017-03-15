package com.kidsdynamic.swing.androidswingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by 03543 on 2017/1/24.
 */

public class FragmentProfileEditor extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCircle mViewPhoto;
    private EditText mViewFirst;
    private EditText mViewLast;
    private EditText mViewPhone;
    private EditText mViewZip;

    private Bitmap mUserAvatar = null;
    private String mUserAvatarFilename = null;
    private boolean mUserAvatarChanged = false;

    private Dialog processDialog = null;

    AlertDialog mDialog;
    private Uri mPhotoUri;
    public final static int ACTIVITY_RESULT_CAMERA_REQUEST = 1888;
    public final static int ACTIVITY_RESULT_PHOTO_PICK = 9111;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_editor, container, false);

        mViewFirst = (EditText) mViewMain.findViewById(R.id.profile_editor_first);
        mViewLast = (EditText) mViewMain.findViewById(R.id.profile_editor_last);
        mViewPhone = (EditText) mViewMain.findViewById(R.id.profile_editor_phone);
        mViewZip = (EditText) mViewMain.findViewById(R.id.profile_editor_zip);

        mViewPhoto = (ViewCircle) mViewMain.findViewById(R.id.profile_editor_photo);
        mViewPhoto.setOnClickListener(mPhotoListener);

        //mViewFirst = (EditText)
        //        mViewMain.findViewById(R.id.profile_editor_first).
        //                findViewById(R.id.view_text_editor_textedit);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_profile), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, R.mipmap.icon_ok);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    @Override
    public void onToolbarAction2() {
        processDialog = ProgressDialog.show(mActivityMain,
                getResources().getString(R.string.profile_editor_processing),
                getResources().getString(R.string.profile_editor_wait), true);
        profileSave();
    }

    @Override
    public void onResume() {
        super.onResume();

        mUserAvatar = null;
        mUserAvatarChanged = false;
        if (!mActivityMain.mBitmapStack.isEmpty()) {
            mUserAvatar = mActivityMain.mBitmapStack.pop();
            mUserAvatarChanged = true;
        } else {
            profileLoad();
        }

        if (mUserAvatar != null)
            mViewPhoto.setBitmap(mUserAvatar);
    }

    @Override
    public void onPause() {
        if (processDialog != null)
            processDialog.dismiss();

        super.onPause();

        InputMethodManager inputMethodManager = (InputMethodManager) mActivityMain
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);
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

    private View.OnClickListener mPhotoListener = new View.OnClickListener() {
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
                    mPhotoUri = Uri.fromFile(photoFile);
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

    private void profileLoad() {
        WatchContact.User user = mActivityMain.mOperator.getUser();
        mViewFirst.setText(user.mFirstName);
        mViewLast.setText(user.mLastName);
        mViewPhone.setText(user.mPhoneNumber);
        mViewZip.setText(user.mZipCode);
        mUserAvatar = user.mPhoto;
    }

    private void profileSave() {
        WatchContact.User user = mActivityMain.mOperator.getUser();
        String first = mViewFirst.getText().toString();
        String last = mViewLast.getText().toString();
        String phone = mViewPhone.getText().toString();
        String zip = mViewZip.getText().toString();

        if (!first.equals(user.mFirstName) ||
                !last.equals(user.mLastName) ||
                !phone.equals(user.mPhoneNumber) ||
                !zip.equals(user.mZipCode)) {
            mActivityMain.mServiceMachine.userUpdateProfile(mUpdateProfileListener, first, last, phone, zip);
        } else if (mUserAvatar != null && mUserAvatarChanged) {
            mUserAvatarFilename = ServerMachine.createAvatarFile(mUserAvatar, "User", ".jpg");
            mActivityMain.mServiceMachine.userAvatarUpload(mUserAvatarUploadListener, mUserAvatarFilename);
        } else {
            mActivityMain.popFragment();
        }
    }

    ServerMachine.userUpdateProfileListener mUpdateProfileListener = new ServerMachine.userUpdateProfileListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.userData response) {

            mActivityMain.mOperator.setUser(
                    new WatchContact.User(
                            null,
                            response.id,
                            response.email,
                            response.firstName,
                            response.lastName,
                            WatchOperator.getTimeStamp(response.lastUpdate),
                            WatchOperator.getTimeStamp(response.dateCreated),
                            response.zipCode,
                            response.phoneNumber,
                            response.profile)
            );

            if (mUserAvatar != null && mUserAvatarChanged) {
                mUserAvatarFilename = ServerMachine.createAvatarFile(mUserAvatar, "User", ".jpg");
                mActivityMain.mServiceMachine.userAvatarUpload(mUserAvatarUploadListener, mUserAvatarFilename);
            } else {
                mActivityMain.popFragment();
            }
        }

        @Override
        public void onFail(String command, int statusCode) {
            mActivityMain.popFragment();
        }
    };

    ServerMachine.userAvatarUploadListener mUserAvatarUploadListener = new ServerMachine.userAvatarUploadListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.avatar.upload.response response) {
            WatchContact.User user = mActivityMain.mOperator.getUser();
            File fileFrom = new File(mUserAvatarFilename);
            File fileTo = new File(ServerMachine.GetAvatarFilePath(), response.user.profile);
            if (!fileFrom.renameTo(fileTo))
                Log.d("swing", "Rename failed! " + mUserAvatarFilename + " to " + response.user.profile);
            user.mProfile = response.user.profile;
            mActivityMain.mOperator.setUser(user);
            mActivityMain.mOperator.ResetBitmapCache();

            mActivityMain.popFragment();
        }

        @Override
        public void onFail(String command, int statusCode) {
            Toast.makeText(mActivityMain,
                    mActivityMain.mServiceMachine.getErrorMessage(command, statusCode) +
                            "(" + statusCode + ").", Toast.LENGTH_SHORT).show();
            mActivityMain.popFragment();
        }
    };

}
