//
//  Location.swift
//  
//
//  Created by Rostyslav Stepanyak on 6/4/15.
//
//

import Foundation
import CoreData

class Location: NSManagedObject {

    @NSManaged var latitude: NSNumber
    @NSManaged var longitude: NSNumber
    @NSManaged var updateTime: String

}
