package com.qiong.onesdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.unity3d.player.UnityPlayer;

/**
 * Created by sric0880 on 2016/11/24.
 */

public class SDKBaidu implements SDK {
    private Activity activity;
    @Override
    public void onCreate(Context context) {
        this.activity = (Activity)context;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void Login() {

    }

    @Override
    public void Logout() {
        UnityPlayer.UnitySendMessage("SDKMng", "OnLogout", "success");
    }

    @Override
    public String Pay(String data) {
        return null;
    }

    @Override
    public String GetChannelStr() {
        return "baidu_";
    }
}
