//
//  HiMessage.h
//  HI
//
//  Created by Rostyslav Stepanyak on 3/16/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface HiMessage : NSManagedObject

@property (nonatomic, retain) NSString * userName;
@property (nonatomic, retain) NSString * fbId;
@property (nonatomic, retain) NSDate * date;

@end
