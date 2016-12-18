package com.qiong.onesdk;

import android.content.Context;
import android.content.Intent;

/**
 * Created by sric0880 on 2016/9/18.
 */
public interface SDK {
    void onCreate(Context context);
    void onStart();
    void onRestart();
    void onPause();
    void onStop();
    void onResume();
    void onDestroy();
    void onNewIntent( Intent intent);
    void onActivityResult(int requestCode, int resultCode, Intent data);

    void Login();
    void Logout();
    /**
     * 返回订单ID
     */
    String Pay(String data);

    String GetChannelStr();
}
