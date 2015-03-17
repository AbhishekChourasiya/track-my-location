//
//  HIMainViewController.h
//  HI
//
//  Created by Rostyslav Stepanyak on 3/13/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LocationTracker.h"

@interface HIMainViewController : UIViewController
  @property LocationTracker * locationTracker;
  @property (nonatomic) NSTimer* locationUpdateTimer;
@end
