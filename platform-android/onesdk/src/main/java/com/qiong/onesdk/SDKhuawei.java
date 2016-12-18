package com.qiong.onesdk;

import android.content.res.Configuration;
import android.util.Log;

import com.android.huawei.pay.plugin.PayParameters;
import com.android.huawei.pay.util.HuaweiPayUtil;
import com.android.huawei.pay.util.Rsa;
import com.huawei.gameservice.sdk.GameServiceSDK;
import com.huawei.gameservice.sdk.api.GameEventHandler;
import com.huawei.gameservice.sdk.api.PayResult;
import com.huawei.gameservice.sdk.api.Result;
import com.huawei.gameservice.sdk.api.UserResult;
import com.huawei.gameservice.sdk.util.LogUtil;
import com.qiong.onesdk.utils.RSAUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by sric0880 on 2016/11/21.
 */

public class SDKhuawei extends SDK {
    public static final String APP_ID = "";
    /**
     * private key for buoy, the CP need to save the key value on the server for security
     */
    /**************TODO:DELETE*******************/
    public static String BUOY_SECRET = "";
    /**
     * 支付ID
     */
    public static final String PAY_ID = "";
    /**
     * 支付公钥
     */
    public static final String PAY_RSA_PUBLIC = "";
    /**
     * 登录签名公钥
     */
    public static final String LOGIN_RSA_PUBLIC = "";

    public static final String USER_ID = "userID";

    public static final String APPLICATION_ID = "applicationID";

    public static final String AMOUNT = "amount";

    public static final String PRODUCT_NAME = "productName";

    public static final String PRODUCT_DESC = "productDesc";

    public static final String REQUEST_ID = "requestId";

    public static final String USER_NAME = "userName";

    public static final String SIGN = "sign";

//    public static final String NOTIFY_URL = "notifyUrl";

    public static final String SERVICE_CATALOG = "serviceCatalog";

    public static final String SHOW_LOG = "showLog";

    public static final String SCREENT_ORIENT = "screentOrient";

//    public static final String SDK_CHANNEL = "sdkChannel";

//    public static final String URL_VER = "urlver";

    private boolean initOk = false;

