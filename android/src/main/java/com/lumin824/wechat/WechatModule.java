package com.lumin824.wechat;

import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.ReadableMap;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import com.tencent.mm.sdk.modelmsg.SendAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class WechatModule extends ReactContextBaseJavaModule implements ActivityEventListener, IWXAPIEventHandler {

  private static List<WechatModule> moduleList = new ArrayList<WechatModule>();

  private IWXAPI wxAPI;

  private Promise authReqPromise;

  private static final String appId = BuildConfig.APP_ID;

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

  @ReactMethod
  public void sendMsgReq(ReadableMap msg, int scene, Promise promise){

    SendMessageToWX.Req req = new SendMessageToWX.Req();
    WXMediaMessage message = new WXMediaMessage();
    if(msg.hasKey("text")){
      String text = msg.getString("text");
      message.mediaObject = new WXTextObject(text);
    }
    req.message = message;
    req.scene = scene;
    wxAPI.sendReq(req);
  }

  @Nullable
  @Override
  public Map<String, Object> getConstants() {
    Map<String, Object> constants = new HashMap<>();
    constants.put("WXSceneSession", SendMessageToWX.Req.WXSceneSession);
    constants.put("WXSceneTimeline", SendMessageToWX.Req.WXSceneTimeline);
    constants.put("WXSceneFavorite", SendMessageToWX.Req.WXSceneFavorite);
    constants.put("APP_ID", appId);
    return constants;
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
