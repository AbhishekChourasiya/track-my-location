//
//  MapView.swift
//  Le Location Tracker
//
//  Created by Oleh Zayats on 5/29/15.
//  Copyright (c) 2015 Oleh Zayats. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation
import CoreData
import Foundation

class MapViewController: UIViewController, CLLocationManagerDelegate, MKMapViewDelegate, TMLocationManagerDelegate {
    
    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet var leftView:  UIView!
    @IBOutlet var rightView: UIView!
    
    var locationAnnotationArray = [MKPointAnnotation]()
    var trackerIsOn = DataManager.sharedInstance.trackingTurnedOn
    var locationManager: LocationManager = LocationManager(desiredAccuracy: kCLLocationAccuracyNearestTenMeters, distanceFilter: 500.0, requestAlwaysAuth: true)
    
    var longitude: NSNumber!
    var latitude:  NSNumber!
    var locationsArray: NSMutableArray!
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(true)
        let locations :NSArray! = DataManager.sharedInstance.loadFromCoreData()
        locationsArray = locations.mutableCopy() as! NSMutableArray
        createPinsFromAnnotationArrayAndRevealOnMap()
    }
    
    override func viewDidLoad(){
        super.viewDidLoad()
        checkLocationService()
        styleButtons()
        setMap()
    }
    
    func setMap(){
        mapView.delegate = self
        mapView.showsUserLocation = true
        mapView.mapType = MKMapType.Standard
    }
    
    func checkLocationService(){
        if trackerIsOn == true {
            locationManager.startUpdatingLocation()
        } else {
            locationManager.stopUpdatingLocation()
        }
    }
    
    func createPinsFromAnnotationArrayAndRevealOnMap(){
        var coordinate2d: CLLocationCoordinate2D!
        var objectWithLatAndLon: Location!
        for objectWithLatAndLon in locationsArray{
            let annotation = locationFromCoreData(objectWithLatAndLon as! Location)
            mapView.addAnnotation(annotation)
        }
    }
    
    func locationFromCoreData(objectFromCD: Location) -> MKAnnotation{
        var coordinate2d: CLLocationCoordinate2D! = CLLocationCoordinate2D()
        coordinate2d.latitude  = Float64(objectFromCD.latitude)
        coordinate2d.longitude = Float64(objectFromCD.longitude)
        let annotation = MKPointAnnotation()
        annotation.coordinate = coordinate2d
        return annotation
    }
    
    func centerMapOnLocation(location: CLLocation) {
        let regionRadius: CLLocationDistance = 1000
        let coordinateRegion = MKCoordinateRegionMakeWithDistance(location.coordinate, regionRadius * 2.0, regionRadius * 2.0)
        mapView.setRegion(coordinateRegion, animated: true)
    }
    
    func updateLocationAndPlacemark(location: LocationInfo){
        
        let newAnnotation = MKPointAnnotation()
        var newLocation = location.locationUpdated
        newAnnotation.coordinate = newLocation.coordinate
        
        // *** Core Data ***
        longitude = NSNumber(double: newLocation.coordinate.longitude)
        latitude  = NSNumber(double: newLocation.coordinate.latitude)
        var adress = location.locationString as String
        let location = DataManager.sharedInstance.saveToCoreData(longitude, latitude: latitude, adress: adress, updateTime: NSDate())
        // *** *** *** *** ***
        
        let loadedAnnotation = locationFromCoreData(location as Location)
        mapView.addAnnotation(loadedAnnotation)
        
        locationAnnotationArray.append(newAnnotation)
        locationAnnotationArray.append(loadedAnnotation as! MKPointAnnotation)
        
        while locationAnnotationArray.count > 20 {
            let annotationToRemove = locationAnnotationArray.first!
            locationAnnotationArray.removeAtIndex(0)
            mapView.removeAnnotation(annotationToRemove)
        }
        if UIApplication.sharedApplication().applicationState == .Active {
            mapView.showAnnotations(locationAnnotationArray, animated: true)
        } else {
            println(NSString(format: "App is backgrounded. New location is \n\n %@\n", newLocation))
        }
    }

// *** *** *** *** *** *** *** *** *** *** *** ***  Buttons \ Actions  *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***
    
    //MARK: *** Buttons\Actions ***
    @IBAction func backButton(sender: UIButton){
        dismissViewControllerAnimated(true, completion: nil)
    }
    
    @IBAction func historyButton(sender: UIButton){
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let destinationVC = storyboard.instantiateViewControllerWithIdentifier("HistoryViewController") as! HistoryViewController
        let navigationController = UINavigationController(rootViewController: destinationVC)
        self.showViewController(navigationController, sender: UIButton())
    }
    
    private func styleButtons(){
        rightView.backgroundColor = UIColor(red: 1.0, green: 0.5, blue: 0.06, alpha: 1.0)
        leftView.backgroundColor = UIColor(red: 1.0, green: 0.5, blue: 0.06, alpha: 1.0)
        rightView.layer.cornerRadius = 15
        leftView.layer.cornerRadius = 15
    }
}
