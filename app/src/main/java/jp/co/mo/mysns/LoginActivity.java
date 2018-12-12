package jp.co.mo.mysns;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AbstractBaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int RESULT_LOAD_IMAGE = 123;

    @BindView(R.id.editUserName)
    EditText mEditUserName;
    @BindView(R.id.editUserEmail)
    EditText mEditUserEmail;
    @BindView(R.id.editUserPassword)
    EditText mEditUserPassword;
    @BindView(R.id.imgUserIcon)
    ImageView mImgUserIcon;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged sign in: " + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged sign out");
                }
            }
        };

        mImgUserIcon.setOnClickListener(v ->
                checkPermission(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        new CallBackAction() {
                            @Override
                            public void onSuccess() {
                                chooseImageFromDevice();
                            }

                            @Override
                            public void onFailed() {

                            }
                        })
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        signInAnonymousUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        hideProgressDialog();
    }

    private void signInAnonymousUser() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    private void chooseImageFromDevice() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (data == null) {
            return;
        }

        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn,
                        null,
                        null,
                        null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    if (!TextUtils.isEmpty(picturePath)) {
                        mImgUserIcon.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    }
                }
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private boolean isValidInputText() {
        if (TextUtils.isEmpty(mEditUserName.getText().toString())) {
            return false;
        }
        if (TextUtils.isEmpty(mEditUserEmail.getText().toString())) {
            return false;
        }
        if (TextUtils.isEmpty(mEditUserPassword.getText().toString())) {
            return false;
        }

        return true;
    }

    @OnClick(R.id.btnLogin)
    public void onClickLoginBtn() {
        Log.d(TAG, "onClickLoginBtn");
        if (!isValidInputText()) {
            Toast.makeText(getApplicationContext(), "Please check your information.", Toast.LENGTH_LONG).show();
            return;
        }

        showProgressDialog();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sRef = storage.getReferenceFromUrl(BuildConfig.FIRE_STORAGE_URL);
        final String imagePath = DateUtil.format(DateUtil.DATE_FORMAT_YYYMMDDHHMSS, DateUtil.now())
                + mEditUserName.getText().toString()
                + ".jpg";
        StorageReference imageRef = sRef.child("images/" + imagePath);
        mImgUserIcon.setDrawingCacheEnabled(true);
        mImgUserIcon.buildDrawingCache();

        BitmapDrawable drawable = (BitmapDrawable) mImgUserIcon.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "UploadTask onFailure");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "UploadTask onSuccess");
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                Log.d(TAG, "UploadTask continueWithTask then");
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Log.d(TAG, "UploadTask continueWithTask onComplete");

                if (task.isSuccessful()) {
                    String name = "";
                    try {
                        name = java.net.URLEncoder.encode(mEditUserName.getText().toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    StringBuilder urlBuilder = new StringBuilder();
                    urlBuilder.append(BuildConfig.HTTP_HOST);
                    urlBuilder.append("register.php");
                    urlBuilder.append("?first_name=");
                    urlBuilder.append(mEditUserName.getText().toString());
                    urlBuilder.append("&email=");
                    urlBuilder.append(mEditUserEmail.getText().toString());
                    urlBuilder.append("&password=");
                    urlBuilder.append(mEditUserPassword.getText().toString());
                    urlBuilder.append("&picture_path=");
                    urlBuilder.append("images%2F" + imagePath);

                    new HttpUtil(new HttpCallBackAction() {
                        @Override
                        public void onSuccess(Object object) {
                            Log.d(TAG, "HttpUtil onSuccess register");
                            Gson gson = new Gson();
                            LoginUserResponse res = gson.fromJson((String)object, LoginUserResponse.class);

                            if (BaseResponse.SUCCESS.equalsIgnoreCase(res.getMsg())) {
                                Log.d(TAG, "HttpUtil onSuccess BaseResponse.SUCCESS");

                                Toast.makeText(getApplicationContext(), res.getMsg(), Toast.LENGTH_LONG).show();
                                StringBuilder urlBuilder = new StringBuilder();
                                urlBuilder.append(BuildConfig.HTTP_HOST);
                                urlBuilder.append("login.php");
                                urlBuilder.append("?email=");
                                urlBuilder.append(mEditUserEmail.getText().toString());
                                urlBuilder.append("&password=");
                                urlBuilder.append(mEditUserPassword.getText().toString());

                                new HttpUtil(new HttpCallBackAction() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        Log.d(TAG, "HttpUtil onSuccess login");
                                        Gson gson = new Gson();
                                        LoginUserResponse res = gson.fromJson((String) object, LoginUserResponse.class);
                                        if (BaseResponse.SUCCESS.equalsIgnoreCase(res.getMsg()) && res.getInfo().size() == 1) {
                                            login(res);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_LONG).show();
                                        }
                                        hideProgressDialog();
                                    }

                                    @Override
                                    public void onFailed(Object object) {
                                        hideProgressDialog();
                                        Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_LONG).show();
                                    }
                                }).execute(urlBuilder.toString());

                            } else {
                                hideProgressDialog();
                                Log.d(TAG, "HttpUtil onSuccess BaseResponse.FAILED");
                            }

                        }

                        @Override
                        public void onFailed(Object object) {
                            hideProgressDialog();
                            Toast.makeText(getApplicationContext(), "register failed", Toast.LENGTH_LONG).show();
                        }
                    }).execute(urlBuilder.toString());
                } else {
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(), "register failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void login(LoginUserResponse response) {
        AppDataManager.getInstance().saveStringData(this, Parameter.PREF_KEY_USER_ID, response.getInfo().get(0).getUser_id());
        hideProgressDialog();
        Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_LONG).show();
        finish();
    }
}
