package jp.co.mo.mysns;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppDataManager {

    private static final String TAG = AppDataManager.class.getSimpleName();

    private static AppDataManager mInstance = null;

    private static final String SAVE_DATA_FILE_NAME = "twitterData";

    public static AppDataManager getInstance() {
        if(mInstance == null) {
            synchronized (jp.co.mo.mysns.AppDataManager.class) {
                mInstance = new AppDataManager();
            }
        }

        return mInstance;
    }

    public boolean saveStringData(Context context, String key, String data) {
        if(context == null) {
            Log.e(TAG, "saveStringData. context is null");
            return false;
        }
        SharedPreferences pref = context.getSharedPreferences(SAVE_DATA_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, data);
        return editor.commit();
    }

    public String getStringData(Context context, String key) {
        if(context == null) {
            Log.e(TAG, "getStringData. context is null");
            return "";
        }
        SharedPreferences pref = context.getSharedPreferences(SAVE_DATA_FILE_NAME, Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public boolean clearData(Context context, String key) {
        if(context == null) {
            Log.e(TAG, "clearData. context is null");
            return false;
        }
        SharedPreferences pref = context.getSharedPreferences(SAVE_DATA_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        return editor.commit();
    }

}
