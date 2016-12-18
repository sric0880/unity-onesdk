package com.qiong.onesdk;

import android.content.Context;
import android.content.Intent;

/**
 * Created by sric0880 on 2016/9/18.
 */
public class SDKManager {

    private SDK sdk;
    private int channelID;
    private Context mContext;

    private static SDKManager manager = null;

    private SDKManager(Context context, int channelID)
    {
        this.mContext = context;
        this.channelID = channelID;
        switch (channelID){
            case 1:
                sdk = new SDKMi();
                break;
            case 2:
                sdk = new SDKWeixin();
                break;
            case 3:
                sdk = new SDKWdj();
                break;
            case 5:
                sdk = new SDKYyb();
                break;
            case 6:
                sdk = new SDKVivo();
                break;
            case 7:
                sdk = new SDKHuawei();
                break;
            case 8:
                sdk = new SDK360();
                break;
            case 9:
                sdk = new SDKOppo();
                break;
            case 10:
                sdk = new SDKBaidu();
                break;
        }
    }

    public static SDKManager GetInstance(Context context, int channelID)
    {
        if (manager == null){
            manager = new SDKManager(context, channelID);
        }
        return manager;
    }

    public void onCreate()
    {
        if (sdk != null){
            sdk.onCreate(mContext);
        }
    }

    public void onStart()
    {
        if (sdk != null)
        {
            sdk.onStart();
        }
    }

    public void onRestart()
    {
        if (sdk != null)
        {
            sdk.onRestart();
        }
    }

    public void onPause()
    {
        if (sdk != null)
        {
            sdk.onPause();
        }
    }

    public void onStop()
    {
        if (sdk != null){
            sdk.onStop();
        }
    }

    public void onResume()
    {
        if (sdk != null){
            sdk.onResume();
        }
    }

    public void onDestroy()
    {
        if (sdk != null){
            sdk.onDestroy();
        }
    }

    public void onNewIntent(Intent intent){
        if (sdk != null){
            sdk.onNewIntent(intent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (sdk != null){
            sdk.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void Login(){
        if (sdk != null){
            sdk.Login();
        }
    }

    public void Logout() {
        if (sdk != null){
            sdk.Logout();
        }
    }

    public String Pay(String data){
        if (sdk != null){
            return sdk.Pay(data);
        }
        return "";
    }

    public String GetChannelStr(){
        if (sdk != null)
        {
            return sdk.GetChannelStr();
        }
        return "undefined_";
    }
}
