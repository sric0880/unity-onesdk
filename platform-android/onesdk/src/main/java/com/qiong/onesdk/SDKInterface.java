package com.qiong.onesdk;

/**
 * Created by sric0880 on 16/12/17.
 */
public class SDKInterface{
    public interface LogoutCallback{
        void succeed();
        void failed(String msg);
    }

    public interface LoginCallback{
        void succeed(String userId, String token, String password, String msg);
        void failed(String msg);
        void cancelled();
    }

    public interface PayCallback{
        void succeed(String orderId, String msg);
        void failed(String orderId, String msg);
        void cancelled(String orderId, String msg);
    }
}
