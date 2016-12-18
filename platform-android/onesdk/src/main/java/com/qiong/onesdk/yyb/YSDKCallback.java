package com.qiong.onesdk.yyb;

import android.util.Log;

import com.qiong.onesdk.OneSDK;
import com.qiong.onesdk.SDKyyb;
import com.tencent.ysdk.framework.common.BaseRet;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.module.pay.PayListener;
import com.tencent.ysdk.module.pay.PayRet;
import com.tencent.ysdk.module.user.UserListener;
import com.tencent.ysdk.module.user.UserLoginRet;
import com.tencent.ysdk.module.user.UserRelationRet;
import com.tencent.ysdk.module.user.WakeupRet;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * TODO GAME 游戏需要根据自己的逻辑实现自己的YSDKCallback对象。 
 * YSDK通过UserListener抽象类中的方法将授权或查询结果回调给游戏。
 * 游戏根据回调结果调整UI等。只有设置回调，游戏才能收到YSDK的响应。
 * 这里是Java层回调(设置了Java层回调会优先调用Java层回调, 如果要使用C++层回调则不能设置Java层回调)
 */
public class YSDKCallback implements UserListener, PayListener {
    public final String LOG_TAG = OneSDK.TAG;
    private SDKyyb sdk;

    public YSDKCallback(SDKyyb sdk) {
        this.sdk = sdk;
    }

