package jp.co.mo.mysns;

import android.Manifest;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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
//                String tweet = "";
//                try {
//                    java.net.URLEncoder.encode(postText.getText().toString(), "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }


            }
        });

        return view;
    }


}
