//
//  DeviceInfo.m
//  Location
//
//  Created by Rostyslav Stepanyak on 3/11/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import "HIDeviceInfo.h"

@implementation HIDeviceInfo

+ (id)info
{
    static HIDeviceInfo *sharedMyModel = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedMyModel = [[self alloc] init];
    });
    return sharedMyModel;
}

#pragma mark device info

- (NSString *)deviceID {
    NSUUID *identifierForVendor = [[UIDevice currentDevice] identifierForVendor];
    NSString *deviceId = [identifierForVendor UUIDString];
    return deviceId;
}

- (NSString *)deviceName {
    return [[UIDevice currentDevice] name];
}

@end