    @Override
    public void OnLoginNotify(UserLoginRet ret) {
        Log.d(LOG_TAG,ret.getAccessToken());
        Log.d(LOG_TAG,"ret.flag" + ret.flag);
        Log.d(LOG_TAG,"platform: " + ret.platform);
        Log.d(LOG_TAG,ret.toString());
        String result = "";
        switch (ret.flag) {
            case eFlag.Succ:
                if (ret.ret != BaseRet.RET_SUCC) {
                    Log.d(LOG_TAG, "UserLogin error!!!");
                    sdk.mLoginCb.failed("UserLogin error");
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("openid", ret.open_id);
//                    jsonObject.put("token", ret.token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //TODO
//                sdk.mLoginCb.succeed();
                break;
            // 游戏逻辑，对登录失败情况分别进行处理
            case eFlag.QQ_UserCancel:
                sdk.mLoginCb.failed("用户取消授权，请重试");
                break;
            case eFlag.QQ_LoginFail:
                sdk.mLoginCb.failed("QQ登录失败，请重试");
                break;
            case eFlag.QQ_NetworkErr:
                sdk.mLoginCb.failed("QQ登录异常，请重试");
                break;
            case eFlag.QQ_NotInstall:
                sdk.mLoginCb.failed("手机未安装手Q，请安装后重试");
                break;
            case eFlag.QQ_NotSupportApi:
                sdk.mLoginCb.failed("手机手Q版本太低，请升级后重试");
                break;
            case eFlag.WX_NotInstall:
                sdk.mLoginCb.failed("手机未安装微信，请安装后重试");
                break;
            case eFlag.WX_NotSupportApi:

                sdk.mLoginCb.failed("手机微信版本太低，请升级后重试");
                break;
            case eFlag.WX_UserCancel:
                sdk.mLoginCb.failed("用户取消授权，请重试");
                break;
            case eFlag.WX_UserDeny:
                sdk.mLoginCb.failed("用户拒绝了授权，请重试");
                break;
            case eFlag.WX_LoginFail:
                sdk.mLoginCb.failed("微信登录失败，请重试");
                break;
            case eFlag.Login_TokenInvalid:
                sdk.mLoginCb.failed("您尚未登录或者之前的登录已过期，请重试");
                break;
            case eFlag.Login_NotRegisterRealName:
                sdk.mLoginCb.failed("您的账号没有进行实名认证，请实名认证后重试");
                break;
            default:
                sdk.mLoginCb.failed("Login failed");
                break;
        }
    }

    public void OnWakeupNotify(WakeupRet ret) {
        Log.d(LOG_TAG,"called");
        Log.d(LOG_TAG,"flag:" + ret.flag);
        Log.d(LOG_TAG,"msg:" + ret.msg);
        Log.d(LOG_TAG,"platform:" + ret.platform);
        // TODO GAME 游戏需要在这里增加处理异账号的逻辑
        if (eFlag.Wakeup_YSDKLogining == ret.flag) {
            // 用拉起的账号登录，登录结果在OnLoginNotify()中回调
        } else if (ret.flag == eFlag.Wakeup_NeedUserSelectAccount) {
            // 异账号时，游戏需要弹出提示框让用户选择需要登录的账号
            Log.d(LOG_TAG,"diff account");
            sdk.logout();
        } else if (ret.flag == eFlag.Wakeup_NeedUserLogin) {
            // 没有有效的票据，登出游戏让用户重新登录
            Log.d(LOG_TAG,"need login");
            sdk.logout();
        } else {
            Log.d(LOG_TAG,"logout");
            sdk.logout();
        }
    }

    @Override
    public void OnRelationNotify(UserRelationRet relationRet) {
//    	String result = "";
//        result = result +"flag:" + relationRet.flag + "\n";
//        result = result +"msg:" + relationRet.msg + "\n";
//        result = result +"platform:" + relationRet.platform + "\n";
//        if (relationRet.persons != null && relationRet.persons.size()>0) {
//            PersonInfo personInfo = (PersonInfo)relationRet.persons.firstElement();
//            StringBuilder builder = new StringBuilder();
//            builder.append("UserInfoResponse json: \n");
//            builder.append("nick_name: " + personInfo.nickName + "\n");
//            builder.append("open_id: " + personInfo.openId + "\n");
//            builder.append("userId: " + personInfo.userId + "\n");
//            builder.append("gender: " + personInfo.gender + "\n");
//            builder.append("picture_small: " + personInfo.pictureSmall + "\n");
//            builder.append("picture_middle: " + personInfo.pictureMiddle + "\n");
//            builder.append("picture_large: " + personInfo.pictureLarge + "\n");
//            builder.append("provice: " + personInfo.province + "\n");
//            builder.append("city: " + personInfo.city + "\n");
//            builder.append("country: " + personInfo.country + "\n");
//            result = result + builder.toString();
//        } else {
//            result = result + "relationRet.persons is bad";
//        }
//        Log.d(LOG_TAG,"OnRelationNotify" + result);
//
//        // 发送结果到结果展示界面
//       mainActivity.sendResult(result);
    }

    @Override
    public void OnPayNotify(PayRet ret) {
        Log.d(LOG_TAG,ret.toString());
        if(PayRet.RET_SUCC == ret.ret){
            //支付流程成功
            switch (ret.payState){
                //支付成功
                case PayRet.PAYSTATE_PAYSUCC:
                    Log.i(LOG_TAG,
                            "用户支付成功，支付金额"+ret.realSaveNum+";" +
                            "使用渠道："+ret.payChannel+";" +
                            "发货状态："+ret.provideState+";" +
                            "业务类型："+ret.extendInfo+";建议查询余额："+ret.toString());
                    sdk.mPayCb.succeed(sdk.getPayOrder(), "");
                    break;
                //取消支付
                case PayRet.PAYSTATE_PAYCANCEL:
                    sdk.mPayCb.cancelled(sdk.getPayOrder(), "用户取消支付：" + ret.toString());
                    break;
                //支付结果未知
                case PayRet.PAYSTATE_PAYUNKOWN:
                    sdk.mPayCb.failed(sdk.getPayOrder(), "用户支付结果未知，建议查询余额：" + ret.toString());
                    break;
                //支付失败
                case PayRet.PAYSTATE_PAYERROR:
                default:
                    sdk.mPayCb.failed(sdk.getPayOrder(), "支付异常" + ret.toString());
                    break;
            }
        }else{
            switch (ret.flag){
                case eFlag.Login_TokenInvalid:
                    sdk.mPayCb.failed(sdk.getPayOrder(), "登录态过期，请重新登录：" + ret.toString());
                    break;
                case eFlag.Pay_User_Cancle:
                    //用户取消支付
                    sdk.mPayCb.cancelled(sdk.getPayOrder(), "用户取消支付：" + ret.toString());
                    break;
                case eFlag.Pay_Param_Error:
                    sdk.mPayCb.failed(sdk.getPayOrder(), "支付失败，参数错误: " + ret.toString());
                    break;
                case eFlag.Error:
                default:
                    sdk.mPayCb.failed(sdk.getPayOrder(), "支付异常: " + ret.toString());
                    break;
            }
        }
    }
}

