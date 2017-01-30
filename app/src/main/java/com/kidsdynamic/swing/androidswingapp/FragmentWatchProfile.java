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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by 03543 on 2017/1/6.
 */

public class FragmentWatchProfile extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewPhoto mViewPhoto;
    private EditText mViewName;
    private EditText mViewZip;
    private ImageView mViewBack;

    AlertDialog mDialog;
    private Dialog mProcessDialog = null;

    private Uri mPhotoUri;
    private WatchContact.Kid mDevice;
    private Bitmap mAvatarBitmap = null;
    private String mAvatarFilename;
    private String mFirstName;
    private String mLastName;
    private int mKidId;

    public final static int ACTIVITY_RESULT_CAMERA_REQUEST = 1888;
    public final static int ACTIVITY_RESULT_PHOTO_PICK = 9111;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
        mDevice = (WatchContact.Kid)getArguments().getSerializable(ViewFragment.BUNDLE_KEY_DEVICE);
        Log.d("swing", "Watch profile " + mDevice.mLabel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_profile, container, false);

        mViewPhoto = (ViewPhoto) mViewMain.findViewById(R.id.watch_profile_photo);
        mViewPhoto.setOnClickListener(mPhotoClickListener);

        mViewName = (EditText) mViewMain.findViewById(R.id.watch_profile_name);
        mViewName.setOnEditorActionListener(mEdittextActionListener);

        mViewZip = (EditText) mViewMain.findViewById(R.id.watch_profile_zip);
        mViewZip.setOnEditorActionListener(mEdittextActionListener);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Watch", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mActivityMain.mBitmapStack.isEmpty()) {
            mAvatarBitmap = mActivityMain.mBitmapStack.pop();
            mViewPhoto.setPhoto(mAvatarBitmap);
        }
    }

    @Override
    public void onPause() {
        if (mProcessDialog != null) {
            mProcessDialog.dismiss();
            mProcessDialog = null;
        }
        super.onPause();
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
                Log.d("swing", "FragmentSignupKid(Camera):" + Log.getStackTraceString(e));
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
                Log.d("swing", "FragmentSignupKid(Pick):" + Log.getStackTraceString(e));
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
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

    private EditText.OnEditorActionListener mEdittextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (view == mViewZip && actionId == EditorInfo.IME_ACTION_DONE) {
                mFirstName = mViewName.getText().toString();
                mLastName = mViewZip.getText().toString();

                if (mFirstName != null && mLastName != null) {
                    mProcessDialog = ProgressDialog.show(mActivityMain, "Processing", "Please wait...",true);
                    String macId = ServerMachine.getMacID(mDevice.mLabel);
                    mActivityMain.mServiceMachine.kidsAdd(mKidsAddListener, mFirstName, mLastName, macId);
                }
            }

            return false;
        }
    };

    ServerMachine.kidsAddListener mKidsAddListener = new ServerMachine.kidsAddListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.kidDataWithParent response) {
            if (mAvatarBitmap != null) {
                mKidId = response.id;
                mAvatarFilename = ServerMachine.createAvatarFile(mAvatarBitmap, mFirstName + mLastName);

                if (mAvatarFilename != null) {
                    mActivityMain.mServiceMachine.userAvatarUploadKid(mUserAvatarUploadKidListener, ""+mKidId, mAvatarFilename);
                } else {
                    // GioChen Todo: upload later?
                    Log.d("swing", "Can't create avatar file!" + mAvatarFilename);
                    mActivityMain.selectFragment(FragmentWatchFinish.class.getName(), null);
                }

            } else {
                mActivityMain.selectFragment(FragmentWatchFinish.class.getName(), null);
            }
        }

        @Override
        public void onConflict(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, "Add kid failed(" + statusCode + ").", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, "Add kid failed(" + statusCode + ").", Toast.LENGTH_SHORT).show();
        }
    };

    ServerMachine.userAvatarUploadKidListener mUserAvatarUploadKidListener = new ServerMachine.userAvatarUploadKidListener() {

        @Override
        public void onSuccess(int statusCode, ServerGson.user.avatar.uploadKid.response response) {
            Bundle bundle = new Bundle();
            bundle.putString(ViewFragment.BUNDLE_KEY_AVATAR, mAvatarFilename);

            mActivityMain.selectFragment(FragmentWatchFinish.class.getName(), bundle);
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, "Upload kid avatar failed(" + statusCode + ").", Toast.LENGTH_SHORT).show();
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
}
