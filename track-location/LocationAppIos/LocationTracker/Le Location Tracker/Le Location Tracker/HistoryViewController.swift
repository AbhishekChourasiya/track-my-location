//
//  HistoryViewController.swift
//  Le Location Tracker
//
//  Created by Oleh Zayats on 5/29/15.
//  Copyright (c) 2015 Oleh Zayats. All rights reserved.
//

import UIKit
import Foundation

let DEFAULT_CELL_HEIGHT: CGFloat = 34.0

class HistoryViewController: UIViewController, UITableViewDataSource, UITableViewDelegate{
    
    @IBOutlet var historyTableOutlet: UITableView!
    
    var locationHolder:[Location] = [Location]()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupNavBarStyle()
        historyTableOutlet.separatorColor = UIColor.clearColor()
        locationHolder = DataManager.sharedInstance.loadFromCoreData()
    }
    
    override func viewWillAppear(animated: Bool) {
        animateTable()
    }
    
    func backButtonClicked() {
        dismissViewControllerAnimated(true, completion: nil)
    }
    
    // MARK: *** Table view data source ***
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int{
        return locationHolder.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        var historyRecord = locationHolder[indexPath.row]
        let cell = tableView.dequeueReusableCellWithIdentifier("historyTableCell", forIndexPath: indexPath) as! HistoryTableCell
        cell.setHistoryCell(historyRecord)
        cell.backgroundColor = colorForIndex(indexPath.row)
        return cell
    }
    
    // MARK: *** TableView Cell Height ***
    func getStringHeight(mytext: String, font: UIFont, width: CGFloat) -> CGFloat {
        let attributedString = NSMutableAttributedString(string: mytext)
        let options : NSStringDrawingOptions = .UsesLineFragmentOrigin | .UsesFontLeading
        attributedString.addAttribute(NSFontAttributeName, value: font, range: NSMakeRange(0, count(mytext)))
        let rect:CGRect! = attributedString.boundingRectWithSize(CGSizeMake(width, 300), options: options, context: nil)
        return rect.size.height
    }
    
    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        var additionalHeight:CGFloat = 0.0
        let font: UIFont! = UIFont(name: "Helvetica", size: 12)
        var textInput = NSString(format: "%@", locationHolder[indexPath.row].address) as String
        additionalHeight = CGFloat(additionalHeight) + self.getStringHeight(textInput as String, font: font, width: tableView.frame.size.width)
        return 18.0 + additionalHeight + 10.0
    }
    
    // MARK: *** Styles And Animation ***
    func setupNavBarStyle(){
        let backButton = UIBarButtonItem(title: "Back", style: UIBarButtonItemStyle.Plain, target: self, action: "backButtonClicked")
        self.navigationItem.leftBarButtonItem = backButton
        self.navigationItem.leftBarButtonItem?.tintColor = UIColor.whiteColor()
        self.navigationController?.navigationBar.barTintColor = UIColor(red: 1.0, green: 0.5, blue: 0.06, alpha: 1.0)
    }

    private func uicolorFromHex (rgbValue:UInt32) -> UIColor {
        let red = CGFloat((rgbValue & 0xFF0000) >> 16) / 256.0
        let green = CGFloat((rgbValue & 0xFF00) >> 8) / 256.0
        let blue = CGFloat(rgbValue & 0xFF) / 256.0
        return UIColor(red:red, green:green, blue:blue, alpha:1.0)
    }
    
    func colorForIndex (index: Int) -> UIColor {
        let itemCount = locationHolder.count - 1
        let valueForBlue = (CGFloat(index) / CGFloat(itemCount)) * 0.6
        return UIColor(red: 1.0, green: 0.5, blue: valueForBlue, alpha: 1.0)
    }
    
    func animateTable(){
        
        historyTableOutlet.reloadData()
        let cells = historyTableOutlet.visibleCells()
        let tableHeight: CGFloat = historyTableOutlet.bounds.size.height
        for i in cells {
            let cell: UITableViewCell = i as! UITableViewCell
            cell.transform = CGAffineTransformMakeTranslation(0, tableHeight)
        }
        var index = 0
        for c in cells{
            let cell: UITableViewCell = c as! UITableViewCell
            UIView.animateWithDuration(1.6, delay: 0.08 * Double(index),
                           usingSpringWithDamping: 0.7,
                            initialSpringVelocity: 0.8,
                                          options: nil,
                                       animations: {
                                                    cell.transform = CGAffineTransformMakeTranslation(0, 0);
                                                   },
                                       completion: nil)
            index += 1
        }
    }
}

