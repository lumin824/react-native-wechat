'use strict';

var React = require('react-native');
var {
  NativeModules
} = React;

var WechatAPI = NativeModules.RNWechatModule || NativeModules.RNWechatManager;

var login = function(scopes, callback){
  WechatAPI.login(scopes, function(err, data){
    console.log(arguments);
    var obj = data;
    if(!err && data) obj = JSON.parse(data);
    callback && callback(err, obj);
  });
}

var sendAuthReq = function(scope, state, callback){
  WechatAPI.sendAuthReq(scope, state, callback);
}

module.exports = {
  login,
  sendAuthReq
}
