package com.lumin824.rn.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import com.facebook.react.bridge.Callback;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

  public static IWXAPI wxAPI;
  public static Callback callback;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      wxAPI.handleIntent(getIntent(), this);
  }

  @Override
  protected void onNewIntent(Intent intent) {
      super.onNewIntent(intent);
      setIntent(intent);
      wxAPI.handleIntent(intent, this);
  }

  @Override
  public void onReq(BaseReq baseReq) {

  }

  @Override
  public void onResp(BaseResp baseResp) {
    callback.invoke("android ok");
  }
}
