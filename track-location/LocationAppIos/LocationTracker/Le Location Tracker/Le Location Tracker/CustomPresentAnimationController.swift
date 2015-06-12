//
//  LocationManager.swift
//  Le Location Tracker
//
//  Created by Oleh Zayats on 5/29/15.
//  Copyright (c) 2015 Oleh Zayats. All rights reserved.
//

import UIKit

class CustomPresentAnimationController: NSObject, UIViewControllerAnimatedTransitioning{
    
    func transitionDuration(transitionContext: UIViewControllerContextTransitioning) -> NSTimeInterval {
        return 1.4
    }
    
    func animateTransition(transitionContext: UIViewControllerContextTransitioning) {
        
        let fromViewController = transitionContext.viewControllerForKey(UITransitionContextFromViewControllerKey)!
        let toViewController = transitionContext.viewControllerForKey(UITransitionContextToViewControllerKey)!
        let finalFrameForVC = transitionContext.finalFrameForViewController(toViewController)
        let containerView = transitionContext.containerView()
        let bounds = UIScreen.mainScreen().bounds
        
        toViewController.view.frame = CGRectOffset(finalFrameForVC, 0, -bounds.size.height)
        containerView.addSubview(toViewController.view)
        
        UIView.animateWithDuration(transitionDuration(transitionContext), delay: 0.0,
                                                         usingSpringWithDamping: 0.6,
                                                          initialSpringVelocity: 0.1,
                                                                        options: .CurveLinear,
                                                                     animations: {
                                                                                   fromViewController.view.alpha = 0.6
                                                                                   toViewController.view.frame   = finalFrameForVC
                                                                                 },
            
                                                                     completion: {
                                                                                   finished in transitionContext.completeTransition(true)
                                                                                   fromViewController.view.alpha = 1.0
                                                                                 })
    }
}
