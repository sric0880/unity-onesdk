package com.qiong.onesdk;

import com.qiong.onesdk.wdj.MarioPluginApplication;

import android.content.Intent;
import android.widget.Toast;

import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnLoginFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnLogoutFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnMessageReceivedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnPayFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.model.LoginFinishType;
import com.wandoujia.mariosdk.plugin.api.model.model.LogoutFinishType;
import com.wandoujia.mariosdk.plugin.api.model.model.MessageEntity;
import com.wandoujia.mariosdk.plugin.api.model.model.PayResult;
import com.wandoujia.mariosdk.plugin.api.model.model.UnverifiedPlayer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sric0880 on 2016/10/17.
 */
public class SDKwdj extends SDK {
    private WandouGamesApi wandouGamesApi;

    public SDKwdj(OneSDK oneSDK){
        super(oneSDK);
    }
    @Override
    public void onCreate() {
        wandouGamesApi = MarioPluginApplication.getWandouGamesApi();

        wandouGamesApi.init(this.mActivity);

        wandouGamesApi.registerMessageListener(new OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(MessageEntity entity) {
                Toast.makeText(mActivity, entity.getMessageContent(), Toast.LENGTH_LONG)
                        .show();

            }
        });

        wandouGamesApi.onCreate(this.mActivity);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onPause() {
        wandouGamesApi.onPause(this.mActivity);
    }

    @Override
    public void onStop() {
        wandouGamesApi.onStop(this.mActivity);
    }

    @Override
    public void onResume() {
        wandouGamesApi.onResume(this.mActivity);
    }

    @Override
    public void onDestroy(){}

    @Override
    public void onNewIntent(Intent intent) {
        wandouGamesApi.onNewIntent(this.mActivity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void login() {
        wandouGamesApi.login(new OnLoginFinishedListener() {
            @Override
            public void onLoginFinished(LoginFinishType loginFinishType, UnverifiedPlayer unverifiedPlayer) {
                if (loginFinishType != LoginFinishType.CANCEL) {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("uid", unverifiedPlayer.getId());
                        jsonObject.put("token", unverifiedPlayer.getToken());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // success
                    //TODO
//                    mLoginCb.succeed();
                }
                else{
                    mLoginCb.cancelled();
                }
            }
        });
    }

    @Override
    public void logout() {
        wandouGamesApi.logout(new OnLogoutFinishedListener() {
            @Override
            public void onLoginFinished(LogoutFinishType logoutFinishType) {
                if (logoutFinishType == LogoutFinishType.LOGOUT_SUCCESS) {
                    mLogoutCb.succeed();
                }
                else{
                    mLogoutCb.failed("");
                }
            }
        });
    }

    @Override
    public void pay(String data) {
        try {
            if (data != null && data.length() > 0) {
                JSONObject json = new JSONObject(data);
                if (null != json) {
                    String description = json.getString("desc");
                    long moneyInFen = json.getLong("moneyInFen");
                    long itemCount = json.getLong("count");
                    String gameTradeNo = json.getString("out_trade_no");
                    wandouGamesApi.pay(this.mActivity, description, moneyInFen, itemCount,
                            gameTradeNo, new OnPayFinishedListener() {
                                @Override
                                public void onPaySuccess(PayResult payResult) {
                                    mPayCb.succeed(mPayOrder, "");
                                }

                                @Override
                                public void onPayFail(PayResult payResult) {
                                    mPayCb.failed(mPayOrder, "支付异常");
                                }
                            });
                    mPayOrder = gameTradeNo;
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
    public String getChannelStr()
    {
        return "wandoujia";
    }
}
