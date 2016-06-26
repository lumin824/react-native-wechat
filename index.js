import {
  NativeModules
} from 'react-native';

let { WechatModule } = NativeModules;

export var sendAuthReq = WechatModule.sendAuthReq;

export var sendMsgReq = WechatModule.sendMsgReq;

export var { WXSceneSession, WXSceneTimeline, WXSceneFavorite } = WechatModule;
