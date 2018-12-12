package jp.co.mo.mysns;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class AbstractTweetAdapter extends BaseAdapter {

    protected List<TweetInfo> mTweetInfoList;
    protected WeakReference<Activity> mActivity;

    public AbstractTweetAdapter(List<TweetInfo> mList, Activity mActivity) {
        this.mTweetInfoList = mList;
        this.mActivity = new WeakReference<>(mActivity);
    }

    @Override
    public int getCount() {
        return mTweetInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewImpl(position, convertView, parent);
    }

    /**
     * Override this method in child class.
     * @param position
     * @param convertView
     * @param parent
     * @return view
     */
    abstract protected View getViewImpl(int position, View convertView, ViewGroup parent);
}
