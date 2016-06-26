package com.lumin824.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import com.facebook.react.bridge.Callback;

public class WXSampleEntryActivity extends Activity {

  public static IWXAPI wxAPI;
  public static Callback callback;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      WechatModule.handleIntent(getIntent());
      finish();
  }

  @Override
  protected void onNewIntent(Intent intent) {
      super.onNewIntent(intent);
      WechatModule.handleIntent(getIntent());
      finish();
  }
}
