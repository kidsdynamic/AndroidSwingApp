package com.kidsdynamic.swing.androidswingapp;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;

/**
 * Created by 03543 on 2017/1/24.
 */

public class FragmentProfileKid extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;
    private ViewCircle mViewPhoto;
    private EditText mViewName;
    private EditText mViewZip;
    private Button mViewRemove;

    AlertDialog mDialog;
    private Uri mPhotoUri;
    public final static int ACTIVITY_RESULT_CAMERA_REQUEST = 1888;
    public final static int ACTIVITY_RESULT_PHOTO_PICK = 9111;

    private WatchContact.Kid mKid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mActivityMain.mBitmapStack.isEmpty()) {
            mViewPhoto.setBitmap(mActivityMain.mBitmapStack.pop());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_kid, container, false);

        mViewPhoto = (ViewCircle) mViewMain.findViewById(R.id.profile_kid_photo);

        mViewName = (EditText)mViewMain.findViewById(R.id.profile_kid_name);
        mViewZip = (EditText)mViewMain.findViewById(R.id.profile_kid_zip);

        mViewRemove = (Button)mViewMain.findViewById(R.id.profile_kid_remove);
        mViewRemove.setOnClickListener(mRemoveListener);

        mKid = mActivityMain.mOperator.getFocusKid();
        if(mKid != null) {
            mViewPhoto.setBitmap(mKid.mPhoto);
            mViewName.setText(mKid.mLabel);
            mViewZip.setText(""); // todo: Where is ZIP?

            if(true) {    // todo: if Kid is belong to user
                mViewPhoto.setFillDarker(true);
                mViewPhoto.setCrossWidth(4);
                mViewPhoto.setOnClickListener(mPhotoListener);

                mViewName.setEnabled(true);
                mViewZip.setEnabled(true);
            }
        }

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Profile", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, R.mipmap.icon_ok);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    @Override
    public void onToolbarAction2() {
        saveContact(mKid);
        mActivityMain.selectFragment(FragmentProfileOption.class.getName(), null);
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

    private View.OnClickListener mRemoveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mKid == null)
                return;
        }
    };

    private void saveContact(WatchContact.Kid kid) {
        if(mKid == null)
            return;
    }
}
