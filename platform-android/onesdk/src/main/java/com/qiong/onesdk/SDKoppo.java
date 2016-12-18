package com.qiong.onesdk;

import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.ApiCallback;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.nearme.game.sdk.common.model.biz.PayInfo;
import com.nearme.platform.opensdk.pay.PayResponse;

import org.json.JSONObject;

/**
 * Created by sric0880 on 2016/11/24.
 */

public class SDKoppo extends SDK {
    private static final String APP_SECRET = "e2eCa732422245E8891F6555e999878B";

    public SDKoppo(OneSDK oneSDK){
        super(oneSDK);
    }
    @Override
    public void onCreate() {
        GameCenterSDK.init(APP_SECRET, this.mActivity);
    }

    @Override
    public void onPause() {
        GameCenterSDK.getInstance().onPause();
    }

    @Override
    public void onResume() {
        GameCenterSDK.getInstance().onResume(this.mActivity);
    }

    @Override
    public void onDestroy() {
        GameCenterSDK.getInstance().onExit(this.mActivity,
                new GameExitCallback() {

                    @Override
                    public void exitGame() {
                    }
                });
    }

    @Override
    public void login() {
        GameCenterSDK.getInstance().doLogin(this.mActivity, new ApiCallback() {

            @Override
            public void onSuccess(String resultMsg) {
                GameCenterSDK.getInstance().doGetTokenAndSsoid(new ApiCallback() {

                    @Override
                    public void onSuccess(String resultMsg) {
                        //TODO
//                        mLoginCb.succeed();
                    }

                    @Override
                    public void onFailure(String content, int resultCode) {
                        mLoginCb.failed(content);
                    }
                });
            }

            @Override
            public void onFailure(String resultMsg, int resultCode) {
                mLoginCb.failed(resultMsg);
            }
        });
    }

    @Override
    public void pay(String data) {
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

                    GameCenterSDK.getInstance().doPay(this.mActivity, payInfo, new ApiCallback() {

                        @Override
                        public void onSuccess(String resultMsg) {
                            mPayCb.succeed(mPayOrder, "");
                        }

                        @Override
                        public void onFailure(String resultMsg, int resultCode) {
                            if (PayResponse.CODE_CANCEL != resultCode) {
                                mPayCb.failed(mPayOrder, "支付失败");
                            } else {
                                mPayCb.cancelled(mPayOrder, "支付取消");
                            }
                        }
                    });
                    mPayOrder = order;
                } else {
                    mPayCb.failed(mPayOrder, "返回订单数据格式错误");
                }
            } else {
                mPayCb.failed(mPayOrder, "返回订单数据格式错误");
            }
        }
        catch (Exception e)
        {
            mPayCb.failed(mPayOrder, "返回订单数据格式错误");
        }
    }

    @Override
    public String getChannelStr() {
        return "oppo";
    }
}
