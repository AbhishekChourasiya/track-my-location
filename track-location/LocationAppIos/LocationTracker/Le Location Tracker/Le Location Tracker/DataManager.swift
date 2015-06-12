//
//  DataManager.swift
//  Le Location Tracker
//
//  Created by Oleh Zayats on 02.06.15.
//  Copyright (c) 2015 Oleh Zayats. All rights reserved.
//

import UIKit
import CoreData
import Foundation

private let _DataManagerSharedInstance = DataManager()

class DataManager{
    
    var trackingTurnedOn = false as Bool
    static let sharedInstance = DataManager()
    
    // MARK: Core Data Save and Load
    internal func saveToCoreData(longitude: NSNumber, latitude: NSNumber, adress: NSString, updateTime: NSDate) -> Location {
        let managedContext = (UIApplication.sharedApplication().delegate as! AppDelegate).managedObjectContext
        let entity = NSEntityDescription.entityForName("Location", inManagedObjectContext:managedContext!)
        let locationObject = Location(entity: entity!, insertIntoManagedObjectContext: managedContext)
        
        locationObject.longitude = longitude
        locationObject.latitude = latitude
        locationObject.updateTime = updateTime
        locationObject.address = adress as String
        
        var error: NSError?
        if !(managedContext!.save(&error)) {
            println("Could not save \(error), \(error?.userInfo)")
        }
        return locationObject
    }
    
    internal func loadFromCoreData() -> [Location] {
        let managedContext = (UIApplication.sharedApplication().delegate as! AppDelegate).managedObjectContext
        let fetchRequest   = NSFetchRequest(entityName:"Location")
        var error: NSError?
        let fetchedResultsArray = managedContext!.executeFetchRequest(fetchRequest, error: &error) as? [Location]
        if (fetchedResultsArray == nil){
            println("Nothing to load.")
            return NSArray() as! [Location]
        }
        return fetchedResultsArray!
    }
    
    // loading tracked locations unsynchronized with server
    internal func loadUnsynchronized() -> [Location]{
        var error: NSError?
        let managedContext = (UIApplication.sharedApplication().delegate as! AppDelegate).managedObjectContext
        let fetchedResultsArray = managedContext!.executeFetchRequest(NSFetchRequest(entityName:"Location"), error: &error) as? [Location]
        
        if (fetchedResultsArray == nil){
            println("Nothing unsynchronized to load.")
            return NSArray() as! [Location]
        }
        //let array = fetchedResultsArray?.filter(){ return $0.isSyncWithServer.boolValue } // filtering to see if location from CoreData is synchronized
        var locationItem:Location
        var unsynchronizedLocations:[Location] = [Location]()
        for locationItem in fetchedResultsArray as [Location]! {
            if locationItem.isSyncWithServer.boolValue == false {
                unsynchronizedLocations.append(locationItem)
            }
        }
        return unsynchronizedLocations
    }
    
    internal func clearCoreDataStorage() {
        let managedContext = (UIApplication.sharedApplication().delegate as! AppDelegate).managedObjectContext
        let fetchRequest   = NSFetchRequest(entityName:"Location")
        var error: NSError?
        let fetchedResults = managedContext!.executeFetchRequest(fetchRequest, error: &error)
        if (fetchedResults != nil) {
            for item in fetchedResults! {
                managedContext!.deleteObject(item as! NSManagedObject)
            }
            managedContext!.save(nil)
            println("Storage Cleared.")
        }
    }
}