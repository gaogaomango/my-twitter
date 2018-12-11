package jp.co.mo.mysns;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class AbstractBaseActivity extends AppCompatActivity {

    private static final String TAG = AbstractBaseActivity.class.getSimpleName();

    private static final String[] PERMISSION_LIST = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private CallBackAction mCallBackAction;

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
                for(int i : grantResults) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isSuccess = false;
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

}
