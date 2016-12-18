package com.qiong.onesdk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;

import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sric0800 on 2016/9/18.
 */
public class SDKMi implements SDK {

    @Override
    public void onCreate(Context context) {
        /** SDK初始化 */
        MiAppInfo appInfo = new MiAppInfo();
        appInfo.setAppId("2882303761517509489");
        appInfo.setAppKey("5191750980489");
        MiCommplatform.Init(UnityPlayer.currentActivity.getApplication(), appInfo);
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
    public void onDestroy(){

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void Login() {
        Log.d("Unity", "xiaomi enter Login");
        // 调用SDK执行登陆操作
        MiCommplatform.getInstance().miLogin(UnityPlayer.currentActivity, new OnLoginProcessListener() {
            @Override
            public void finishLoginProcess(int arg0, MiAccountInfo accountInfo) {
                Log.i("Milogin", "finishLoginProcess");

                if (MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS == arg0) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("uid", accountInfo.getUid());
                        jsonObject.put("sessionId", accountInfo.getSessionId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String success = "succ|" + jsonObject.toString();
                    // success
                    UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", success);
                } else if (MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED == arg0) {
                    //logining
                    UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", "failed|logining");
                } else {
                    //login failed
                    UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", "failed|login failed" + arg0);
                }
            }
        });
    }

    @Override
    public void Logout() {
        Log.d("Unity", "xiaomi logout");
        UnityPlayer.UnitySendMessage("SDKMng", "OnLogout", "success");
    }

    @Override
    public String Pay(String data)
    {
        return "";
    }

    @Override
    public String GetChannelStr()
    {
        return "xiaomi_";
    }
}
