
#import "RCTBridgeModule.h"
#import <UIKit/UIKit.h>
#import "WXApi.h"

@interface WechatModule : NSObject<RCTBridgeModule,WXApiDelegate>

@property(nonatomic, copy) RCTPromiseResolveBlock authReqResolve;
@property(nonatomic, copy) RCTPromiseRejectBlock authReqReject;
@end

@implementation WechatModule

-(instancetype) init
{
    self = [super init];
    if(self){
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleOpenURL:) name:@"RCTOpenURLNotification" object:nil];
        
        NSString* appId = nil;
        NSArray* list = [[[NSBundle mainBundle] infoDictionary] valueForKey:@"CFBundleURLTypes"];
        for(NSDictionary* item in list){
            NSString* name = item[@"CFBundleURLName"];
            if([name isEqualToString:@"weixin"])
            {
                NSArray* schemes = item[@"CFBundleURLSchemes"];
                if(schemes.count > 0){
                    appId = schemes[0];
                    break;
                }
            }
        }
        
        [WXApi registerApp:appId];
    }
    return self;
}

- (void)handleOpenURL:(NSNotification *)note
{
    NSDictionary *userInfo = note.userInfo;
    NSURL *url = [NSURL URLWithString:userInfo[@"url"]];
    [WXApi handleOpenURL:url delegate:self];
}

-(dispatch_queue_t) methodQueue
{
    return dispatch_get_main_queue();
}

-(void) onReq:(BaseReq *)req
{
}

-(void) onResp:(BaseResp *)resp
{
    if([resp isKindOfClass:[SendAuthResp class]]){
        SendAuthResp * authResp = (SendAuthResp *) resp;
        
        if(authResp.code){
            if(self.authReqResolve){
                self.authReqResolve(authResp.code);
                self.authReqResolve = nil;
                self.authReqReject = nil;
            }
        }else{
            if(self.authReqReject){
                self.authReqReject(@"cancel", @"cancel", nil);
                self.authReqResolve = nil;
                self.authReqReject = nil;
            }
        }
        
    }
}


RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(sendAuthReq:(NSString*)scope state:(NSString*)state
                  resolve:(RCTPromiseResolveBlock) resolve
                  reject:(RCTPromiseRejectBlock) reject)
{
    SendAuthReq* req = [[SendAuthReq alloc]init];
    req.scope = scope;
    req.state = state;
    self.authReqResolve = resolve;
    self.authReqReject = reject;
    [WXApi sendReq:req];
}

@end
