//
//  LocationAppDelegate.m
//  Location
//
//  Created by Rick
//  Copyright (c) 2014 Location. All rights reserved.
//

#import "HIAppDelegate.h"
#import <FacebookSDK/FacebookSDK.h>
#import "HIFacebookAPI.h"

@implementation HIAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    [FBAppEvents activateApp];
    // Whenever a person opens app, check for a cached session
    if (FBSession.activeSession.state == FBSessionStateCreatedTokenLoaded) {
        HIFacebookAPI *fbAPI= [HIFacebookAPI api];
        [HIFacebookAPI openActiveSessionWithReadPermissions:@[@"public_profile", @"email", @"user_friends", @"user_about_me"] allowLoginUI:NO completionHandler:^(FBSession *session, FBSessionState state, NSError *error) {
            [fbAPI sessionStateChanged:session state:state error:error];
        }];
    }
    
    //-- Set Notification
    if ([application respondsToSelector:@selector(isRegisteredForRemoteNotifications)])
    {
        // iOS 8 Notifications
        [application registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:(UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge) categories:nil]];
        
        [application registerForRemoteNotifications];
    }
    else
    {
        // iOS < 8 Notifications
        [application registerForRemoteNotificationTypes:
         (UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeSound)];
    }
    return YES;
}

- (void)application:(UIApplication*)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData*)deviceToken
{
    NSLog(@"My token is: %@", deviceToken);
}
                                    
- (void)application:(UIApplication*)application didFailToRegisterForRemoteNotificationsWithError:(NSError*)error
{
    NSLog(@"Failed to get token, error: %@", error);
}

- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication
         annotation:(id)annotation {
    
     [FBSession.activeSession setStateChangeHandler:
     ^(FBSession *session, FBSessionState state, NSError *error) {
         [[HIFacebookAPI api] sessionStateChanged:session state:state error:error];
     }];
    
    
    return [FBAppCall handleOpenURL:url sourceApplication:sourceApplication];
}

- (void) applicationDidEnterBackground:(UIApplication *) application {
    
}

@end
