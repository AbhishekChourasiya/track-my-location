//
//  HIFacebookAPI.m
//  Location
//
//  Created by Rostyslav Stepanyak on 3/13/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import "HIFacebookAPI.h"

@implementation HIFacebookAPI

+ (HIFacebookAPI *)api {
    static HIFacebookAPI *sharedMyModel = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedMyModel = [[self alloc] init];
    });
    return sharedMyModel;
}

+ (BOOL)openActiveSessionWithReadPermissions:(NSArray *)readPermissions
                                allowLoginUI:(BOOL)allowLoginUI
                           completionHandler:(FBSessionStateHandler)handler {
    
    return [FBSession openActiveSessionWithReadPermissions:readPermissions allowLoginUI:allowLoginUI
                                  completionHandler:handler];
    
}

// Handles session state changes in the app
- (void)sessionStateChanged:(FBSession *)session state:(FBSessionState) state error:(NSError *)error
{
    // If the session was opened successfully
    if (!error && state == FBSessionStateOpen){
        NSLog(@"Session opened");
        
        [FBRequestConnection startWithGraphPath:@"me" parameters:[NSMutableDictionary dictionaryWithObjectsAndKeys:@"id,name,first_name,last_name, gender, email,picture",@"fields",nil] HTTPMethod:@"GET" completionHandler:^(FBRequestConnection *connection, id result, NSError *error) {
            NSDictionary *userData = (NSDictionary *)result;
            NSLog(@"%@",[userData description]);
            
            _fbId = userData[@"id"];
            _gender = userData[@"gender"];
            _name = [NSString stringWithFormat:@"%@ %@", userData[@"first_name"], userData[@"last_name"]];
            _imageURL = [NSString stringWithFormat:@"https://graph.facebook.com/%@/picture?type=large", _fbId];
            [self subscribeToPushNotifications];
            [self userLoggedIn];
        }];
        
        
        return;
    }
    if (state == FBSessionStateClosed || state == FBSessionStateClosedLoginFailed){
        // If the session is closed
        NSLog(@"Session closed");
        // Show the user the logged-out UI
        [self userLoggedOut];
    }
    
    // Handle errors
    if (error){
        NSLog(@"Error");
        NSString *alertText;
        NSString *alertTitle;
        // If the error requires people using an app to make an action outside of the app in order to recover
        if ([FBErrorUtility shouldNotifyUserForError:error] == YES){
            alertTitle = @"Something went wrong";
            alertText = [FBErrorUtility userMessageForError:error];
            [self showMessage:alertText withTitle:alertTitle];
        } else {
            
            // If the user cancelled login, do nothing
            if ([FBErrorUtility errorCategoryForError:error] == FBErrorCategoryUserCancelled) {
                NSLog(@"User cancelled login");
                
                // Handle session closures that happen outside of the app
            } else if ([FBErrorUtility errorCategoryForError:error] == FBErrorCategoryAuthenticationReopenSession){
                alertTitle = @"Session Error";
                alertText = @"Your current session is no longer valid. Please log in again.";
                [self showMessage:alertText withTitle:alertTitle];
                
                // Here we will handle all other errors with a generic error message.
                // We recommend you check our Handling Errors guide for more information
                // https://developers.facebook.com/docs/ios/errors/
            } else {
                //Get more error information from the error
                NSDictionary *errorInformation = [[[error.userInfo objectForKey:@"com.facebook.sdk:ParsedJSONResponseKey"] objectForKey:@"body"] objectForKey:@"error"];
                
                // Show the user an error message
                alertTitle = @"Something went wrong";
                alertText = [NSString stringWithFormat:@"Please retry. \n\n If the problem persists contact us and mention this error code: %@", [errorInformation objectForKey:@"message"]];
                [self showMessage:alertText withTitle:alertTitle];
            }
        }
        // Clear this token
        [FBSession.activeSession closeAndClearTokenInformation];
        // Show the user the logged-out UI
        [self userLoggedOut];
    }
}

- (void)checkSession {
    if (FBSession.activeSession.state == FBSessionStateCreatedTokenLoaded) {
        [HIFacebookAPI openActiveSessionWithReadPermissions:@[@"public_profile", @"email", @"user_friends", @"user_about_me"] allowLoginUI:NO completionHandler:^(FBSession *session, FBSessionState state, NSError *error) {
            [self sessionStateChanged:session state:state error:error];
        }];
    }
    else if(FBSession.activeSession.state == FBSessionStateOpen) {
        [FBRequestConnection startWithGraphPath:@"me" parameters:[NSMutableDictionary dictionaryWithObjectsAndKeys:@"id,name,first_name,last_name, gender, email,picture",@"fields",nil] HTTPMethod:@"GET" completionHandler:^(FBRequestConnection *connection, id result, NSError *error) {
            NSDictionary *userData = (NSDictionary *)result;
            NSLog(@"%@",[userData description]);
            
            _fbId = userData[@"id"];
            _gender = userData[@"gender"];
            _name = [NSString stringWithFormat:@"%@ %@", userData[@"first_name"], userData[@"last_name"]];
            _imageURL = [NSString stringWithFormat:@"https://graph.facebook.com/%@/picture?type=large", _fbId];
            [self subscribeToPushNotifications];
            [self userLoggedIn];
        }];
    }
    
    else if (FBSession.activeSession.state == FBSessionStateClosed || FBSession.activeSession.state == FBSessionStateClosedLoginFailed) {
        [self userLoggedOut];
    }
    else {
        [self userLoggedOut];
    }
    
}

- (void)userLoggedOut {
    if(self.delegate)
        [self.delegate fbLoggedOut];
}

- (void)userLoggedIn {
    if(self.delegate)
        [self.delegate fbLoggedIn];
}

- (void)showMessage:(NSString *)alertText withTitle:(NSString *)alertTitle {
    if(self.delegate)
        [self.delegate showMessage:alertText withTitle:alertTitle];
}

#pragma mark parse puch notifications

- (void)subscribeToPushNotifications {
    UIApplication *application = [UIApplication sharedApplication];
    
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
}


@end
