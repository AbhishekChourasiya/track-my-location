//
//  DeviceInfo.h
//  Location
//
//  Created by Rostyslav Stepanyak on 3/11/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface HIDeviceInfo : NSObject

+ (id)info;
- (NSString *)deviceID;
- (NSString *)deviceName;

@end
