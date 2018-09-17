#import <Cordova/CDVPlugin.h>

@interface DeviceDataPlugin : CDVPlugin
- (void)getInfo : (CDVInvokedUrlCommand *)command;
- (NSString*) build;
- (NSString*) device;
@end
