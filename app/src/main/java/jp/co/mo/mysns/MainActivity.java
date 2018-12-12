package jp.co.mo.mysns;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AbstractBaseActivity {

    @BindView(R.id.channelInfo)
    LinearLayout mChannelInfo;
    @BindView(R.id.listTweets)
    ListView mListTweets;

    List<TweetInfo> mTweetInfoList;

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
}
