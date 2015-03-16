//
//  HIFacebookAPI.h
//  Location
//
//  Created by Rostyslav Stepanyak on 3/13/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <FacebookSDK/FacebookSDK.h>

@protocol HIFacebookDelegate <NSObject>
- (void)fbLoggedIn;
- (void)fbLoggedOut;
- (void)showMessage:(NSString *)alertText withTitle:(NSString *)alertTitle;
@end

@interface HIFacebookAPI : NSObject
+ (HIFacebookAPI *)api;

+ (BOOL)openActiveSessionWithReadPermissions:(NSArray *)readPermissions
                                allowLoginUI:(BOOL)allowLoginUI
                           completionHandler:(FBSessionStateHandler)handler;
- (void)sessionStateChanged:(FBSession *)session state:(FBSessionState) state error:(NSError *)error;
- (void)checkSession;
- (void)subscribeToPushNotifications;

@property (nonatomic, strong) id <HIFacebookDelegate>delegate;
@property (nonatomic) BOOL logged;

//account info
@property (nonatomic, readonly) NSString *fbId;
@property (nonatomic, readonly) NSString *name;
@property (nonatomic, readonly) NSString *gender;
@property (nonatomic, readonly) NSString *imageURL;
@end
