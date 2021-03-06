package jp.co.mo.mysns;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Arrays;
import java.util.stream.Stream;

public class AbstractBaseActivity extends AppCompatActivity {

    private static final String TAG = AbstractBaseActivity.class.getSimpleName();

    private static final String[] PERMISSION_LIST = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    protected static final int RESULT_LOAD_IMAGE = 100;

    private CallBackAction mCallBackAction;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void checkPermission(CallBackAction callBackAction) {
        checkPermission(PERMISSION_LIST, callBackAction);
    }

    protected void checkPermission(String[] permissions, CallBackAction callBackAction) {
        mCallBackAction = callBackAction;
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissionGranted(permissions)) {
                requestPermissions(permissions,
                        REQUEST_CODE_ASK_PERMISSIONS);
                mCallBackAction = callBackAction;
                return;
            }
        }
        callBackAction.onSuccess();
    }

    private boolean checkPermissionGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isSuccess = true;
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    isSuccess = Arrays.stream(grantResults).noneMatch(i -> i != PackageManager.PERMISSION_GRANTED);
                } else {
                    for(int i : grantResults) {
                        if (i != PackageManager.PERMISSION_GRANTED) {
                            isSuccess = false;
                        }
                    }
                }
                if (isSuccess) {
                    mCallBackAction.onSuccess();
                } else {
                    Toast.makeText(this, "please check permissions.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    protected void chooseImageFromDevice() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected String getUploadImgPath(String userId) {
        return userId + "_" + DateUtil.format(DateUtil.DATE_FORMAT_YYYMMDDHHMSS, DateUtil.now()) + ".jpg";
    }

}
