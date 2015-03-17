//
//  HiDataProvider.m
//  HI
//
//  Created by Rostyslav Stepanyak on 3/16/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import "HiDataProvider.h"
#import "HIMessage.h"
#import "HIAppDelegate.h"

@implementation HiDataProvider

+ (HiDataProvider *)provider {
    static HiDataProvider *sharedMyModel = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedMyModel = [[self alloc] init];
    });
    return sharedMyModel;
}

- (void)addMessageForUserId:(NSString *)fbId name:(NSString *)name image:(UIImage *)image{
    
    if(fbId && name) {
        HIAppDelegate *delegate = (HIAppDelegate *)[UIApplication sharedApplication].delegate;
        
        HiMessage *message = [NSEntityDescription
                              insertNewObjectForEntityForName:@"HiMessage"
                              inManagedObjectContext:delegate.managedObjectContext];
        message.userName = name;
        message.fbId = fbId;
        message.date = [[NSDate alloc] init];
        NSError *mocSaveError = nil;
        
        if (![delegate.managedObjectContext save:&mocSaveError])
        {
            NSLog(@"Save did not complete successfully. Error: %@",
                  [mocSaveError localizedDescription]);
        }
    }
}

- (NSArray *)getAllMessages {
    HIAppDelegate *delegate = (HIAppDelegate *)[UIApplication sharedApplication].delegate;
    NSManagedObjectContext *context = delegate.managedObjectContext;
    NSFetchRequest *request = [[NSFetchRequest alloc]initWithEntityName:@"HiMessage"];
    NSError *error = nil;
    
    NSArray *results = [context executeFetchRequest:request error:&error];
    
    return results;
}

- (void)handleHiPushInfo:(NSDictionary *)userInfo {
    NSArray *friends = userInfo[@"friends"];
    for (NSDictionary *friend in friends) {
         [self addMessageForUserId:friend[@"fb_id"] name:friend[@"name"] image:nil];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"kNewHiNotification" object:nil];
}


@end
