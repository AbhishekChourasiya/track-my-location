//
//  HIMainViewController.m
//  HI
//
//  Created by Rostyslav Stepanyak on 3/13/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import "HIMainViewController.h"
#import <FacebookSDK/FacebookSDK.h>

@interface HIMainViewController ()

@end

@implementation HIMainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setupRightNavigationItem];
    [self startTracking];
}

#pragma mark right navigation item

- (void)setupRightNavigationItem {
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc]
                                              initWithTitle:@"Logout"
                                              style:UIBarButtonItemStyleBordered
                                              target:self
                                              action:@selector(logoutButtonWasPressed:)];
}

- (void)logoutButtonWasPressed:(id)sender {
    [FBSession.activeSession closeAndClearTokenInformation];
    [self.locationUpdateTimer invalidate];
    self.locationUpdateTimer = nil;
    [self stoptracking];
    
    [self.navigationController popViewControllerAnimated:YES];
    
}

#pragma mark track/send location

-(void)updateLocation {
    NSLog(@"updateLocation");
    
    [self.locationTracker updateLocationToServer];
}

- (void)startTracking {
    [self stoptracking];
    
    //[self.startButton setTitleColor:[UIColor colorWithRed:0. green:170./255. blue:0. alpha:1.0] forState:UIControlStateNormal];
    UIAlertView * alert;
    
    //We have to make sure that the Background App Refresh is enable for the Location updates to work in the background.
    if([[UIApplication sharedApplication] backgroundRefreshStatus] == UIBackgroundRefreshStatusDenied){
        
        alert = [[UIAlertView alloc]initWithTitle:@""
                                          message:@"The app doesn't work without the Background App Refresh enabled. To turn it on, go to Settings > General > Background App Refresh"
                                         delegate:nil
                                cancelButtonTitle:@"Ok"
                                otherButtonTitles:nil, nil];
        [alert show];
        
    }else if([[UIApplication sharedApplication] backgroundRefreshStatus] == UIBackgroundRefreshStatusRestricted){
        
        alert = [[UIAlertView alloc]initWithTitle:@""
                                          message:@"The functions of this app are limited because the Background App Refresh is disable."
                                         delegate:nil
                                cancelButtonTitle:@"Ok"
                                otherButtonTitles:nil, nil];
        [alert show];
        
    } else{
        self.locationTracker = [[LocationTracker alloc]init];
        [self.locationTracker startLocationTracking];
        
        //Send the best location to server every 60 seconds
        //You may adjust the time interval depends on the need of your app.
        NSTimeInterval time = 60.0;
        self.locationUpdateTimer =
        [NSTimer scheduledTimerWithTimeInterval:time
                                         target:self
                                       selector:@selector(updateLocation)
                                       userInfo:nil
                                        repeats:YES];
        
    }
}

- (void)stoptracking {
    //[self.startButton setTitleColor:[UIColor darkGrayColor] forState:UIControlStateNormal];
    
    [self.locationUpdateTimer invalidate];
    [self.locationTracker stopLocationTracking];
}

- (void)dealloc {
    [self.locationUpdateTimer invalidate];
    self.locationUpdateTimer = nil;
    [self stoptracking];
}

@end
