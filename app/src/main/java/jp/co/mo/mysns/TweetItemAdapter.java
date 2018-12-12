package jp.co.mo.mysns;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TweetItemAdapter extends AbstractTweetAdapter {


    public TweetItemAdapter(List<TweetInfo> mList, Activity mActivity) {
        super(mList, mActivity);
    }

    @Override
    protected View getViewImpl(int position, View convertView, ViewGroup parent) {
        final TweetInfo tweetInfo = mTweetInfoList.get(position);
        LayoutInflater inflater = mActivity.get().getLayoutInflater();
        View view = inflater.inflate(R.layout.tweet_item, null);

        TextView txtUserName = view.findViewById(R.id.txtUserName);
        txtUserName.setText(tweetInfo.getFirstName());
        txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView txtTweet = view.findViewById(R.id.txtTweet);
        txtTweet.setText(tweetInfo.getTweetText());

        TextView txtTweetDate = view.findViewById(R.id.txtTweetDate);
        txtTweetDate.setText(tweetInfo.getTweetDate());

        ImageView tweetPicture = view.findViewById(R.id.tweetPicture);
        ImageView picturePath = view.findViewById(R.id.picturePath);

        return view;
    }
}
