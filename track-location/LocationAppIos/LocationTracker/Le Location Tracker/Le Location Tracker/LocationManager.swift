//
//  LocationManager.swift
//  Le Location Tracker
//
//  Created by Oleh Zayats on 5/29/15.
//  Copyright (c) 2015 Oleh Zayats. All rights reserved.
//

import CoreLocation
import MapKit
import Foundation

let IS_OS_8_OR_LATER = ((UIDevice.currentDevice().systemVersion as NSString).floatValue >= 8.0)

protocol TMLocationManagerDelegate: class {
    func updateLocationAndPlacemark(location: LocationInfo)
}

class LocationManager: NSObject, CLLocationManagerDelegate {
    
    static let sharedInstance = LocationManager(desiredAccuracy: kCLLocationAccuracyNearestTenMeters,
                                                 distanceFilter: 500.0,
                                              requestAlwaysAuth: true)

    weak var delegate: TMLocationManagerDelegate?

    // *** for Terminated\Suspended application ***
    var nonActive: Bool
    // *** *** *** *** *** *** *** *** *** *** ***
    var locationManager: CLLocationManager!
    var desiredAccuracy: CLLocationAccuracy!
    var distanceFilter : CLLocationDistance!
    var requestAlwaysAuth = true as Bool
    
    init (desiredAccuracy accuracy: CLLocationAccuracy?, distanceFilter filter: CLLocationDistance?, requestAlwaysAuth authorisation: Bool) {
        locationManager   = CLLocationManager()
        desiredAccuracy   = accuracy
        distanceFilter    = filter
        requestAlwaysAuth = authorisation
        nonActive = false
    }
    
    // MARK: *** Updating Location ***
    func startUpdatingLocation(){
        locationManager.delegate = self
        locationManager.requestAlwaysAuthorization()
        if CLLocationManager.locationServicesEnabled(){
            locationManager.startUpdatingLocation()
        }
        println("Started updates.")
    }
    
    func startUpdatingLocationInBackground(){
        locationManager.delegate = self
        self.locationManager.startMonitoringSignificantLocationChanges()
            if IS_OS_8_OR_LATER {
                self.locationManager.requestAlwaysAuthorization()
        }
        self.locationManager.startMonitoringSignificantLocationChanges()
        println("Started updates in background.")
    }
    
    func setLocationManager(){
        locationManager = CLLocationManager()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        locationManager.activityType = CLActivityType.OtherNavigation
    }
    
    func stopUpdatingLocation(){
        locationManager.stopUpdatingLocation()
        println("Stopped updates.")
    }

    func locationManager(manager: CLLocationManager!, didUpdateLocations locations: [AnyObject]!) {
        CLGeocoder().reverseGeocodeLocation(manager.location, completionHandler: {
            (placemarks, error) -> Void in
            
            if (error != nil) {
                println("Error:" + error.localizedDescription)
                return
            }
            if placemarks.count > 0 {
                let placeMark = placemarks[0] as! CLPlacemark

                var currentTime = NSDate()
                var location: LocationInfo
                var address = self.addressString(placeMark) as String
                location = LocationInfo(locationUpdated: manager.location, placemark: placeMark, locationString: address, updateTime: currentTime)
                
                self.delegate?.updateLocationAndPlacemark(location) // here we send location (LocationInfo) to MapViewController (updateLocationAndPlacemark) via delegate
      
                if self.nonActive {
                    var longitude = NSNumber(double: manager.location.coordinate.longitude)
                    var latitude  = NSNumber(double: manager.location.coordinate.latitude)
                    var address   = "fetched in Terminated State" + (self.addressString(placeMark) as String) as String
                    var location  = DataManager.sharedInstance.saveToCoreData(longitude, latitude: latitude, adress: address, updateTime: NSDate())
                }
                
                var loadedLocationArray = DataManager.sharedInstance.loadUnsynchronized() as [Location]
                println(loadedLocationArray)
                RestAPIManager.sharedInstance.sendDeviceTrackedData(loadedLocationArray, onCompletion: {json, err in })
                
            } else {
                println("Error :(")
            }
        })
    }
    func locationManager(manager: CLLocationManager!, didFailWithError error: NSError!) {
        var notification = UILocalNotification()
        notification.timeZone = NSTimeZone.defaultTimeZone()
        notification.fireDate = NSDate().dateByAddingTimeInterval(2)
        notification.alertBody = "didFailWithError" as String
        UIApplication.sharedApplication().scheduleLocalNotification(notification)
    }
    
    func addressString(placemark: CLPlacemark) -> NSString {

        var subThoroughfareStr = "None"
        var thoroughfareStr    = "None"
        var subLocalityStr     = "None"
        var localityStr        = "None"
        var admAreaStr         = "None"
        var countryStr         = "None"
        
        if placemark.subThoroughfare != nil {
            subThoroughfareStr = placemark.subThoroughfare
        }
        if placemark.thoroughfare != nil {
            thoroughfareStr = placemark.thoroughfare
        }
        if placemark.subLocality != nil {
            subLocalityStr = placemark.subLocality
        }
        if placemark.locality != nil {
            localityStr = placemark.locality
        }
        if placemark.administrativeArea != nil {
            admAreaStr = placemark.administrativeArea
        }
        if placemark.country != nil {
            countryStr = placemark.country
        }
        var locationString = NSString(format: "Address: %@, %@ \nLocation: %@, %@, %@, %@", subThoroughfareStr, thoroughfareStr, subLocalityStr,
                                                                                                        localityStr, admAreaStr, countryStr) as String
        return locationString
    }
}
