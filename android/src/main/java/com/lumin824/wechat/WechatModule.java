package com.lumin824.wechat;

import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import com.tencent.mm.sdk.modelmsg.SendAuth;

import java.util.ArrayList;
import java.util.List;

public class WechatModule extends ReactContextBaseJavaModule implements ActivityEventListener, IWXAPIEventHandler {

  private static List<WechatModule> moduleList = new ArrayList<WechatModule>();

  private IWXAPI wxAPI;

  private Promise authReqPromise;

  private String appId = BuildConfig.APP_ID;

  @Override
  public String getName() {
    return "WechatModule";
  }

  public WechatModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public void initialize(){
    super.initialize();
    moduleList.add(this);

    if(wxAPI == null){
      wxAPI = WXAPIFactory.createWXAPI(getReactApplicationContext().getBaseContext(), appId, true);
      wxAPI.registerApp(appId);
      WXSampleEntryActivity.wxAPI = wxAPI;
    }
    getReactApplicationContext().addActivityEventListener(this);
  }

  @Override
  public void onCatalystInstanceDestroy(){
    super.onCatalystInstanceDestroy();
    moduleList.remove(this);
    if(wxAPI != null){
      wxAPI = null;
    }
    getReactApplicationContext().removeActivityEventListener(this);
    super.onCatalystInstanceDestroy();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.w("weixin","onActivityResult");
  }

  @ReactMethod
  public void sendAuthReq(String scope, String state, Promise promise){
    authReqPromise = promise;
    SendAuth.Req req = new SendAuth.Req();
    req.scope = scope;
    req.state = state;
    Object ret = wxAPI.sendReq(req);

  }

  public static void handleIntent(Intent intent){
    for(WechatModule module: moduleList){
      module.wxAPI.handleIntent(intent, module);
    }
  }

  @Override
  public void onReq(BaseReq baseReq) {
    baseReq.getType();
  }

  @Override
  public void onResp(BaseResp baseResp) {

    if(baseResp instanceof SendAuth.Resp){
      SendAuth.Resp resp = (SendAuth.Resp) baseResp;

      if(authReqPromise != null){
        if(resp.code != null){
          authReqPromise.resolve(resp.code);
        }else{
          authReqPromise.reject("cancel", "cancel");
        }
        authReqPromise = null;
      }
    }
  }
}
