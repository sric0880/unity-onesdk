package com.qiong.onesdk.wxapi;

import com.qiong.onesdk.wechat.Constants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unity3d.player.UnityPlayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "Unity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
//		Toast.makeText(UnityPlayer.currentActivity, "返回码：" + resp.errCode, Toast.LENGTH_SHORT).show();

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			switch (resp.errCode){
				case 0: //success
					UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "success");
					break;
				case -1:
					UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "fail|支付异常");
					break;
				case -2:
					UnityPlayer.UnitySendMessage("SDKMng", "OnPay", "cancel");
					break;
			}
		}

		finish();
	}
}