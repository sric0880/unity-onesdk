package com.qiong.onesdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by sric0880 on 2016/11/22.
 */
import com.qiong.onesdk.yyb.YSDKCallback;
import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.ePlatform;
import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

public class SDKYyb implements SDK {
    private Activity activity;
    private YSDKCallback callback;
    private ePlatform subChannel;
    @Override
    public void onCreate(Context context) {
        this.activity = (Activity)context;
        this.callback = new YSDKCallback(this);
        this.subChannel = ePlatform.WX;
        YSDKApi.onCreate(this.activity);
        YSDKApi.setUserListener(this.callback);
        YSDKApi.handleIntent(this.activity.getIntent());
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onRestart() {
        YSDKApi.onRestart(this.activity);
    }

    @Override
    public void onPause() {
        YSDKApi.onPause(this.activity);
    }

    @Override
    public void onStop() {
        YSDKApi.onStop(this.activity);
    }

    @Override
    public void onResume() {
        YSDKApi.onResume(this.activity);
    }

    @Override
    public void onDestroy() {
        YSDKApi.onDestroy(this.activity);
    }

    @Override
    public void onNewIntent(Intent intent) {
        YSDKApi.handleIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        YSDKApi.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void Login() {
        Log.i("Unity", "yyb start login with subchannel "+ this.subChannel.platformStr());
        YSDKApi.login(this.subChannel);
    }

    public void SetSubChannel(int subChannel)
    {
        this.subChannel = ePlatform.getEnum(subChannel);
    }

    @Override
    public void Logout() {
        YSDKApi.logout();
        UnityPlayer.UnitySendMessage("SDKMng", "OnLogout", "success");
    }

    @Override
    public String Pay(String data) {
        try {
            if (data != null && data.length() > 0) {
//                    Toast.makeText(UnityPlayer.currentActivity, data, Toast.LENGTH_SHORT).show();
                JSONObject json = new JSONObject(data);
                if (null != json) {
                    String zoneId = json.getString("zoneId");
                    String url = json.getString("goodsTokenUrl");
//                    Bitmap bmp = BitmapFactory.decodeResource(mMainActivity.getResources(), R.drawable.sample_yuanbao);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                    byte[] appResData = baos.toByteArray();
                    String ysdkExt = "ysdkExt";
                    YSDKApi.buyGoods(zoneId,url,null,ysdkExt,this.callback);
                    return url;
                } else {
                    UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|返回订单数据格式错误");
//                        Toast.makeText(UnityPlayer.currentActivity, "返回的json錯誤：" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                }
            } else {
                UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|返回订单数据错误");
//                    Toast.makeText(UnityPlayer.currentActivity, "httpGet failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|返回订单数据错误");
//                Toast.makeText(UnityPlayer.currentActivity, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    // 获取当前登录平台
//    public ePlatform getPlatform() {
//        UserLoginRet ret = new UserLoginRet();
//        YSDKApi.getLoginRecord(ret);
//        if (ret.flag == eFlag.Succ) {
//            return ePlatform.getEnum(ret.platform);
//        }
//        return ePlatform.None;
//    }

    @Override
    public String GetChannelStr()
    {
        return "yingyongbao_";
    }
}
