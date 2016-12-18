package com.qiong.onesdk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by sric0880 on 2016/12/17.
 */
public class OneSDK {

    public final static String TAG = "Onesdk";

    private SDK mSdk;
    private Context mContext;
    private int mChannelID = 0;
    private String mVersionName = "";
    private int mVersionCode = 0;
    private String mPackageName = "";

    /**
     * 下面添加新的渠道
     */
    private final static HashMap<Integer, Class> ChannelClasses = new HashMap<Integer, Class>(){
        {
            put(1, SDKxiaomi.class);
            put(2, SDKweixin.class);
            put(3, SDKwdj.class);
            put(4, SDKyyb.class);
            put(5, SDKvivo.class);
            put(6, SDKhuawei.class);
            put(7, SDK360.class);
            put(8, SDKoppo.class);
            put(9, SDKbaidu.class);
        }
    };

    private static OneSDK onesdk = null;
    public static OneSDK getInstance(Context context)
    {
        if (onesdk == null)
        {
            onesdk = new OneSDK(context);
        }
        return onesdk;
    }

    private OneSDK(Context context) {
        this.mContext = context;
        mPackageName = mContext.getPackageName();
        PackageManager packageMan = mContext.getPackageManager();
        try {
            PackageInfo pInfo = packageMan.getPackageInfo(mPackageName, 0);
            mVersionName = pInfo.versionName;
            mVersionCode = pInfo.versionCode;
            ApplicationInfo appInfo = packageMan.getApplicationInfo(mPackageName, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                mChannelID = appInfo.metaData.getInt("channel_id");
                Log.i(TAG, "Channel id is " + mChannelID);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (mChannelID != 0){
            if (ChannelClasses.containsKey(mChannelID)){
                Class c = ChannelClasses.get(mChannelID);
                try {
                    Constructor<SDK> ctr = c.getConstructor(OneSDK.class);
                    this.mSdk = ctr.newInstance(this);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setLogoutCallback(SDKInterface.LogoutCallback logoutCb) {
        if (mSdk != null) {
            mSdk.mLogoutCb = logoutCb;
        }
    }

    public int getChannelID() {
        return mChannelID;
    }

    public String getVersionName() {
        return  mVersionName;
    }

    public int GetVersionCode() {
        return mVersionCode;
    }

    public String getPackageName()
    {
        return mPackageName;
    }

    public void onCreate()
    {
        if (mSdk != null){
            mSdk.onCreate();
        }
    }

    public void onStart()
    {
        if (mSdk != null)
        {
            mSdk.onStart();
        }
    }

    public void onRestart()
    {
        if (mSdk != null)
        {
            mSdk.onRestart();
        }
    }

    public void onPause()
    {
        if (mSdk != null)
        {
            mSdk.onPause();
        }
    }

    public void onStop()
    {
        if (mSdk != null){
            mSdk.onStop();
        }
    }

    public void onResume()
    {
        if (mSdk != null){
            mSdk.onResume();
        }
    }

    public void onDestroy()
    {
        if (mSdk != null){
            mSdk.onDestroy();
        }
    }

    public void onNewIntent(Intent intent){
        if (mSdk != null){
            mSdk.onNewIntent(intent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (mSdk != null){
            mSdk.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void login(SDKInterface.LoginCallback loginCb){
        if (mSdk != null){
            mSdk.mLoginCb = loginCb;
            mSdk.login();
        }
    }

    public void logout() {
        if (mSdk != null){
            mSdk.logout();
        }
    }

    public void pay(String json, SDKInterface.PayCallback callback){
        if (mSdk != null){
            mSdk.mPayCb = callback;
            mSdk.pay(json);
        }
    }

    public String getChannelStr(){
        if (mSdk != null)
        {
            return mSdk.getChannelStr();
        }
        return "undefined_";
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 不需要用户自己调用
     */
    public static void onLoginFailed(String msg){
        if (onesdk != null)
        {
            if (onesdk.mSdk.mLoginCb != null){
                onesdk.mSdk.mLoginCb.failed(msg);
            }
        }
    }

    /**
     * 不需要用户自己调用
     */
    public static void onLoginSuccess(String userId, String token, String password, String msg){
        if (onesdk != null)
        {
            if (onesdk.mSdk.mLoginCb != null){
                onesdk.mSdk.mLoginCb.succeed(userId, token, password, msg);
            }
        }
    }

    /**
     * 不需要用户自己调用
     */
    public static void onPayFailed(String msg)
    {
        if (onesdk != null)
        {
            if (onesdk.mSdk.mPayCb != null){
                onesdk.mSdk.mPayCb.failed(onesdk.mSdk.getPayOrder(), msg);
            }
        }
    }

    /**
     * 不需要用户自己调用
     */
    public static void onPaySuccess(String msg)
    {
        if (onesdk != null)
        {
            if (onesdk.mSdk.mPayCb != null){
                onesdk.mSdk.mPayCb.succeed(onesdk.mSdk.getPayOrder(), msg);
            }
        }
    }

    /**
     * 不需要用户自己调用
     */
    public static void onPayCanceled(String msg)
    {
        if (onesdk != null)
        {
            if (onesdk.mSdk.mPayCb != null){
                onesdk.mSdk.mPayCb.cancelled(onesdk.mSdk.getPayOrder(), msg);
            }
        }
    }
}
