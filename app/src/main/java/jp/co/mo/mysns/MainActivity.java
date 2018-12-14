package jp.co.mo.mysns;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AbstractBaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.channelInfo)
    LinearLayout mChannelInfo;
    @BindView(R.id.listTweets)
    ListView mListTweets;
    @BindView(R.id.postTweets)
    ListView mPostTweets;
    @BindView(R.id.btnFollow)
    Button mBtnFollow;

    private List<TweetInfo> mTweetInfoList;
    protected String mDownloadImgName;
    private AbstractTweetAdapter mAdapter;
    private String mSelectedUserId = "";

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

        List<TweetInfo> postList = new ArrayList<>();
        postList.add(new TweetInfo(null,
                null,
                null,
                null,
                null,
                null,
                null));

        // TODO change adapter to normal layout
        AbstractTweetAdapter adapter = getAdapter(TweetAdapterType.TWEET_ADD, postList);
        mPostTweets.setAdapter(adapter);
        mTweetInfoList = new ArrayList<>();
        AbstractTweetAdapter mAdapter = getAdapter(TweetAdapterType.OTHER, mTweetInfoList);
        mListTweets.setAdapter(mAdapter);
        loadTweets(0, TweetSearchType.MY_FOLLOWING.getTypeId(), null, null);

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
                try {
                    String searchQuery = java.net.URLEncoder.encode(query, "UTF-8");
                    loadTweets(0, TweetSearchType.SEARCH_IN.getTypeId(), searchQuery, null);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
                loadTweets(0, TweetSearchType.MY_FOLLOWING.getTypeId(), null, null);
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
        StorageReference imageRef = sRef.child("images/" + imagePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        Log.e(TAG, "hey!");
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
                    mDownloadImgName = task.getResult().toString();
                    Toast.makeText(getApplicationContext(), "img saved", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "img saving was failed", Toast.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

    protected void loadTweets(int startFrom, int userOperationType, String searchQuery, String selectedUserId) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BuildConfig.HTTP_HOST);
        urlBuilder.append("tweet_list.php");
        urlBuilder.append("?user_id=");

        if (TweetSearchType.SEARCH_IN.equals(TweetSearchType.getByTypeId(userOperationType))) {
            if (TextUtils.isEmpty(searchQuery)) {
                Toast.makeText(getApplicationContext(), "You need to some term to search.", Toast.LENGTH_LONG).show();
            }

            urlBuilder.append(AppDataManager.getInstance().getUserId(this));
            urlBuilder.append("&start_from=");
            urlBuilder.append(startFrom);
            urlBuilder.append("&op=");
            urlBuilder.append(userOperationType);
            urlBuilder.append("&query=");
            urlBuilder.append(searchQuery);
        } else if (TweetSearchType.ONE_PERSON.equals(TweetSearchType.getByTypeId(userOperationType))) {
            urlBuilder.append(selectedUserId);
            urlBuilder.append("&start_from=");
            urlBuilder.append(startFrom);
            urlBuilder.append("&op=");
            urlBuilder.append(userOperationType);

        } else {
            urlBuilder.append(AppDataManager.getInstance().getUserId(this));
            urlBuilder.append("&start_from=");
            urlBuilder.append(startFrom);
            urlBuilder.append("&op=");
            urlBuilder.append(userOperationType);
        }

        new HttpUtil(new HttpCallBackAction() {
            @Override
            public void onSuccess(Object object) {
                Log.d(TAG, "loadTweet onSuccess");
                if (object == null) {
                    return;
                }
                Gson gson = null;
                TweetResponse response = null;
                try {
                    gson = new Gson();
                    response = gson.fromJson((String) object, TweetResponse.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "load tweets was failed", Toast.LENGTH_LONG).show();
                    return;
                }

                if (response == null || TextUtils.isEmpty(response.getMsg())) {
                    return;
                }

                if (BaseResponse.SUCCESS.equalsIgnoreCase(response.getMsg())) {
                    if (response.getInfo() == null || response.getInfo().isEmpty()) {
                        if(mAdapter == null || !(mAdapter instanceof TweetNoTweetAdapter)) {
                            mAdapter = getAdapter(TweetAdapterType.TWEET_NOT_EXISTS, mTweetInfoList);
                            mListTweets.setAdapter(mAdapter);
                        }
                    } else {
                        mTweetInfoList.clear();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            response.getInfo().stream().forEach(info ->
                                    mTweetInfoList.add(new TweetInfo(info.getTweet_id(),
                                            info.getTweet_text(),
                                            info.getTweet_picture(),
                                            info.getTweet_date(),
                                            info.getUser_id(),
                                            info.getFirst_name(),
                                            info.getPicture_path())));
                        } else {
                            for (TweetInfoResponse info : response.getInfo()) {
                                if (info != null) {
                                    mTweetInfoList.add(new TweetInfo(info.getTweet_id(),
                                            info.getTweet_text(),
                                            info.getTweet_picture(),
                                            info.getTweet_date(),
                                            info.getUser_id(),
                                            info.getFirst_name(),
                                            info.getPicture_path()));
                                }
                            }
                        }
                        if(mAdapter == null || !(mAdapter instanceof TweetItemAdapter)) {
                            mAdapter = getAdapter(TweetAdapterType.OTHER, mTweetInfoList);
                            mListTweets.setAdapter(mAdapter);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onFailed(Object object) {
                Log.d(TAG, "loadTweet onFailed");
            }
        }).execute(urlBuilder.toString());

        if (TweetSearchType.ONE_PERSON.equals(TweetSearchType.getByTypeId(userOperationType))) {
            mChannelInfo.setVisibility(View.VISIBLE);
            mSelectedUserId = selectedUserId;
        } else {
            mChannelInfo.setVisibility(View.GONE);
            mSelectedUserId = "";
        }
    }

    @OnClick(R.id.btnFollow)
    public void onClickFollowBtn(View v) {
        if(TextUtils.isEmpty(mSelectedUserId)) {
            return;
        }
        int operation;
        String followBtnText = mBtnFollow.getText().toString();
        if(TextUtils.isEmpty(followBtnText)) {
           return;
        }

        if(TweetItemAdapter.FOLLOW.equals(followBtnText)) {
            operation = 1;
        } else if(TweetItemAdapter.UNFOLLOW.equals(followBtnText)) {
            operation = 2;
        } else {
            return;
        }

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BuildConfig.HTTP_HOST);
        urlBuilder.append("user_following.php");
        urlBuilder.append("?user_id=");
        urlBuilder.append(AppDataManager.getInstance().getUserId(this));
        urlBuilder.append("&following_user_id=");
        urlBuilder.append(mSelectedUserId);
        urlBuilder.append("&op=");
        urlBuilder.append(operation);
        new HttpUtil(new HttpCallBackAction() {
            @Override
            public void onSuccess(Object object) {
                try {
                    Gson gson = new Gson();
                    IsFollowingResponse response = gson.fromJson((String) object, IsFollowingResponse.class);
                    if(response != null && BaseResponse.SUCCESS.equalsIgnoreCase(response.getMsg())) {
                        if(IsFollowingResponse.SUBSCRIBER.equalsIgnoreCase(response.getInfo())) {
                            mBtnFollow.setText(TweetItemAdapter.UNFOLLOW);
                        } else if(IsFollowingResponse.NOT_SUBSCRIBER.equalsIgnoreCase(response.getInfo())) {
                            mBtnFollow.setText(TweetItemAdapter.FOLLOW);
                        } else {
                            return;
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    return;
                }
            }

            @Override
            public void onFailed(Object object) {

            }
        }).execute(urlBuilder.toString());
    }

}
