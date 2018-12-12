package jp.co.mo.mysns;

import android.Manifest;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class TweetAddAdapter extends AbstractTweetAdapter {


    public TweetAddAdapter(List<TweetInfo> mList, Activity mActivity) {
        super(mList, mActivity);
    }

    @Override
    protected View getViewImpl(int position, View convertView, ViewGroup parent) {
        final TweetInfo tweetInfo = mTweetInfoList.get(position);
        LayoutInflater inflater = mActivity.get().getLayoutInflater();
        View view = inflater.inflate(R.layout.tweet_add, null);

        ImageView attachFileBtn = view.findViewById(R.id.attachFile);
        attachFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AbstractBaseActivity)mActivity.get()).checkPermission(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, new CallBackAction() {
                    @Override
                    public void onSuccess() {
                        ((MainActivity)mActivity.get()).chooseImageFromDevice();
                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }
        });

        EditText postText = view.findViewById(R.id.editPost);

        ImageView postBtn = view.findViewById(R.id.btnPost);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweet = "";
                try {
                    if(!TextUtils.isEmpty(postText.getText().toString())) {
                        tweet = java.net.URLEncoder.encode(postText.getText().toString(), "UTF-8");
                    }
                    if(!TextUtils.isEmpty(((MainActivity)mActivity.get()).mDownloadImgName)) {
                        ((MainActivity)mActivity.get()).mDownloadImgName = java.net.URLEncoder.encode(((MainActivity)mActivity.get()).mDownloadImgName, "UTF-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(BuildConfig.HTTP_HOST);
                urlBuilder.append("tweet_add.php");
                urlBuilder.append("?user_id=");
                urlBuilder.append(AppDataManager.getInstance().getUserId(mActivity.get()));
                urlBuilder.append("&tweet_text=");
                urlBuilder.append(tweet);
                urlBuilder.append("&tweet_picture=");
                urlBuilder.append(((MainActivity)mActivity.get()).mDownloadImgName);

                new HttpUtil(new HttpCallBackAction() {
                    @Override
                    public void onSuccess(Object object) {

                        Gson gson = new Gson();
                        BaseResponse response = gson.fromJson((String) object, BaseResponse.class);

                        if(response == null) {
                            return;
                        }

                        if(BaseResponse.SUCCESS.equalsIgnoreCase(response.getMsg())) {
                            Toast.makeText(mActivity.get(), "post was succeed", Toast.LENGTH_LONG).show();
                            postText.setText("");
//                        loadPost();
                        } else {
                            Toast.makeText(mActivity.get(), "post was failed", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailed(Object object) {
                        Toast.makeText(mActivity.get(), "post was failed", Toast.LENGTH_LONG).show();
                    }
                }).execute(urlBuilder.toString());


            }
        });

        return view;
    }


}
