#import "DeviceDataPlugin.h"
#import <Cordova/CDVPluginResult.h>
#import <Foundation/Foundation.h>
#import <AdSupport/ASIdentifierManager.h>
#include <sys/types.h>
#include <sys/sysctl.h>

@implementation DeviceDataPlugin
    - (NSString*) build {
        
        size_t bufferSize = 64;
        NSMutableData *buffer = [[NSMutableData alloc] initWithLength:bufferSize];
        
        int status = sysctlbyname("kern.osversion", buffer.mutableBytes, &bufferSize, NULL, 0);
        if (status != 0) {
            return nil;
        }
        return [[NSString alloc] initWithCString:buffer.mutableBytes encoding:NSUTF8StringEncoding];
    }

    - (NSString*) device {
        size_t bufferSize = 64;
        NSMutableData *buffer = [[NSMutableData alloc] initWithLength:bufferSize];
        int status = sysctlbyname("hw.machine", buffer.mutableBytes, &bufferSize, NULL, 0);
        if (status != 0) {
            return nil;
        }
        return [[NSString alloc] initWithCString:buffer.mutableBytes encoding:NSUTF8StringEncoding];
    }

    - (void)getInfo : (CDVInvokedUrlCommand *)command
    {
        NSString * callbackId = command.callbackId;
        
        NSString * appVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
        NSString * build = [self build];
        NSString * deviceModel = [self device];
        NSString * osVersion = [[UIDevice currentDevice] systemVersion];
        NSString *locale = [[NSLocale currentLocale] localeIdentifier];
        
        NSUUID *adId = [[ASIdentifierManager sharedManager] advertisingIdentifier]; 
        NSString *deviceAdvertiserId = [adId UUIDString];

        NSDictionary *appData = @{
            @"deviceId" : deviceAdvertiserId,
            @"os" : @"iOS",
            @"osVersion" : osVersion,
            @"locale" : locale,
            @"deviceModel" : deviceModel,
            @"buildId": build,
            @"appVersion" : appVersion
        };

        NSError *error; 
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:appData 
                                                        options:NSJSONWritingPrettyPrinted // Pass 0 if you don't care about the readability of the generated string
                                                            error:&error];

        if (! jsonData) {
            NSLog(@"Got an error: %@", error);
            CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus : CDVCommandStatus_ERROR messageAsString : @"Error generating app data"];
            [self.commandDelegate sendPluginResult : pluginResult callbackId : callbackId];
        } else {
            NSString *appDataResult = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
            CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus : CDVCommandStatus_OK messageAsString : appDataResult];
            [self.commandDelegate sendPluginResult : pluginResult callbackId : callbackId];
        }
    }
@end
