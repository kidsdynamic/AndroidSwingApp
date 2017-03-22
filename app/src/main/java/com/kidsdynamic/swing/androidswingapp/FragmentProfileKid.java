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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button mViewRemove;

    private Dialog mProcessDialog = null;
    private Bitmap mAvatarBitmap = null;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_kid, container, false);

        mViewPhoto = (ViewCircle) mViewMain.findViewById(R.id.profile_kid_photo);

        mViewName = (EditText) mViewMain.findViewById(R.id.profile_kid_name);

        mViewRemove = (Button) mViewMain.findViewById(R.id.profile_kid_remove);
        mViewRemove.setOnClickListener(mRemoveListener);

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
        if (mKid != null) {
            if (!mKid.mBound)
                requestAddKid(mKid);
            else
                requestUpdateKid(mKid);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mKid = mActivityMain.mContactStack.isEmpty() ?
                new WatchContact.Kid() : (WatchContact.Kid) mActivityMain.mContactStack.pop();

        if (mKid != null) {
            if (!mKid.mBound)
                viewNewKid();
            else if (mKid.mUserId == mActivityMain.mOperator.getUser().mId)
                viewMyKid();
            else
                viewOthersKid();
        }

        if (!mActivityMain.mBitmapStack.isEmpty()) {
            mAvatarBitmap = mActivityMain.mBitmapStack.pop();
            mViewPhoto.setBitmap(mAvatarBitmap);
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

            mActivityMain.mContactStack.push(mKid);

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
            mActivityMain.mContactStack.push(mKid);

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
            if (mKid == null)
                return;
            mProcessDialog = ProgressDialog.show(mActivityMain,
                    getResources().getString(R.string.profile_kid_processing),
                    getResources().getString(R.string.profile_kid_wait), true);
            mActivityMain.mOperator.deleteKid(mDeleteKidListener, mKid.mId);
        }
    };

    WatchOperator.finishListener mDeleteKidListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            mProcessDialog.dismiss();
            mActivityMain.popFragment();
            mActivityMain.updateFocusAvatar();
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, Command, Toast.LENGTH_SHORT).show();
        }
    };

    private EditText.OnEditorActionListener mEdittextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (view == mViewName && actionId == EditorInfo.IME_ACTION_DONE) {
                if (!mKid.mBound)
                    requestAddKid(mKid);
                else
                    requestUpdateKid(mKid);
            }

            return false;
        }
    };

    private void viewMyKid() {
        mViewPhoto.setBitmap(mKid.mPhoto);
        mViewName.setText(mKid.mLabel);
        mViewPhoto.setFillDarker(true);
        mViewPhoto.setCrossWidth(4);
        mViewPhoto.setOnClickListener(mPhotoListener);

        mViewName.setEnabled(true);
        mViewRemove.setVisibility(View.VISIBLE);
    }

    private void viewOthersKid() {
        mViewPhoto.setBitmap(mKid.mPhoto);
        mViewName.setText(mKid.mLabel);
        mViewPhoto.setFillDarker(false);
        mViewPhoto.setCrossWidth(0);
        mViewPhoto.setOnClickListener(null);

        mViewName.setEnabled(false);
        mViewRemove.setVisibility(View.VISIBLE);
    }

    private void viewNewKid() {
        mViewPhoto.setFillDarker(true);
        mViewPhoto.setCrossWidth(4);
        mViewPhoto.setOnClickListener(mPhotoListener);

        mViewName.setEnabled(true);
        mViewName.setOnEditorActionListener(mEdittextActionListener);
        mViewRemove.setVisibility(View.INVISIBLE);
    }

    private void requestAddKid(WatchContact.Kid kid) {
        if (mKid == null)
            return;

        mKid.mName = mViewName.getText().toString();
        if (!mKid.mName.equals("")) {
            mProcessDialog = ProgressDialog.show(mActivityMain,
                    getResources().getString(R.string.profile_kid_processing),
                    getResources().getString(R.string.profile_kid_wait), true);
            String macId = ServerMachine.getMacID(mKid.mLabel);
            mActivityMain.mOperator.setKid(mAddKidListener, mKid.mName, macId, mAvatarBitmap);
        } else {
            Toast.makeText(mActivityMain, getResources().getString(R.string.profile_kid_name_not_blank), Toast.LENGTH_SHORT).show();
        }
    }

    WatchOperator.finishListener mAddKidListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            mProcessDialog.dismiss();
            mActivityMain.selectFragment(FragmentProfileMain.class.getName(), null);
            mActivityMain.updateFocusAvatar();
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, Command, Toast.LENGTH_SHORT).show();
        }
    };

    private void requestUpdateKid(WatchContact.Kid kid) {
        if (mKid == null)
            return;

        mKid.mName = mViewName.getText().toString();
        if (!mKid.mName.equals("")) {
            mProcessDialog = ProgressDialog.show(mActivityMain,
                    getResources().getString(R.string.profile_kid_processing),
                    getResources().getString(R.string.profile_kid_wait), true);
            mActivityMain.mOperator.setKid(mUpdateKidListener, mKid.mId, mKid.mName, mAvatarBitmap);
        } else {
            Toast.makeText(mActivityMain, getResources().getString(R.string.profile_kid_name_not_blank), Toast.LENGTH_SHORT).show();
        }
    }

    WatchOperator.finishListener mUpdateKidListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            mProcessDialog.dismiss();
            mActivityMain.popFragment();
            mActivityMain.updateFocusAvatar();
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, Command, Toast.LENGTH_SHORT).show();
        }
    };
}
