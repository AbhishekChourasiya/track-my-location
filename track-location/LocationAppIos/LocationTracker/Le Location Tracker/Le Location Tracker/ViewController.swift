//
//  ViewController.swift
//  Le Location Tracker
//
//  Created by Oleh Zayats on 5/29/15.
//  Copyright (c) 2015 Oleh Zayats. All rights reserved.
//

import UIKit
import FBSDKCoreKit
import FBSDKLoginKit
import CoreLocation

class ViewController: UIViewController, UIViewControllerTransitioningDelegate, CLLocationManagerDelegate, UINavigationControllerDelegate, FBSDKLoginButtonDelegate {
    
    @IBOutlet var startView: UIView!
    @IBOutlet var stopView: UIView!
    
    let customPresentAnimationController = CustomPresentAnimationController()
    var locationManager: LocationManager = LocationManager(desiredAccuracy: kCLLocationAccuracyNearestTenMeters, distanceFilter: 500.0, requestAlwaysAuth: true)

    override func viewDidLoad(){
        super.viewDidLoad()
        getSimulatorDirectory()
        setupNavBarStyle()
        styleBackground()
        styleButtons()
        //setupFBLogin()
    }
    
    // MARK: - Facebook Login
    func setupFBLogin() {
        if (FBSDKAccessToken.currentAccessToken() == nil) {
            println("Not logged in...")
        } else {
            println("Logged in...")
        }
        var loginButton = FBSDKLoginButton()
        loginButton.readPermissions = ["public_profile", "email", "user_friends"]
        loginButton.center = self.view.center
        loginButton.delegate = self
        self.view.addSubview(loginButton)
    }
    
    func loginButton(loginButton: FBSDKLoginButton!, didCompleteWithResult result: FBSDKLoginManagerLoginResult!, error: NSError!) {
        if error == nil {
            println("Login complete.")
            self.performSegueWithIdentifier("showNew", sender: self)
        } else {
            println(error.localizedDescription)
        }
    }
    
    func loginButtonDidLogOut(loginButton: FBSDKLoginButton!){
        println("User logged out...")
    }

    // MARK: - Actions
    @IBAction func startTrackingLocation(sender: UIButton){
        locationManager.startUpdatingLocation()
        self.performSegueWithIdentifier("showMap", sender: self)
        DataManager.sharedInstance.trackingTurnedOn = true
    }
    
    @IBAction func stopTrackingLocation(sender: UIButton){
        locationManager.stopUpdatingLocation()
        DataManager.sharedInstance.trackingTurnedOn = false
    }
    
    @IBAction func clearTrackedData(sender: UIBarButtonItem) {
        DataManager.sharedInstance.clearCoreDataStorage()
        println("Data Storage Cleared :)")
    }

    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?){
        if segue.identifier == "showMap"{
            let toViewController = segue.destinationViewController as! MapViewController
            toViewController.transitioningDelegate = self
            locationManager.delegate = toViewController
        }
    }
    
// *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** 
    
    func animationControllerForPresentedController(presented: UIViewController, presentingController presenting: UIViewController, sourceController source: UIViewController) -> UIViewControllerAnimatedTransitioning? {
        return customPresentAnimationController
    }
    
    private func styleButtons(){
        startView.backgroundColor = UIColor(red: 1.0, green: 0.5, blue: 0.06, alpha: 1.0)
        stopView.backgroundColor = UIColor(red: 1.0, green: 0.5, blue: 0.06, alpha: 1.0)
        startView.layer.cornerRadius = 15
        stopView.layer.cornerRadius = 15
    }
    
    private func styleBackground(){
        self.view.backgroundColor = uicolorFromHex(0x1f253d)
    }
    
    func setupNavBarStyle() {
        self.navigationController?.navigationBar.barTintColor = uicolorFromHex(0x394264)
    }
    
    private func uicolorFromHex(rgbValue:UInt32) -> UIColor{
        let red   = CGFloat((rgbValue & 0xFF0000) >> 16) / 256.0
        let green = CGFloat((rgbValue & 0xFF00) >> 8) / 256.0
        let blue  = CGFloat (rgbValue & 0xFF) / 256.0
        return UIColor(red:red, green:green, blue:blue, alpha:1.0)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func getSimulatorDirectory(){
        let documentsPath = NSFileManager.defaultManager().URLsForDirectory(.DocumentDirectory, inDomains: .UserDomainMask)
        let lastPath = documentsPath[0] as! NSURL
        println("\nxCode simulator .sqlite directory:")
        println(NSString(format: "----------\n%@\n----------\n", lastPath) as String)
    }
}

