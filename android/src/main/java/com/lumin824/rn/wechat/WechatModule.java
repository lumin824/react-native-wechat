package com.lumin824.rn.wechat;

import android.content.Intent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import com.tencent.mm.sdk.modelmsg.SendAuth;

public class WechatModule extends ReactContextBaseJavaModule implements ActivityEventListener {

  public static final String REACT_CLASS = "RNWechatModule";

  private IWXAPI wxAPI;

  private String appId;

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  public WechatModule(ReactApplicationContext reactContext) {
    super(reactContext);

    try{
      ApplicationInfo appInfo = reactContext.getPackageManager().getApplicationInfo(reactContext.getPackageName(), PackageManager.GET_META_DATA);
      appId = appInfo.metaData.get("QQ_APPID").toString();
    } catch (PackageManager.NameNotFoundException e){

    }

    appId = "wx5bba21edfa7efd61";
  }

  @Override
  public void initialize(){
    if(wxAPI == null){
      wxAPI = WXAPIFactory.createWXAPI(getReactApplicationContext().getBaseContext(), appId, true);
      wxAPI.registerApp(appId);
      WXEntryActivity.wxAPI = wxAPI;
    }
    getReactApplicationContext().addActivityEventListener(this);
  }

  @Override
  public void onCatalystInstanceDestroy(){
    if(wxAPI != null){
      wxAPI = null;
    }
    getReactApplicationContext().removeActivityEventListener(this);
    super.onCatalystInstanceDestroy();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

  }

  @ReactMethod
  public void getImageForFont(Callback callback) {
    callback.invoke(null, "ok");
  }

  @ReactMethod
  public void login(String scopes, final Callback callback){



    callback.invoke("err", "ok");
  }

  @ReactMethod
  public void sendAuthReq(String scope, String state, Callback callback){
    SendAuth.Req req = new SendAuth.Req();
    req.scope = scope;
    req.state = state;
    WXEntryActivity.callback = callback;
    Object ret = wxAPI.sendReq(req);
    callback.invoke(ret.toString());
  }

}