    public SDKhuawei(OneSDK oneSDK){
        super(oneSDK);
    }
    /**
     * 生成游戏签名
     * generate the game sign
     */
    private String createGameSign(String data){

        // 为了安全把浮标密钥放到服务端，并使用https的方式获取下来存储到内存中，CP可以使用自己的安全方式处理
        // For safety, buoy key put into the server and use the https way to get down into the client's memory.
        // By the way CP can also use their safe approach.

        String str = data;
        try {
            String result = RSAUtil.sha256WithRsa(str.getBytes("UTF-8"), BUOY_SECRET);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 校验签名
     * check the
     */
//    protected boolean checkSign(String data, String gameAuthSign)
//    {
//    	/*
//         * 建议CP获取签名后去游戏自己的服务器校验签名
//         */
//    	/*
//         * The CP need to deployed a server for checking the sign.
//         */
//        try
//        {
//            return RSAUtil.verify(data.getBytes("UTF-8"), LOGIN_RSA_PUBLIC, gameAuthSign);
//        }
//        catch (Exception e)
//        {
//            return false;
//        }
//    }

    @Override
    public void onCreate() {
        init(false);
    }

    private void init(final boolean goLogin)
    {
        GameServiceSDK.init(this.mActivity, APP_ID, PAY_ID, "com.huawei.gb.huawei.installnewtype.provider", new GameEventHandler(){

            @Override
            public void onResult(Result result) {
                if(result.rtnCode != Result.RESULT_OK){
                    initOk = false;
                    Log.i("Unity", "init the game service SDK failed:" + result.rtnCode);
                    return;
                }
                initOk = true;
                if (goLogin)
                {
                    login();
                }
                checkUpdate();
            }

            @Override
            public String getGameSign(String appId, String cpId, String ts){
                return createGameSign(appId+cpId+ts);
            }

        });
    }

    /**
     * 检测游戏更新
     * check the update for game
     */
    private void checkUpdate()
    {
        GameServiceSDK.checkUpdate(this.mActivity, new GameEventHandler(){

            @Override
            public void onResult(Result result) {
                if(result.rtnCode != Result.RESULT_OK){
                    Log.i("Unity", "check update failed:" + result.rtnCode);
                }
                Log.i("Unity", "channel huawei has new version, go and update it");
            }

            @Override
            public String getGameSign(String appId, String cpId, String ts){
                return createGameSign(appId+cpId+ts);
            }

        });
    }

    @Override
    public void onPause() {
        GameServiceSDK.hideFloatWindow(this.mActivity);
    }

    @Override
    public void onResume() {
        GameServiceSDK.showFloatWindow(this.mActivity);
    }

    @Override
    public void onDestroy(){
        GameServiceSDK.destroy(this.mActivity);
    }

    @Override
    public void login() {
        if (initOk)
        {
            GameServiceSDK.login(this.mActivity, new GameEventHandler(){

                @Override
                public void onResult(Result result) {
                    if (result.rtnCode != Result.RESULT_OK) {
                        mLoginCb.failed(result.toString());
                    } else {
                        UserResult userResult = (UserResult) result;
                        if(userResult.isAuth != null && userResult.isAuth == 1)
                        {
                            LoginSuccess(userResult);

                        }else if(userResult.isChange != null && userResult.isChange == 1){
                            mLogoutCb.succeed();
                        }
                        else
                        {
                            LoginSuccess(userResult);
                        }
                    }
                }

                @Override
                public String getGameSign(String appId, String cpId, String ts){
                    return createGameSign(appId+cpId+ts);
                }

            }, 1);
        }
        else
        {
            init(true);
        }
    }

    private void LoginSuccess(UserResult userResult) {
        Log.i("Unity", "huawei login auth success:" + userResult.toString());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", userResult.playerId);
            jsonObject.put("sign", userResult.gameAuthSign);
            jsonObject.put("ts", userResult.ts);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //TODO
//        mLoginCb.succeed();
    }

    private GameEventHandler payHandler  = new GameEventHandler() {
            @Override
            public String getGameSign(String appId, String cpId, String ts) {
                return null;
            }

            @Override
            public void onResult(Result result) {
                Map<String, String> payResp = ((PayResult) result).getResultMap();
                // 支付成功，进行验签
                // payment successful, then check the response value
                if ("0".equals(payResp.get(PayParameters.returnCode))) {
                    if ("success".equals(payResp.get(PayParameters.errMsg))) {
                        // 支付成功，验证信息的安全性；待验签字符串中如果有isCheckReturnCode参数且为yes，则去除isCheckReturnCode参数
                        // If the response value contain the param "isCheckReturnCode" and its value is yes, then remove the param "isCheckReturnCode".
                        if (payResp.containsKey("isCheckReturnCode") && "yes".equals(payResp.get("isCheckReturnCode"))) {
                            payResp.remove("isCheckReturnCode");

                        }
                        // 支付成功，验证信息的安全性；待验签字符串中如果没有isCheckReturnCode参数活着不为yes，则去除isCheckReturnCode和returnCode参数
                        // If the response value does not contain the param "isCheckReturnCode" and its value is yes, then remove the param "isCheckReturnCode".
                        else {
                            payResp.remove("isCheckReturnCode");
                            payResp.remove(PayParameters.returnCode);
                        }
                        // 支付成功，验证信息的安全性；待验签字符串需要去除sign参数
                        // remove the param "sign" from response
                        String sign = payResp.remove(PayParameters.sign);

                        String noSigna = HuaweiPayUtil.getSignData(payResp);

                        // 使用公钥进行验签
                        // check the sign using RSA public key
                        boolean s = Rsa.doCheck(noSigna, sign, PAY_RSA_PUBLIC);

                        if (s) {
                            mPayCb.succeed(mPayOrder, "");
                        } else {
                            mPayCb.failed(mPayOrder, "支付验证失败");
                        }
                    }

                } else if ("30002".equals(payResp.get(PayParameters.returnCode))) {
                    mPayCb.failed(mPayOrder, "支付超时");
                }
            }
        };

    private void _pay(
            final String price,
            final String productName,
            final String productDesc,
            final String requestId,
            final String sign,
            final GameEventHandler handler)
    {
//        Map<String, String> params = new HashMap<String, String>();
//        // 必填字段，不能为null或者""，请填写从联盟获取的支付ID
//        // the pay ID is required and can not be null or ""
//        params.put(USER_ID, PAY_ID);
//        // 必填字段，不能为null或者""，请填写从联盟获取的应用ID
//        // the APP ID is required and can not be null or ""
//        params.put(APPLICATION_ID, APP_ID);
//        // 必填字段，不能为null或者""，单位是元，精确到小数点后两位，如1.00
//        // the amount (accurate to two decimal places) is required
//        params.put(AMOUNT, price);
//        // 必填字段，不能为null或者""，道具名称
//        // the product name is required and can not be null or ""
//        params.put(PRODUCT_NAME, productName);
//        // 必填字段，不能为null或者""，道具描述
//        // the product description is required and can not be null or ""
//        params.put(PRODUCT_DESC, productDesc);
//        // 必填字段，不能为null或者""，最长30字节，不能重复，否则订单会失败
//        // the request ID is required and can not be null or "". Also it must be unique.
//        params.put(REQUEST_ID, requestId);
//
//        String noSign = HuaweiPayUtil.getSignData(params);
//        LogUtil.d("startPay", "noSign：" + noSign);

        // CP必须把参数传递到服务端，在服务端进行签名，然后把sign传递下来使用；服务端签名的代码和客户端一致
        // the CP need to send the params to the server and sign the params on the server ,
        // then the server passes down the sign to client;
//        String sign = Rsa.sign(noSign, PAY_RSA_PRIVATE);
        LogUtil.d("startPay", "sign： " + sign);


        Map<String, Object> payInfo = new HashMap<String, Object>();
        // 必填字段，不能为null或者""
        // the amount is required and can not be null or ""
        payInfo.put(AMOUNT, price);
        // 必填字段，不能为null或者""
        // the product name is required and can not be null or ""
        payInfo.put(PRODUCT_NAME, productName);
        // 必填字段，不能为null或者""
        // the request ID is required and can not be null or ""
        payInfo.put(REQUEST_ID, requestId);
        // 必填字段，不能为null或者""
        // the product description is required and can not be null or ""
        payInfo.put(PRODUCT_DESC, productDesc);
        // 必填字段，不能为null或者""，请填写自己的公司名称
        // the user name is required and can not be null or "". Input the company name of CP.
        payInfo.put(USER_NAME, "魂动科技有限公司");
        // 必填字段，不能为null或者""
        // the APP ID is required and can not be null or "".
        payInfo.put(APPLICATION_ID, APP_ID);
        // 必填字段，不能为null或者""
        // the user ID is required and can not be null or "".
        payInfo.put(USER_ID, PAY_ID);
        // 必填字段，不能为null或者""
        // the sign is required and can not be null or "".
        payInfo.put(SIGN, sign);

        // 必填字段，不能为null或者""，此处写死X6
        // the service catalog is required and can not be null or "".
        payInfo.put(SERVICE_CATALOG, "X6");


        // 调试期可打开日志，发布时注释掉
        // print the log for demo
        payInfo.put(SHOW_LOG, true);
        /*
        * 支付页面横竖屏参数：1表示竖屏，2表示横屏，默认竖屏
        */
        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            payInfo.put(SCREENT_ORIENT, 2);
        }
        else
        {
            payInfo.put(SCREENT_ORIENT, 1);
        }
        GameServiceSDK.startPay(this.mActivity, payInfo, handler);
    }

    @Override
    public void pay(String data) {
        try {
            if (data != null && data.length() > 0) {
                JSONObject json = new JSONObject(data);
                if (null != json) {
                    String price = json.getString("price");
                    String productName = json.getString("productName");
                    String productDesc = json.getString("productDesc");
                    String requestId = json.getString("requestId");
                    String sign = json.getString("sign");

                    // 价格必须精确到小数点后两位，使用正则进行匹配
                    // The price must be accurate to two decimal places
                    boolean priceChceckRet = Pattern.matches("^\\d+[.]\\d{2}$", price);
                    if (!priceChceckRet)
                    {
                        mPayCb.failed(null, "价格异常");
                        return;
                    }

                    if ("".equals(productName))
                    {
                        mPayCb.failed(null, "商品名称为空");
                        return;
                    }
                    // 禁止输入：# " & / ? $ ^ *:) \ < > | , =
                    // the name can not input characters: # " & / ? $ ^ *:) \ < > | , =
                    else if (Pattern.matches(".*[#\\$\\^&*)=|\",/<>\\?:].*", productName))
                    {
                        mPayCb.failed(null, "商品名称异常");
                        return;
                    }
                    if ("".equals(productDesc))
                    {
                        mPayCb.failed(null, "商品描述为空");
                        return;
                    }
                    // 禁止输入：# " & / ? $ ^ *:) \ < > | , =
                    // the description can not input characters: # " & / ? $ ^ *:) \ < > | , =
                    else if (Pattern.matches(".*[#\\$\\^&*)=|\",/<>\\\\?\\^:].*", productDesc))
                    {
                        mPayCb.failed(null, "商品描述异常");
                        return;
                    }
                    // 调用公共方法进行支付
                    // call the pay method
                    _pay(price, productName, productDesc, requestId, sign, payHandler);
                    mPayOrder = requestId;
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
        return "huawei";
    }
}
