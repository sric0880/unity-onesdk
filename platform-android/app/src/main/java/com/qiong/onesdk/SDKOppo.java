package com.qiong.onesdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.ApiCallback;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.nearme.game.sdk.common.model.biz.PayInfo;
import com.nearme.platform.opensdk.pay.PayResponse;
import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

/**
 * Created by sric0880 on 2016/11/24.
 */

public class SDKOppo implements SDK {
    private Activity activity;
    @Override
    public void onCreate(Context context) {
        this.activity = (Activity)context;
        String appSecret = "e2eCa732422245E8891F6555e999878B";
        GameCenterSDK.init(appSecret, context);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onPause() {
        GameCenterSDK.getInstance().onPause();
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {
        GameCenterSDK.getInstance().onResume(this.activity);
    }

    @Override
    public void onDestroy() {
        GameCenterSDK.getInstance().onExit(this.activity,
                new GameExitCallback() {

                    @Override
                    public void exitGame() {
                    }
                });
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void Login() {
        GameCenterSDK.getInstance().doLogin(this.activity, new ApiCallback() {

            @Override
            public void onSuccess(String resultMsg) {
                GameCenterSDK.getInstance().doGetTokenAndSsoid(new ApiCallback() {

                    @Override
                    public void onSuccess(String resultMsg) {
                        String success = "succ|" + resultMsg;
                        UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", success);
                    }

                    @Override
                    public void onFailure(String content, int resultCode) {
                        UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", "failed|Get token failed, error:" + content);
                    }
                });
            }

            @Override
            public void onFailure(String resultMsg, int resultCode) {
                UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", "failed|Login failed, error:" + resultMsg);
            }
        });
    }

    @Override
    public void Logout() {
        UnityPlayer.UnitySendMessage("SDKMng", "OnLogout", "success");
    }

    @Override
    public String Pay(String data) {
        try {
            if (data != null && data.length() > 0) {
//                    Toast.makeText(UnityPlayer.currentActivity, data, Toast.LENGTH_SHORT).show();
                JSONObject json = new JSONObject(data);
                if (null != json) {
                    String order = json.getString("order");
                    String attach = json.getString("attach");
                    int moneyInFen = json.getInt("amount");
                    String productName = json.getString("productName");
                    String productDesc = json.getString("productDesc");
                    String callback = json.getString("callbackUrl");
                    // CP 支付参数
                    PayInfo payInfo = new PayInfo(order, attach, moneyInFen);
                    payInfo.setProductDesc(productDesc);
                    payInfo.setProductName(productName);
                    payInfo.setCallbackUrl(callback);

                    GameCenterSDK.getInstance().doPay(this.activity, payInfo, new ApiCallback() {

                        @Override
                        public void onSuccess(String resultMsg) {
                            UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "success");
                        }

                        @Override
                        public void onFailure(String resultMsg, int resultCode) {
                            if (PayResponse.CODE_CANCEL != resultCode) {
                                UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|支付失败");
                            } else {
                                // 取消支付处理
                                UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|支付取消");
                            }
                        }
                    });
                    return order;
                } else {
                    UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|返回订单数据格式错误");
                }
            } else {
                UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|返回订单数据错误");
            }
        }
        catch (Exception e)
        {
            UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|返回订单数据错误");
        }
        return "";
    }

    @Override
    public String GetChannelStr() {
        return "oppo_";
    }
}
