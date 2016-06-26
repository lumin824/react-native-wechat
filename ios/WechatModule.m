
#import "RCTBridgeModule.h"

@interface WechatModule : NSObject<RCTBridgeModule>

@end

@implementation WechatModule

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(sendAuthReq:(NSString*)scope state:(NSString*)state
                  resolve:(RCTPromiseResolveBlock) resolve
                  reject:(RCTPromiseRejectBlock) reject)
{
    resolve(@"ok");
}

@end
