package com.qiong.onesdk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.qiong.onesdk.utils.Util;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import com.qiong.onesdk.wechat.*;
import com.unity3d.player.UnityPlayer;

import java.io.File;

/**
 * Created by sric0880 on 2016/9/23.
 */
public class SDKWeixin implements SDK {
    private IWXAPI api;
    private Context context;
    private static final int THUMB_SIZE = 320;
//    private String openID;

    @Override
    public void onCreate(Context context) {
        api = WXAPIFactory.createWXAPI(context, Constants.APP_ID, true);
        api.registerApp(Constants.APP_ID);

        this.context = context;
        Log.i("Unity", "weixin app id = " + Constants.APP_ID);
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
    public void Login(){
        Log.i("Unity", "weixin Login called");
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_ninja_test";
        if( !api.sendReq(req) )
        {
            Log.i("Unity", "weixin sendRep failed");
        }
        else {
            Log.i("Unity", "weixin sendRep success");
        }
    }

    @Override
    public void Logout() {
        UnityPlayer.UnitySendMessage("SDKMng", "OnLogout", "success");
    }

    @Override
    public String Pay(String data){

        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (isPaySupported) {
            try {
                if (data != null && data.length() > 0) {
//                    Toast.makeText(UnityPlayer.currentActivity, data, Toast.LENGTH_SHORT).show();
                    JSONObject json = new JSONObject(data);
                    if (null != json && !json.has("retcode")) {
                        PayReq req = new PayReq();
                        req.appId = json.getString("appid");
                        req.partnerId = json.getString("partnerid");
                        req.prepayId = json.getString("prepayid");
                        req.nonceStr = json.getString("noncestr");
                        req.timeStamp = json.getString("timestamp");
                        req.packageValue = json.getString("package");
                        req.sign = json.getString("sign");
                        req.extData = "app data"; // optional
//                        Toast.makeText(UnityPlayer.currentActivity, "發送支付請求", Toast.LENGTH_SHORT).show();
                        api.sendReq(req);
                        return req.prepayId;
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
        }
        return "";
    }

    public void ShareText(String text, boolean isTimelineCb)
    {
        Log.i("Unity", "wechat shareText called");
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "text" + System.currentTimeMillis();
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
//            req.openId = openID;
        api.sendReq(req);
    }

    public void SharePicture(String path, boolean isTimelineCb)
    {
        File file = new File(path);
        if (!file.exists()) {
            Log.e("Unity", "path not exists: " + path);
            return;
        }

        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(path);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap bmp = BitmapFactory.decodeFile(path);
        float r = (float)bmp.getHeight() / bmp.getWidth();
        float height = r * THUMB_SIZE;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, (int)height, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "img" + System.currentTimeMillis();
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
//        req.openId = getOpenId();
        api.sendReq(req);
    }

//    public void setOpenId(String openID) {
//        this.openID = openID;
//    }

    @Override
    public String GetChannelStr()
    {
        return "wechat_";
    }
}
