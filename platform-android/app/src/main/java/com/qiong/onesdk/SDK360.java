package com.qiong.onesdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.matrix.Matrix;

import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;
import com.qiong.onesdk.qihoo360.QihooPayInfo;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sric0880 on 2016/11/24.
 */

public class SDK360 implements SDK {
    private Activity activity;
//    private QihooUserInfo mQihooUserInfo;
    @Override
    public void onCreate(Context context) {
        this.activity = (Activity) context;
        Matrix.init(this.activity);
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
    public void onDestroy() {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * 生成调用360SDK登录接口的Intent
     * @return intent
     */
    private Intent getLoginIntent() {

        Intent intent = new Intent(this.activity, MainActivity.class);

        // 界面相关参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, true);

        // 必需参数，使用360SDK的登录模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);

        //是否显示关闭按钮
        intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, true);

        // 可选参数，是否支持离线模式，默认值为false
        intent.putExtra(ProtocolKeys.IS_SUPPORT_OFFLINE, false);

        // 可选参数，是否在自动登录的过程中显示切换账号按钮
        intent.putExtra(ProtocolKeys.IS_SHOW_AUTOLOGIN_SWITCH, true);

        // 可选参数，是否隐藏欢迎界面
        intent.putExtra(ProtocolKeys.IS_HIDE_WELLCOME, false);

        // 可选参数，登录界面的背景图片路径，必须是本地图片路径
//        intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTRUE, getUiBackgroundPicPath());
        // 可选参数，指定assets中的图片路径，作为背景图
//        intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTURE_IN_ASSERTS, getUiBackgroundPathInAssets());
        // 可选参数，是否需要用户输入激活码，用于游戏内测阶段。如果不需激活码相关逻辑，客户传false或者不传入该参数。
//        intent.putExtra(ProtocolKeys.NEED_ACTIVATION_CODE, getCheckBoxBoolean(R.id.isNeedActivationCode));

        //-- 以下参数仅仅针对自动登录过程的控制
        // 可选参数，自动登录过程中是否不展示任何UI，默认展示。
//        intent.putExtra(ProtocolKeys.IS_AUTOLOGIN_NOUI, getCheckBoxBoolean(R.id.isAutoLoginHideUI));

        // 可选参数，静默自动登录失败后是否显示登录窗口，默认不显示
//        intent.putExtra(ProtocolKeys.IS_SHOW_LOGINDLG_ONFAILED_AUTOLOGIN, getCheckBoxBoolean(R.id.isShowDlgOnFailedAutoLogin));

        //-- 测试参数，发布时要去掉
//        intent.putExtra(ProtocolKeys.IS_SOCIAL_SHARE_DEBUG, getCheckBoxBoolean(R.id.isDebugSocialShare));

        return intent;
    }

    // 登录、注册的回调
    private IDispatcherCallback mLoginCallback = new IDispatcherCallback() {

        @Override
        public void onFinished(String data) {
            // press back
            if (isCancelLogin(data)) {
                UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", "failed|Login canceled.");
                return;
            }
//            mQihooUserInfo = null;
//            Log.d(TAG, "mLoginCallback, data is " + data);
            // 解析access_token
            String accessToken = parseAccessTokenFromLoginResult(data);

            if (!TextUtils.isEmpty(accessToken)) {
                // 需要去应用的服务器获取用access_token获取一下带qid的用户信息
//                getUserInfo(data, accessToken);
                String success = "succ|" + data;
                UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", success);
            } else {
                UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", "failed|access token is empty.");
            }
        }
    };

    private boolean isCancelLogin(String data) {
        try {
            JSONObject joData = new JSONObject(data);
            int errno = joData.optInt("errno", -1);
            if (-1 == errno) {
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    private String parseAccessTokenFromLoginResult(String loginRes) {
        try {

            JSONObject joRes = new JSONObject(loginRes);
            JSONObject joData = joRes.getJSONObject("data");
            return joData.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    private void getUserInfo(final String data, final String accessToken) {
//
//        final QihooUserInfoTask mUserInfoTask = QihooUserInfoTask.newInstance();
//
//        // 请求应用服务器，用AccessToken换取UserInfo
//        mUserInfoTask.doRequest(this, accessToken, Matrix.getAppKey(this.activity), new QihooUserInfoListener() {
//
//            @Override
//            public void onGotUserInfo(QihooUserInfo userInfo) {
//                if (null == userInfo || !userInfo.isValid()) {
//                    UnityPlayer.UnitySendMessage("LoginMng", "OnSdkLogin", "failed|从应用服务器获取用户信息失败");
//                } else {
//                    SdkUserBaseActivity.this.onGotUserInfo(userInfo);
//
//
//                }
//            }
//        });
//    }

    @Override
    public void Login() {
        IDispatcherCallback callback = mLoginCallback;
        Matrix.execute(this.activity, getLoginIntent(), callback);
    }

    @Override
    public void Logout() {
        Intent intent = new Intent();
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGOUT);
        Matrix.execute(this.activity, intent, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
                UnityPlayer.UnitySendMessage("SDKMng", "OnLogout", "success");
            }
        });
    }

    // 支付的回调
    protected IDispatcherCallback mPayCallback = new IDispatcherCallback() {

        @Override
        public void onFinished(String data) {
//            Log.d(TAG, "mPayCallback, data is " + data);
            if(TextUtils.isEmpty(data)) {
                UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|支付数据为空");
                return;
            }

            boolean isCallbackParseOk = false;
            JSONObject jsonRes;
            try {
                jsonRes = new JSONObject(data);
                // error_code 状态码： 0 支付成功， -1 支付取消， 1 支付失败， -2 支付进行中, 4010201和4009911 登录状态已失效，引导用户重新登录
                // error_msg 状态描述
                int errorCode = jsonRes.optInt("error_code");
                isCallbackParseOk = true;
                switch (errorCode) {
                    case 0:
                        UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "success");
                        return;
                    case 4010201:
                        UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|access token失效，请重新登录再试");
                        return;
                    case 4009911:
                        //QT失效
                        UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|qt失效，请重新登录再试");
                        return;
                    case 1:
                    case -1:
                    case -2:
                    default:
                        UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|" + jsonRes.optString("error_msg"));
                        return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 用于测试数据格式是否异常。
            if (!isCallbackParseOk) {
                UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|支付数据格式异常");
            }
        }
    };

    /***
     * 生成调用360SDK支付接口的Intent
     *
     * @param pay
     * @return Intent
     */
    private Intent getPayIntent(QihooPayInfo pay) {
        Bundle bundle = new Bundle();

        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, true);

        // *** 以下非界面相关参数 ***

        // 设置QihooPay中的参数。

        // 必需参数，360账号id，整数。
        bundle.putString(ProtocolKeys.QIHOO_USER_ID, pay.getQihooUserId());

        // 必需参数，所购买商品金额, 以分为单位。金额大于等于100分，360SDK运行定额支付流程； 金额数为0，360SDK运行不定额支付流程。
        bundle.putString(ProtocolKeys.AMOUNT, pay.getMoneyAmount());

        // 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
        bundle.putString(ProtocolKeys.PRODUCT_NAME, pay.getProductName());

        // 必需参数，购买商品的商品id，应用指定，最大16字符。
        bundle.putString(ProtocolKeys.PRODUCT_ID, pay.getProductId());

        // 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
        bundle.putString(ProtocolKeys.NOTIFY_URI, pay.getNotifyUri());

        // 必需参数，游戏或应用名称，最大16中文字。
        bundle.putString(ProtocolKeys.APP_NAME, pay.getAppName());

        // 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360账号和应用账号，则可用360用户名，最大16中文字。（充值不分区服，
        // 充到统一的用户账户，各区服角色均可使用）。
        bundle.putString(ProtocolKeys.APP_USER_NAME, pay.getAppUserName());

        // 必需参数，应用内的用户id。
        // 若应用内绑定360账号和应用账号，充值不分区服，充到统一的用户账户，各区服角色均可使用，则可用360用户ID最大32字符。
        bundle.putString(ProtocolKeys.APP_USER_ID, pay.getAppUserId());

        // 可选参数，应用扩展信息1，原样返回，最大255字符。
        bundle.putString(ProtocolKeys.APP_EXT_1, pay.getAppExt1());

        // 可选参数，应用扩展信息2，原样返回，最大255字符。
        bundle.putString(ProtocolKeys.APP_EXT_2, pay.getAppExt2());

        // 可选参数，应用订单号，应用内必须唯一，最大32字符。
        bundle.putString(ProtocolKeys.APP_ORDER_ID, pay.getAppOrderId());

        // 必需参数，使用360SDK的支付模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);

        Intent intent = new Intent(this.activity, MainActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    public String Pay(String data) {
        try {
            if (data != null && data.length() > 0) {
                JSONObject json = new JSONObject(data);
                if (null != json) {
                    // 支付基础参数
                    // 创建QihooPay
                    QihooPayInfo qihooPay = new QihooPayInfo();
                    qihooPay.setQihooUserId(json.getString("qihooId"));
                    qihooPay.setMoneyAmount(json.getString("amount"));
                    qihooPay.setExchangeRate("1");

                    qihooPay.setProductName(json.getString("productName"));
                    qihooPay.setProductId(json.getString("productId"));

                    qihooPay.setNotifyUri(json.getString("callbackUrl"));

                    qihooPay.setAppName(this.activity.getString(R.string.app_name));

                    qihooPay.setAppUserName(json.getString("qihooName"));
                    qihooPay.setAppUserId(json.getString("qihooId"));

                    // 可选参数
                    String orderId = json.getString("orderId");
                    qihooPay.setAppOrderId(orderId);

                    Intent intent = getPayIntent(qihooPay);

                    // 启动接口
                    Matrix.invokeActivity(this.activity, intent, mPayCallback);

                    return orderId;
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

    @Override
    public String GetChannelStr() {
        return "360_";
    }
}
