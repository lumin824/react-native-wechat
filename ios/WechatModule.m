
#import "RCTBridgeModule.h"
#import <UIKit/UIKit.h>
#import "WXApi.h"
#import "RCTConvert.h"

@interface WechatModule : NSObject<RCTBridgeModule,WXApiDelegate>

@property(nonatomic, copy) RCTPromiseResolveBlock authReqResolve;
@property(nonatomic, copy) RCTPromiseRejectBlock authReqReject;
@property(nonatomic, copy) RCTPromiseResolveBlock msgReqResolve;
@property(nonatomic, copy) RCTPromiseRejectBlock msgReqReject;

@property(nonatomic, copy) NSString* appId;
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
        self.appId = appId;
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
    if ([WXApi isWXAppInstalled]) {
        SendAuthReq* req = [[SendAuthReq alloc]init];
        req.scope = scope;
        req.state = state;
        self.authReqResolve = resolve;
        self.authReqReject = reject;
        [WXApi sendReq:req];
    }else{
        
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"没有安装微信软件，请您安装微信之后再试" message:nil delegate:self cancelButtonTitle:@"确定" otherButtonTitles:nil, nil];
        [alertView show];
    }
}

RCT_EXPORT_METHOD(sendMsgReq:(NSDictionary*)msg scene:(int)scene
                  resolve:(RCTPromiseResolveBlock) resolve
                  reject:(RCTPromiseRejectBlock) reject)
{
    self.msgReqResolve = resolve;
    self.msgReqReject = reject;

    SendMessageToWXReq* req = [[SendMessageToWXReq alloc] init];
    if([msg objectForKey:@"text"]){
        req.text = msg[@"text"];
        req.bText = YES;
    }else{
        WXMediaMessage* message = [WXMediaMessage message];
        req.message = message;
        req.bText = NO;
        if([msg objectForKey:@"imageUri"]){
            NSString* imagePath = msg[@"imageUri"];
            if([imagePath hasPrefix:@"file://"]){
                imagePath = [imagePath substringFromIndex:[@"file://" length]];
                WXImageObject* mediaObject = [WXImageObject object];
                mediaObject.imageData = [NSData dataWithContentsOfFile:imagePath];
                message.mediaObject = mediaObject;
            }
        }
    }
    
    req.scene = scene;
    [WXApi sendReq:req];
}

RCT_EXPORT_METHOD(isWXAppInstalled:(RCTPromiseResolveBlock) resolve
                  reject:(RCTPromiseRejectBlock) reject)
{
    if([WXApi isWXAppInstalled]){
        resolve(@"true");
    }else{
        resolve(@"");
    }
}

-(NSDictionary*) constantsToExport
{
    return @{
             @"WXSceneSession":@(WXSceneSession),
             @"WXSceneTimeline":@(WXSceneTimeline),
             @"WXSceneFavorite":@(WXSceneFavorite),
             @"APP_ID":self.appId};
}

@end
