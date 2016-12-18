package com.qiong.onesdk;

/**
 * Created by sric0880 on 2016/11/24.
 */

public class SDKbaidu extends SDK {

    public SDKbaidu(OneSDK oneSDK) {
        super(oneSDK);
    }

    @Override
    public void onCreate() { }

    @Override
    public void login() { }

    @Override
    public void pay(String json) { }

    @Override
    public String getChannelStr() {
        return "baidu_";
    }
}
