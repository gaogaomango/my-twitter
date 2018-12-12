package jp.co.mo.mysns;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil extends AsyncTask<String, String, String> {

    private static final String TAG = HttpUtil.class.getSimpleName();

    HttpCallBackAction mCallBackAction = null;

    public HttpUtil(HttpCallBackAction callBackAction) {
        super();
        this.mCallBackAction = callBackAction;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        StringBuilder sb = new StringBuilder();
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        BufferedReader br = null;
        try {
            URL url = new URL(strings[0]);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(7000);

            in = new BufferedInputStream(urlConnection.getInputStream());

            br = new BufferedReader(new InputStreamReader(in));
            String line;

            while((line=br.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            in = null;
            br.close();
            br = null;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        for(String val : values) {
            Log.e(TAG, "onProgressUpdate str: " + val);
        }
        Gson gson = new Gson();
        mCallBackAction.onSuccess(gson.toJson(values[0]));
    }
}
