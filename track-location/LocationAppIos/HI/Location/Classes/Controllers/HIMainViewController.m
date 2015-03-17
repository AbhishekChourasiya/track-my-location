//
//  HIMainViewController.m
//  HI
//
//  Created by Rostyslav Stepanyak on 3/13/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import "HIMainViewController.h"
#import <FacebookSDK/FacebookSDK.h>
#import "HICell.h"
#import "HIDataProvider.h"
#import "MGSwipeButton.h"
#import "HiMessage.h"
#import <FacebookSDK/FacebookSDK.h>

@interface HIMainViewController ()
@property (nonatomic, strong) UIToolbar *toolbar;
@property (nonatomic, weak) IBOutlet UITableView *tableView;
@end

@implementation HIMainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadUI) name:@"kNewHiNotification" object:nil];
    
    self.navigationItem.hidesBackButton = YES;
    [self setupBgColors];
    [self hideEmptySeparators];
    [self setupRightNavigationItem];
    [self startTracking];
}

- (void)logoutButtonWasPressed:(id)sender {
    [FBSession.activeSession closeAndClearTokenInformation];
    [self.locationUpdateTimer invalidate];
    self.locationUpdateTimer = nil;
    [self stoptracking];
    
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)reloadUI {
    [self.tableView reloadData];
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

- (void)switchLocationTracking {
    
}

#pragma mark bar navigation items 

- (void)setupRightNavigationItem {
    
    int width = 102;
    //    if(isPad())
    //        width = 216;
    
    self.toolbar = [[UIToolbar alloc]
                    initWithFrame:CGRectMake(0, 0, width, 45)];
    //self.toolbar.layer.borderWidth = 1;
    self.toolbar.clipsToBounds = YES;

    UIBarButtonItem *negativeSeparator = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:nil];
    negativeSeparator.width = -12;
    //self.toolbar.layer.borderColor = [[UIColor whiteColor] CGColor];
    [self.toolbar setBackgroundImage:[[UIImage alloc] init] forToolbarPosition:UIToolbarPositionAny barMetrics:UIBarMetricsDefault];
    
    
    //Weird, but the obly way to add space between buttons
    UIBarButtonItem* space0 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:self action:nil];
    UIBarButtonItem* space1 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:self action:nil];

    
    // create an array for the buttons
    NSMutableArray* buttons = [[NSMutableArray alloc] init];
    
    [buttons addObject:space0];
    // create a standard save button location-bar-item
    
    UIImage *locationButtonImage = [UIImage imageNamed:@"location-bar-item"];
    UIButton *locationButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [locationButton setFrame:CGRectMake(0, 0, 22, 22)];
    [locationButton addTarget:self action:@selector(switchLocationTracking) forControlEvents:UIControlEventTouchUpInside];
    [locationButton setBackgroundImage:locationButtonImage forState:UIControlStateNormal];
    [locationButton setBackgroundImage:locationButtonImage forState:UIControlStateSelected];
    
    UIBarButtonItem *locationBarButton = [[UIBarButtonItem alloc]
                                   initWithCustomView:locationButton];
    locationBarButton.style = UIBarButtonItemStyleBordered;
    [buttons addObject:locationBarButton];
    [buttons addObject:space1];
    
    UIImage *logoutButtonImage = [UIImage imageNamed:@"logout-bar-item"];
    UIButton *logoutButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [logoutButton setFrame:CGRectMake(0, 0, 22, 22)];
    [logoutButton addTarget:self action:@selector(logoutButtonWasPressed:) forControlEvents:UIControlEventTouchUpInside];
    [logoutButton setBackgroundImage:logoutButtonImage forState:UIControlStateNormal];
    [logoutButton setBackgroundImage:logoutButtonImage forState:UIControlStateSelected];
    
    UIBarButtonItem *logoutBarButton = [[UIBarButtonItem alloc]
                                          initWithCustomView:logoutButton];
    logoutBarButton.style = UIBarButtonItemStyleBordered;
    [buttons addObject:logoutBarButton];

   
    
    
    [self.toolbar setItems:buttons animated:NO];
    // place the toolbar into the navigation bar
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:self.toolbar];
    
}

- (void)setupBgColors {
    UIColor *backgroundColor = [UIColor colorWithRed:0.961 green:0.973 blue:0.980 alpha:1.000];
    UIView *footerView = [[UIView alloc]init];
    footerView.backgroundColor = backgroundColor;
    
    self.tableView.tableFooterView = footerView;
    self.tableView.backgroundColor = backgroundColor;
}

#pragma mark table view datasource

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSUInteger number = [[HiDataProvider provider] getAllMessages].count;
    return number;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
        static NSString *CellIdentifier;
        CellIdentifier = @"FBUserCellIdentifier";
    
        HiMessage *message = [[[HiDataProvider provider] getAllMessages] objectAtIndex:indexPath.row];
        HICell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath: indexPath];
        if(cell == nil)
        {
            cell = [[HICell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        }
        
        cell.avatarImage.profileID = message.fbId;
        cell.nameLabel.text = message.userName;
        MGSwipeButton *removeButton = [MGSwipeButton buttonWithTitle:@"" icon:[UIImage imageNamed:@"delete-provider"] backgroundColor:[UIColor colorWithRed:59. / 255. green:77. / 255. blue:91. / 255. alpha:1.0]];
        removeButton.tag = indexPath.row;
        //[removeButton addTarget:self action:@selector(unlinkButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
            
        cell.rightButtons = @[removeButton];
    
    return cell;
}

#pragma mark table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void)hideEmptySeparators
{
    UIView *v = [[UIView alloc] initWithFrame:CGRectZero];
    v.backgroundColor = [UIColor clearColor];
    [self.tableView setTableFooterView:v];
}

-(void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Remove seperator inset
    if ([cell respondsToSelector:@selector(setSeparatorInset:)]) {
        [cell setSeparatorInset:UIEdgeInsetsMake(0, 0, 0, 0)];
    }
    
    // Prevent the cell from inheriting the Table View's margin settings
    if ([cell respondsToSelector:@selector(setPreservesSuperviewLayoutMargins:)]) {
        [cell setPreservesSuperviewLayoutMargins:NO];
    }
    
    // Explictly set your cell's layout margins
    if ([cell respondsToSelector:@selector(setLayoutMargins:)]) {
        [cell setLayoutMargins:UIEdgeInsetsZero];
    }
    
    NSUInteger number = [[HiDataProvider provider] getAllMessages].count;
    if((indexPath.row == number - 1) && [cell respondsToSelector:@selector(setSeparatorInset:)])
    {
        [cell setSeparatorInset:UIEdgeInsetsMake(0, 0, 0, 0)];
    }
}


- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self.locationUpdateTimer invalidate];
    self.locationUpdateTimer = nil;
    [self stoptracking];
}

@end
