//
//  HICell.h
//  HI
//
//  Created by Rostyslav Stepanyak on 3/16/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MGSwipeTableCell.h"
@class FBProfilePictureView;

@interface HICell : MGSwipeTableCell

@property (nonatomic, strong) IBOutlet FBProfilePictureView *avatarImage;
@property (nonatomic, strong) IBOutlet UILabel *nameLabel;

@end
