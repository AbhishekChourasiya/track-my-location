//
//  LocationInfo.swift
//  Le Location Tracker
//
//  Created by Oleh Zayats on 6/3/15.
//  Copyright (c) 2015 Oleh Zayats. All rights reserved.
//

import Foundation
import CoreLocation

class LocationInfo {
    
    var locationUpdated: CLLocation
    var locationString = "" as NSString
    var placemark: CLPlacemark
    var updateTime = NSDate()
    
    init (locationUpdated locationUpd: CLLocation?, placemark place: CLPlacemark?, locationString location: NSString?, updateTime updTime: NSDate?){
        locationUpdated = locationUpd!
        locationString  = location!
        updateTime      = updTime!
        placemark       = place!
    }
}