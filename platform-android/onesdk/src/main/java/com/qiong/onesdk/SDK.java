package com.qiong.onesdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sric0880 on 2016/9/18.
 */
public abstract class SDK {
    protected OneSDK mOneSDK;
    protected Activity mActivity;
    protected String mPayOrder; //支付订单号
    public SDK(OneSDK oneSDK) {
        this.mOneSDK = oneSDK;
        this.mActivity = (Activity) oneSDK.getContext();
    }
    public SDKInterface.LoginCallback mLoginCb;
    public SDKInterface.LogoutCallback mLogoutCb;
    public SDKInterface.PayCallback mPayCb;

    public void onCreate() {};
    public void onStart() {};
    public void onRestart() {};
    public void onPause() {};
    public void onStop() {};
    public void onResume() {};
    public void onDestroy() {};
    public void onNewIntent( Intent intent) {};
    public void onActivityResult(int requestCode, int resultCode, Intent data){};

    public abstract void login();
    public void logout() { mLogoutCb.succeed(); }
    public String getPayOrder() { return mPayOrder;}

    /**
     * 支付接口
     * @param json 订单详细信息（包括订单号）
     */
    public abstract void pay(String json);

    /**
     * 获取渠道名称
     * @return 渠道名称
     */
    public abstract String getChannelStr();
}
