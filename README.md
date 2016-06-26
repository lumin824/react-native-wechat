# react-native-wechat


android

AndroidManifest.xml->
<activity android:name=".wxapi.WXEntryActivity" android:label="@string/app_name" android:exported="true" />

public class WXEntryActivity extends WXSampleEntryActivity {
}

gradle.properties
WECHAT_APP_ID=wx111111111


ios

 Info->URL Types 添加 Identifier -> weixin URL Schemes -> wx111111111
