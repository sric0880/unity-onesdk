package com.qiong.onesdk;

import android.util.Log;

import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sric0800 on 2016/9/18.
 */
public class SDKxiaomi extends SDK {

    private final static String APP_ID = "";
    private final static String APP_KEY = "";

    public SDKxiaomi(OneSDK oneSDK){
        super(oneSDK);
    }

    @Override
    public void onCreate() {
        /** SDK初始化 */
        MiAppInfo appInfo = new MiAppInfo();
        appInfo.setAppId(APP_ID);
        appInfo.setAppKey(APP_KEY);
        MiCommplatform.Init(mActivity.getApplication(), appInfo);
    }

    @Override
    public void login() {
        MiCommplatform.getInstance().miLogin(mActivity, new OnLoginProcessListener() {
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

                    //TODO
//                    mLoginCb.succeed();
                } else if (MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED == arg0) {
                    mLoginCb.failed("正在登录中");
                } else {
                    mLoginCb.failed("登录失败，错误码："+arg0);
                }
            }
        });
    }

    @Override
    public void pay(String data)
    {
    }

    @Override
    public String getChannelStr()
    {
        return "xiaomi";
    }
}
