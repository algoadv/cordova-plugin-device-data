#import <Cordova/CDVPlugin.h>

@interface DeviceDataPlugin : CDVPlugin
- (void)getInfo : (CDVInvokedUrlCommand *)command
- NSString *build(void);
- NSString *device(void);
@end