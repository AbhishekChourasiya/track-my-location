//
//  Location.swift
//  LeLocationTracker
//
//  Created by Rostyslav Stepanyak on 6/10/15.
//  Copyright (c) 2015 Oleh Zayats. All rights reserved.
//

import Foundation
import CoreData

class Location: NSManagedObject {
    
    @NSManaged var address: String
    @NSManaged var latitude: NSNumber
    @NSManaged var longitude: NSNumber
    @NSManaged var updateTime: NSDate
    @NSManaged var isSyncWithServer: NSNumber

    func locationToDictionary() -> NSDictionary {
        
        var latLonDict = ["lon":self.longitude, "lat":self.latitude]
        var locDict = ["loc":latLonDict, "time":(updateTime.timeIntervalSince1970)*1000]
        
        
        
        return locDict
    }
}

