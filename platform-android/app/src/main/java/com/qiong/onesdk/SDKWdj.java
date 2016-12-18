package com.qiong.onesdk;

import com.qiong.onesdk.wdj.MarioPluginApplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
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
public class SDKWdj implements SDK {
    private WandouGamesApi wandouGamesApi;
    private Activity activity;
    @Override
    public void onCreate(Context context) {
        this.activity = (Activity)context;

        wandouGamesApi = MarioPluginApplication.getWandouGamesApi();

        wandouGamesApi.init(activity);

        wandouGamesApi.registerMessageListener(new OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(MessageEntity entity) {
                Toast.makeText(activity, entity.getMessageContent(), Toast.LENGTH_LONG)
                        .show();

            }
        });

        wandouGamesApi.onCreate(activity);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onPause() {
        wandouGamesApi.onPause(activity);
    }

    @Override
    public void onStop() {
        wandouGamesApi.onStop(activity);
    }

    @Override
    public void onResume() {
        wandouGamesApi.onResume(activity);
    }

    @Override
    public void onDestroy(){}

    @Override
    public void onNewIntent(Intent intent) {
        wandouGamesApi.onNewIntent(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void Login() {
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

                    String success = "succ|" + jsonObject.toString();
                    // success
                    UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", success);
                }
                else{
                    UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", "failed|Login canceled");
                }
            }
        });
    }

    @Override
    public void Logout() {
        wandouGamesApi.logout(new OnLogoutFinishedListener() {
            @Override
            public void onLoginFinished(LogoutFinishType logoutFinishType) {
                if (logoutFinishType == LogoutFinishType.LOGOUT_SUCCESS) {
                    UnityPlayer.UnitySendMessage("SDKMng", "OnLogout", "success");
                }
                else{
                    UnityPlayer.UnitySendMessage("SDKMng", "OnLogout", "failed");
                }
            }
        });
    }

    @Override
    public String Pay(String data) {
        try {
            if (data != null && data.length() > 0) {
//                    Toast.makeText(UnityPlayer.currentActivity, data, Toast.LENGTH_SHORT).show();
                JSONObject json = new JSONObject(data);
                if (null != json) {
                    String description = json.getString("desc");
                    long moneyInFen = json.getLong("moneyInFen");
                    long itemCount = json.getLong("count");
                    String gameTradeNo = json.getString("out_trade_no");
                    wandouGamesApi.pay(activity, description, moneyInFen, itemCount,
                            gameTradeNo, new OnPayFinishedListener() {
                                @Override
                                public void onPaySuccess(PayResult payResult) {
                                    UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "success");
                                }

                                @Override
                                public void onPayFail(PayResult payResult) {
                                    UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|支付异常");
                                }
                            });
                    return gameTradeNo;
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
    public String GetChannelStr()
    {
        return "wandoujia_";
    }
}
