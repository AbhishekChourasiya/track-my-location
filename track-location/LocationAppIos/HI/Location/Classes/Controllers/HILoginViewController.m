//
//  LocationViewController.m
//  Location
//
//  Created by Rick
//  Copyright (c) 2014 Location. All rights reserved.
//

#import "HILoginViewController.h"
#import <FacebookSDK/FacebookSDK.h>
#import "MBProgressHUD.h"
#import "HIFacebookAPI.h"
#import "HIMainViewController.h"

@interface HILoginViewController () <FBLoginViewDelegate, HIFacebookDelegate>
@property (nonatomic, assign) IBOutlet UIButton *startButton;
@property (nonatomic, assign) IBOutlet UIButton *stopButton;
@end

@implementation HILoginViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    HIFacebookAPI *fbApi = [HIFacebookAPI api];
    fbApi.delegate = self;
    [fbApi checkSession];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
}

#pragma mark facebook


- (void) setupFBLoginView {
    FBLoginView *loginView = [[FBLoginView alloc] initWithReadPermissions:@[@"public_profile", @"email", @"user_friends", @"user_about_me"]];
    loginView.center = self.view.center;
    loginView.delegate = self;
    [self.view addSubview:loginView];
}

#pragma mark HIFacebookAPIDelegate

- (void)fbLoggedIn {
    UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"Main" bundle: nil];
    HIMainViewController *mainViewController = (HIMainViewController*)[mainStoryboard instantiateViewControllerWithIdentifier:@"HIMainViewControllerID"];
    [self.navigationController pushViewController:mainViewController animated:YES];
}

- (void)fbLoggedOut {
    [self setupFBLoginView];
}

- (void)showMessage:(NSString *)alertText withTitle:(NSString *)alertTitle {
    
}

@end
