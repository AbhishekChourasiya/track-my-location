//
//  ServiceAPI.m
//  Location
//
//  Created by Rostyslav Stepanyak on 3/11/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import "HIServiceAPI.h"
#import "HIDeviceInfo.h"
#import "HIFacebookAPI.h"

@implementation HIServiceAPI

+ (id)api {
    static HIServiceAPI *sharedMyModel = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedMyModel = [[self alloc] init];
    });
    return sharedMyModel;
}

#pragma mark send coordinate

- (void)sendCoordinate:(CLLocationCoordinate2D)coordinate {
    
    NSError *error;
    
    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    NSURLSession *session = [NSURLSession sessionWithConfiguration:configuration delegate:self delegateQueue:nil];
    NSURL *url = [NSURL URLWithString:@"http://hi-track-location.herokuapp.com/track/add"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url
                                                           cachePolicy:NSURLRequestUseProtocolCachePolicy
                                                       timeoutInterval:60.0];
    
    [request addValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request addValue:@"application/json" forHTTPHeaderField:@"Accept"];
    
    [request setHTTPMethod:@"POST"];
    
    NSString *deviceName = [[HIDeviceInfo info] deviceName];
    NSString *deviceId = [[HIDeviceInfo info] deviceID];
    
    NSDictionary *locationDict =  @{@"lat" : [NSString stringWithFormat:@"%f", coordinate.latitude], @"lon" : [NSString stringWithFormat:@"%f", coordinate.longitude]};
    
    NSMutableArray *coordArray = [[NSMutableArray alloc] init];
    
    NSString *time = [NSString stringWithFormat:@"%f", [[[NSDate alloc] init] timeIntervalSince1970]*1000];
    [coordArray addObject: @{@"loc" : locationDict, @"time" : time}];
    
    NSDictionary *mapData = [[NSDictionary alloc] initWithObjectsAndKeys: deviceName, @"device_name",
                             deviceId, @"device_id", coordArray, @"track",
                             nil];
    NSData *postData = [NSJSONSerialization dataWithJSONObject:mapData options:0 error:&error];
    [request setHTTPBody:postData];
    
    
    NSURLSessionDataTask *postDataTask = [session dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*) response;
        NSLog(@"%@",httpResponse.description);
    }];
    
    [postDataTask resume];
    
}

- (void)signIn {
    NSError *error;
    
    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    NSURLSession *session = [NSURLSession sessionWithConfiguration:configuration delegate:self delegateQueue:nil];
    NSURL *url = [NSURL URLWithString:@"http://hi-track-location.herokuapp.com/_s/sign_in/fb"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url
                                                           cachePolicy:NSURLRequestUseProtocolCachePolicy
                                                       timeoutInterval:60.0];
    
    [request addValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request addValue:@"application/json" forHTTPHeaderField:@"Accept"];
    
    [request setHTTPMethod:@"POST"];
    
    HIFacebookAPI *api = [HIFacebookAPI api];
    NSString *fbId = api.fbId;
    NSString *name = api.name;
    NSString *gender = api.gender;
    NSString *imageURL = api.imageURL;
    
    NSDictionary *mapData = [[NSDictionary alloc] initWithObjectsAndKeys: fbId, @"fb_id",
                             name, @"name", gender, @"gender", imageURL, @"image_url",
                             nil];
    NSData *postData = [NSJSONSerialization dataWithJSONObject:mapData options:0 error:&error];
    [request setHTTPBody:postData];
    
    
    NSURLSessionDataTask *postDataTask = [session dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*) response;
        NSLog(@"%@",httpResponse.description);
    }];
    
    [postDataTask resume];

}


@end
