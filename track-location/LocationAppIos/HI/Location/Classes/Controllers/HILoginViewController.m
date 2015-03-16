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
#import "HIServiceAPI.h"

@interface HILoginViewController () <FBLoginViewDelegate, HIFacebookDelegate>
@property (nonatomic)BOOL logged;
@end

@implementation HILoginViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    //if([HIFacebookAPI api].logged) {
        [self showHud];
    //}
    
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
    [[HIServiceAPI api] signIn];
    
    [HIFacebookAPI api].logged = YES;
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"Main" bundle: nil];
    HIMainViewController *mainViewController = (HIMainViewController*)[mainStoryboard instantiateViewControllerWithIdentifier:@"HIMainViewControllerID"];
    [self.navigationController pushViewController:mainViewController animated:YES];
}

- (void)fbLoggedOut {
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    [self setupFBLoginView];
}

- (void)showMessage:(NSString *)alertText withTitle:(NSString *)alertTitle {
    
}

#pragma mark hud

- (void)showHud {
    MBProgressHUD *hud =[MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelFont = [UIFont fontWithName:@"HelveticaNeue-Thin" size:18];
    hud.labelText = @"Authenticating";
    hud.backgroundColor = [UIColor colorWithRed:0.961 green:0.973 blue:0.980 alpha:1.000];
    hud.color = [UIColor colorWithRed:123./255. green:137./255. blue:148./255. alpha:1.000];
}

@end
