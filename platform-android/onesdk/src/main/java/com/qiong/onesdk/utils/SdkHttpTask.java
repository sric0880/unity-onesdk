
package com.qiong.onesdk.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/***
 * 通过http访问应用服务器，获取http返回结果
 */
public class SdkHttpTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "SdkHttpTask";

    private static final int MAX_RETRY_TIME = 3;

    private static final int CONN_TIMEOUT = 15000;

    private static final int SO_TIMEOUT = 20000;

    private int mRetryCount;

    private SdkHttpListener mListener;

//    private ArrayList<NameValuePair> mKeyValueArray;

    private boolean mIsHttpPost;

    private Context mContext;

    public SdkHttpTask(Context context) {
        mContext = context;
    }

    public void doPost(SdkHttpListener listener,
//            ArrayList<NameValuePair> keyValueArray,
            String url) {
        this.mListener = listener;
        this.mIsHttpPost = true;
//        this.mKeyValueArray = keyValueArray;
        this.mRetryCount = 0;

        execute(url);
    }

    public void doGet(SdkHttpListener listener, String url) {
        this.mListener = listener;
        this.mIsHttpPost = false;
        this.mRetryCount = 0;

        execute(url);
    }

    @Override
    protected String doInBackground(String... params) {

        String response = null;
        while (response == null && mRetryCount < MAX_RETRY_TIME) {

            if (isCancelled())
                return null;

            if (mIsHttpPost)
            {
//                response = Util.httpPost(params[0], params[1]);
            }
            else
            {
                response = Util.httpGet(params[0]);
            }

//            Log.d(TAG, this.toString() + "||response=" + response);

            mRetryCount++;
        }

        return response;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if (mListener != null) {
//            Log.d(TAG, this.toString() + "||onCancelled");
            mListener.onCancelled();
            mListener = null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        if (mListener != null && !isCancelled()) {
//            Log.d(TAG, this.toString() + "||onResponse");
            mListener.onResponse(response);
            mListener = null;
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
