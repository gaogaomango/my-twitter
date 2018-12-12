package jp.co.mo.mysns;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AbstractBaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.channelInfo)
    LinearLayout mChannelInfo;
    @BindView(R.id.listTweets)
    ListView mListTweets;

    List<TweetInfo> mTweetInfoList;

    private String mDownloadImgName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mChannelInfo.setVisibility(View.GONE);

        if (TextUtils.isEmpty(AppDataManager.getInstance().getStringData(this, Parameter.PREF_KEY_USER_ID))) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        mTweetInfoList = new ArrayList<>();
        mTweetInfoList.add(new TweetInfo(null,
                null,
                null,
                null,
                null,
                null,
                null));

        // TODO: set the adapter

        AbstractTweetAdapter adapter = getAdapter(TweetAdapterType.TWEET_ADD, mTweetInfoList);
//        AbstractTweetAdapter adapter = getAdapter(TweetAdapterType.TWEET_LOADING, mTweetInfoList);
//        AbstractTweetAdapter adapter = getAdapter(TweetAdapterType.OTHER, mTweetInfoList);
//        AbstractTweetAdapter adapter = getAdapter(TweetAdapterType.TWEET_NOT_EXISTS, mTweetInfoList);
        mListTweets.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchBar).getActionView();
        SearchManager sm = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(sm.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AbstractTweetAdapter getAdapter(TweetAdapterType type, List<TweetInfo> listTweetsInfo) {
        switch (type) {
            case TWEET_ADD:
                return new TweetAddAdapter(listTweetsInfo, this);
            case TWEET_LOADING:
                return new TweetLoadingAdapter(listTweetsInfo, this);
            case TWEET_NOT_EXISTS:
                return new TweetNoTweetAdapter(listTweetsInfo, this);
            case OTHER:
                return new TweetItemAdapter(listTweetsInfo, this);
            default:
                return null;
        }
    }

    public void chooseImageFromDevice() {
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
                        uploadImage(BitmapFactory.decodeFile(picturePath));
                    }
                }
        }
    }

    private void uploadImage(Bitmap bitmap) {
        showProgressDialog();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sRef = storage.getReferenceFromUrl(BuildConfig.FIRE_STORAGE_URL);
        String userId = null;
        try {
            userId = java.net.URLEncoder.encode(AppDataManager.getInstance().getUserId(this), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please check your name.", Toast.LENGTH_LONG).show();
            return;
        }
        final String imagePath = getUploadImgPath(userId);
        mDownloadImgName = "images%2F" + imagePath;
        StorageReference imageRef = sRef.child("images/" + imagePath);

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
                    Toast.makeText(getApplicationContext(), "img saved", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "img saving was failed", Toast.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

}
