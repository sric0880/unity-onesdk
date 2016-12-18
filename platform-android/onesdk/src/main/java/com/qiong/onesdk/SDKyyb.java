package com.qiong.onesdk;

import android.content.Intent;
import android.util.Log;

/**
 * Created by sric0880 on 2016/11/22.
 */
import com.qiong.onesdk.yyb.YSDKCallback;
import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.ePlatform;

import org.json.JSONObject;

public class SDKyyb extends SDK {
    private YSDKCallback callback;
    private ePlatform subChannel;

    public SDKyyb(OneSDK oneSDK){
        super(oneSDK);
    }
    @Override
    public void onCreate() {
        this.callback = new YSDKCallback(this);
        this.subChannel = ePlatform.WX;
        YSDKApi.onCreate(this.mActivity);
        YSDKApi.setUserListener(this.callback);
        YSDKApi.handleIntent(this.mActivity.getIntent());
    }

    @Override
    public void onRestart() {
        YSDKApi.onRestart(this.mActivity);
    }

    @Override
    public void onPause() {
        YSDKApi.onPause(this.mActivity);
    }

    @Override
    public void onStop() {
        YSDKApi.onStop(this.mActivity);
    }

    @Override
    public void onResume() {
        YSDKApi.onResume(this.mActivity);
    }

    @Override
    public void onDestroy() {
        YSDKApi.onDestroy(this.mActivity);
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
    public void login() {
        Log.i("Unity", "yyb start login with subchannel "+ this.subChannel.platformStr());
        YSDKApi.login(this.subChannel);
    }

    public void SetSubChannel(int subChannel)
    {
        this.subChannel = ePlatform.getEnum(subChannel);
    }

    @Override
    public void logout() {
        YSDKApi.logout();
        super.logout();
    }

    @Override
    public void pay(String data) {
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
                    mPayOrder = url;
                } else {
                    mPayCb.failed(mPayOrder, "返回订单数据格式错误");
                }
            } else {
                mPayCb.failed(mPayOrder, "返回订单数据格式错误");
            }
        } catch (Exception e) {
            mPayCb.failed(mPayOrder, "返回订单数据格式错误");
        }
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
    public String getChannelStr()
    {
        return "yingyongbao";
    }
}
