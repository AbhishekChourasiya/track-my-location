//
//  AppDelegate.swift
//  Le Location Tracker
//
//  Created by Oleh Zayats on 5/29/15.
//  Copyright (c) 2015 Oleh Zayats. All rights reserved.
//

import UIKit
import CoreData
import CoreLocation
import FBSDKCoreKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, CLLocationManagerDelegate {

    var window: UIWindow?
    var locationManager:LocationManager!
    
    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        
        UIApplication.sharedApplication().setMinimumBackgroundFetchInterval(UIApplicationBackgroundFetchIntervalMinimum)
        println("didFinishLaunchingWithOptions")
        
        locationManager = LocationManager.sharedInstance
        locationManager.nonActive = false
        
        var alert: UIAlertView
        let messageWhenDenied = "The app doesn't work without the Background App Refresh enabled. To turn it on, go to Settings > General > Background App Refresh"
        let messageWhenRestricted = "The functions of this app are limited because the Background App Refresh is disable."
    
        if UIApplication.sharedApplication().backgroundRefreshStatus == UIBackgroundRefreshStatus.Denied{
            alert = UIAlertView(title: "", message: messageWhenDenied, delegate: nil, cancelButtonTitle: "OK")
            alert.show()
            
        } else if UIApplication.sharedApplication().backgroundRefreshStatus == UIBackgroundRefreshStatus.Restricted{
            alert = UIAlertView(title: "", message: messageWhenRestricted, delegate: nil, cancelButtonTitle: "OK")
            alert.show()
            
        } else {

            if launchOptions?[UIApplicationLaunchOptionsLocationKey] != nil {
                println("location event!")
                
                locationManager.nonActive = true
                locationManager.setLocationManager()
            }
            if IS_OS_8_OR_LATER{
                locationManager.locationManager.requestAlwaysAuthorization()
            }
            locationManager.locationManager.startMonitoringSignificantLocationChanges()
        }
        //MARK: --- Facebook ---
        return FBSDKApplicationDelegate.sharedInstance().application(application, didFinishLaunchingWithOptions: launchOptions)
    }
    
    func application(application: UIApplication, openURL url: NSURL, sourceApplication: String?, annotation: AnyObject?) -> Bool {
        return FBSDKApplicationDelegate.sharedInstance().application(application, openURL: url, sourceApplication: sourceApplication, annotation: annotation)
    }
    
    // ---

    func applicationDidEnterBackground(application: UIApplication) {
        println("applicationDidEnterBackground")
        locationManager.startUpdatingLocationInBackground()
    }

    func applicationDidBecomeActive(application: UIApplication) {
        
        println("applicationDidBecomeActive")
        
        locationManager.nonActive = false
        
        if (locationManager.locationManager != nil) {
            locationManager.locationManager.stopMonitoringSignificantLocationChanges()
            locationManager.setLocationManager()
        }
        if IS_OS_8_OR_LATER{
            locationManager.locationManager.requestAlwaysAuthorization()
        }
        locationManager.locationManager.startMonitoringSignificantLocationChanges()
    }

    func applicationWillTerminate(application: UIApplication) {
        println("applicationWillTerminate")
        
        locationManager.nonActive = true
        self.saveContext()
    }

    // MARK: - Core Data Stack
    
    lazy var applicationDocumentsDirectory: NSURL = {
        // The directory the application uses to store the Core Data store file.
        // This code uses a directory named "com.lelocationtracker.Le_Location_Tracker" in
        // the application's documents Application Support directory.
        let urls = NSFileManager.defaultManager().URLsForDirectory(.DocumentDirectory, inDomains: .UserDomainMask)
        return urls[urls.count-1] as! NSURL
        }()
    
    lazy var managedObjectModel: NSManagedObjectModel = {
        // The managed object model for the application. This property is not optional.
        // It is a fatal error for the application not to be able to find and load its model.
        let modelURL = NSBundle.mainBundle().URLForResource("Le_Location_Tracker", withExtension: "momd")!
        return NSManagedObjectModel(contentsOfURL: modelURL)!
        }()
    
    lazy var persistentStoreCoordinator: NSPersistentStoreCoordinator? = {
        // The persistent store coordinator for the application. This implementation creates and return a coordinator,
        // having added the store for the application to it. This property is optional since there are legitimate
        // error conditions that could cause the creation of the store to fail.
        // Create the coordinator and store
        var coordinator: NSPersistentStoreCoordinator? = NSPersistentStoreCoordinator(managedObjectModel: self.managedObjectModel)
        let url = self.applicationDocumentsDirectory.URLByAppendingPathComponent("Le_Location_Tracker.sqlite")
        var error: NSError? = nil
        var failureReason = "There was an error creating or loading the application's saved data."
        if coordinator!.addPersistentStoreWithType(NSSQLiteStoreType, configuration: nil, URL: url, options: nil, error: &error) == nil {
            coordinator = nil
            // Report any error we got.
            var dict = [String: AnyObject]()
            dict[NSLocalizedDescriptionKey] = "Failed to initialize the application's saved data"
            dict[NSLocalizedFailureReasonErrorKey] = failureReason
            dict[NSUnderlyingErrorKey] = error
            error = NSError(domain: "YOUR_ERROR_DOMAIN", code: 9999, userInfo: dict)
            // Replace this with code to handle the error appropriately.
            // abort() causes the application to generate a crash log and terminate.
            // You should not use this function in a shipping application,
            // although it may be useful during development.
            NSLog("Unresolved error \(error), \(error!.userInfo)")
            abort()
        }
        
        return coordinator
        }()
    
    lazy var managedObjectContext: NSManagedObjectContext? = {
        // Returns the managed object context for the application (which is already
        // bound to the persistent store coordinator for the application.)
        // This property is optional since there are legitimate error conditions
        // that could cause the creation of the context to fail.
        let coordinator = self.persistentStoreCoordinator
        if coordinator == nil {
            return nil
        }
        var managedObjectContext = NSManagedObjectContext()
        managedObjectContext.persistentStoreCoordinator = coordinator
        return managedObjectContext
        }()
    
    // MARK: - Core Data Saving Support
    func saveContext () {
        if let moc = self.managedObjectContext {
            var error: NSError? = nil
            if moc.hasChanges && !moc.save(&error) {
                // Replace this implementation with code to handle the error appropriately.
                // abort() causes the application to generate a crash log and terminate.
                // You should not use this function in a shipping application,
                // although it may be useful during development.
                NSLog("Unresolved error \(error), \(error!.userInfo)")
                abort()
            }
        }
    }
}

