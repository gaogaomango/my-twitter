package jp.co.mo.mysns;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

public class TweetLoadingAdapter extends AbstractTweetAdapter {

    public TweetLoadingAdapter(List<TweetInfo> mList, Activity mActivity) {
        super(mList, mActivity);
    }

    @Override
    protected View getViewImpl(int position, View convertView, ViewGroup parent) {
        final TweetInfo tweetInfo = mTweetInfoList.get(position);
        LayoutInflater inflater = mActivity.get().getLayoutInflater();
        View view = inflater.inflate(R.layout.tweet_loading, null);

        return view;

    }
}
