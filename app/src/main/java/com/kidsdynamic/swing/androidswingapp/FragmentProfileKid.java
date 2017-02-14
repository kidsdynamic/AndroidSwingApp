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
import java.util.ArrayList;

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

        mKid = mActivityMain.mContactStack.isEmpty() ?
                new WatchContact.Kid() : (WatchContact.Kid) mActivityMain.mContactStack.pop();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mActivityMain.mBitmapStack.isEmpty()) {
            mAvatarBitmap = mActivityMain.mBitmapStack.pop();
            mViewPhoto.setBitmap(mAvatarBitmap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_kid, container, false);

        mViewPhoto = (ViewCircle) mViewMain.findViewById(R.id.profile_kid_photo);

        mViewName = (EditText) mViewMain.findViewById(R.id.profile_kid_name);

        mViewRemove = (Button) mViewMain.findViewById(R.id.profile_kid_remove);
        mViewRemove.setOnClickListener(mRemoveListener);

        if (mKid != null) {
            if (!mKid.mBound)
                viewNewKid();
            else if (mKid.mUserId == mActivityMain.mOperator.getUser().mId)
                viewMyKid();
            else
                viewOthersKid();
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
        //mActivityMain.popFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mActivityMain.mIgnoreSyncOnce = true;
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
            if (mKid == null)
                return;

            mActivityMain.mServiceMachine.kidsDelete(mKidsDeleteListener, mKid.mId);
        }
    };

    ServerMachine.kidsDeleteListener mKidsDeleteListener = new ServerMachine.kidsDeleteListener() {
        @Override
        public void onSuccess(int statusCode) {
            mActivityMain.popFragment();
        }

        @Override
        public void onFail(int statusCode) {
            mActivityMain.popFragment();
        }
    };

    private EditText.OnEditorActionListener mEdittextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (view == mViewName && actionId == EditorInfo.IME_ACTION_DONE) {
                if (!mKid.mBound) {
                    mKid.mName = mViewName.getText().toString();

                    if (!mKid.mName.equals("")) {
                        mProcessDialog = ProgressDialog.show(mActivityMain, "Processing", "Please wait...", true);
                        String macId = ServerMachine.getMacID(mKid.mLabel);
                        mActivityMain.mServiceMachine.kidsAdd(mKidsAddListener, mKid.mName, macId);
                        //mActivityMain.mServiceMachine.kidsAdd(mKidsAddListener, mKid.mName, "AAAAAABBBB04");
                    }
                }
            }

            return false;
        }
    };

    ServerMachine.kidsAddListener mKidsAddListener = new ServerMachine.kidsAddListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.kidData response) {
            mKid.mId = response.id;
            mKid.mName = response.name;
            mKid.mDateCreated = WatchOperator.getTimeStamp(response.dateCreated);
            mKid.mMacId = response.macId;
            mKid.mUserId = response.parent.id;
            mActivityMain.mOperator.setFocusKid(mKid);
            if (mAvatarBitmap != null)
                mKid.mProfile = ServerMachine.createAvatarFile(mAvatarBitmap, mKid.mName, ".jpg");
            if (mKid.mProfile == null)
                mKid.mProfile = "";

            mActivityMain.mBLEMachine.Sync(mOnSyncListener, ServerMachine.getMacAddress(mKid.mMacId));
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

    BLEMachine.onSyncListener mOnSyncListener = new BLEMachine.onSyncListener() {
        @Override
        public void onSync(int resultCode, ArrayList<BLEMachine.InOutDoor> result) {
            if (!mKid.mProfile.equals("")) {
                mActivityMain.mServiceMachine.userAvatarUploadKid(mUserAvatarUploadKidListener, "" + mKid.mId, mKid.mProfile);
            } else {
                mProcessDialog.dismiss();
                mActivityMain.selectFragment(FragmentProfileMain.class.getName(), null);
            }
        }
    };

    ServerMachine.userAvatarUploadKidListener mUserAvatarUploadKidListener = new ServerMachine.userAvatarUploadKidListener() {

        @Override
        public void onSuccess(int statusCode, ServerGson.user.avatar.uploadKid.response response) {

            File fileFrom = new File(mKid.mProfile);
            File fileTo = new File(ServerMachine.GetAvatarFilePath(), response.kid.profile);
            if (!fileFrom.renameTo(fileTo)) {
                Log.d("swing", "Rename failed! " + mKid.mProfile + " to " + response.kid.profile);
            }
            mKid.mProfile = response.kid.profile;
            mActivityMain.mOperator.setFocusKid(mKid);

            Bundle bundle = new Bundle();
            bundle.putString(ViewFragment.BUNDLE_KEY_AVATAR, ServerMachine.GetAvatarFilePath() + mKid.mProfile);

            mProcessDialog.dismiss();
            if (mKid.mBound)
                mActivityMain.popFragment();
            else
                mActivityMain.selectFragment(FragmentProfileMain.class.getName(), bundle);
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, "Upload kid avatar failed(" + statusCode + ").", Toast.LENGTH_SHORT).show();

            Bundle bundle = new Bundle();
            bundle.putString(ViewFragment.BUNDLE_KEY_AVATAR, mKid.mProfile);

            mProcessDialog.dismiss();
            if (mKid.mBound)
                mActivityMain.popFragment();
            else
                mActivityMain.selectFragment(FragmentProfileMain.class.getName(), bundle);
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

    private void saveContact(WatchContact.Kid kid) {
        if (mKid == null)
            return;
        mKid.mName = mViewName.getText().toString();
        if (!mKid.mName.equals("")) {
            mProcessDialog = ProgressDialog.show(mActivityMain, "Processing", "Please wait...", true);
            mActivityMain.mServiceMachine.kidsUpdate(mKidsUpdateListener, mKid.mId, mKid.mName);
        } else {
            mActivityMain.popFragment();
        }
    }

    ServerMachine.kidsUpdateListener mKidsUpdateListener = new ServerMachine.kidsUpdateListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.kids.update.response response) {
            if (mAvatarBitmap != null) {
                mKid.mProfile = ServerMachine.createAvatarFile(mAvatarBitmap, mKid.mName, ".jpg");
                mActivityMain.mOperator.setFocusKid(mKid);
                mActivityMain.mServiceMachine.userAvatarUploadKid(mUserAvatarUploadKidListener, "" + mKid.mId, mKid.mProfile);
            } else {
                mProcessDialog.dismiss();
                mActivityMain.popFragment();
            }
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            mActivityMain.popFragment();
        }
    };
}
