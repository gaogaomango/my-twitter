package jp.co.mo.mysns;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TweetItemAdapter extends AbstractTweetAdapter {

    public static final String FOLLOW = "Follow";
    public static final String UNFOLLOW = "UnFollow";


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
                if(((MainActivity) mActivity.get()) == null) {
                    return;
                }
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(BuildConfig.HTTP_HOST);
                urlBuilder.append("is_user_following.php");
                urlBuilder.append("?user_id=");
                urlBuilder.append(AppDataManager.getInstance().getUserId(mActivity.get()));
                urlBuilder.append("&following_user_id=");
                urlBuilder.append(tweetInfo.getUserId());
                new HttpUtil(new HttpCallBackAction() {
                    @Override
                    public void onSuccess(Object object) {
                        try {
                            Gson gson = new Gson();
                            IsFollowingResponse response = gson.fromJson((String) object, IsFollowingResponse.class);
                            if(response != null && BaseResponse.SUCCESS.equalsIgnoreCase(response.getMsg())) {
                                if(IsFollowingResponse.SUBSCRIBER.equalsIgnoreCase(response.getInfo())) {
                                    ((MainActivity) mActivity.get()).mBtnFollow.setText(UNFOLLOW);
                                } else if(IsFollowingResponse.NOT_SUBSCRIBER.equalsIgnoreCase(response.getInfo())) {
                                    ((MainActivity) mActivity.get()).mBtnFollow.setText(FOLLOW);
                                } else {
                                    return;
                                }
                                ((TextView)(mActivity.get()).findViewById(R.id.txtNameFollowers)).setText(tweetInfo.getFirstName());
                                ((MainActivity)mActivity.get()).loadTweets(0, TweetSearchType.ONE_PERSON.getTypeId(), null, tweetInfo.getUserId());
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
        });

        TextView txtTweet = view.findViewById(R.id.txtTweet);
        txtTweet.setText(tweetInfo.getTweetText());

        TextView txtTweetDate = view.findViewById(R.id.txtTweetDate);
        txtTweetDate.setText(tweetInfo.getTweetDate());

        ImageView tweetPicture = view.findViewById(R.id.tweetPicture);
        Picasso.get().load(tweetInfo.getTweetPicture()).into(tweetPicture);

        ImageView picturePath = view.findViewById(R.id.picturePath);
        Picasso.get().load(tweetInfo.getPicturePath()).into(picturePath);

        return view;
    }
}
