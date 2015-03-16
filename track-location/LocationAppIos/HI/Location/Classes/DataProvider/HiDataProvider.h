//
//  HiDataProvider.h
//  HI
//
//  Created by Rostyslav Stepanyak on 3/16/15.
//  Copyright (c) 2015 Location. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface HiDataProvider : NSObject

+ (HiDataProvider *)provider;
- (void)addMessageForUserId:(NSString *)fbId name:(NSString *)name image:(UIImage *)image;
- (NSArray *)getAllMessages;
- (void)handleHiPushInfo:(NSDictionary *)userInfo;
@end
