//
//  ServiceAPI.h
//  Location
//
//  Created by Rostyslav Stepanyak on 3/11/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@interface HIServiceAPI : NSObject<NSURLSessionDelegate>

+ (id)api;
- (void)sendCoordinate:(CLLocationCoordinate2D)coordinate;
@end
