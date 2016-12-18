package com.qiong.onesdk.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.qiong.onesdk.OneSDK;
import com.qiong.onesdk.SDKweixin;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, SDKweixin.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            Log.i("Unity", "weixin errcode: " + resp.errCode);
            switch (resp.errCode){
                case BaseResp.ErrCode.ERR_OK:
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("code", ((SendAuth.Resp)resp).code);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //TODO
//                    OneSDK.onLoginSuccess();
                    break;
                default:
                    OneSDK.onLoginFailed("微信登录失败，" + resp.errStr + " 错误码: " + resp.errCode);
                    break;
            }
        }
//        else {
//            UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", "failed");
//        }
        finish();
    }
}
