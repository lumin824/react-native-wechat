import {
  NativeModules
} from 'react-native';

let { WechatModule } = NativeModules;

export var sendAuthReq = WechatModule.sendAuthReq;
