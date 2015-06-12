//
//  HistoryTableCell.swift
//  Le Location Tracker
//
//  Created by Oleh Zayats on 5/29/15.
//  Copyright (c) 2015 Oleh Zayats. All rights reserved.
//

import Foundation
import UIKit

class HistoryTableCell: UITableViewCell {

    @IBOutlet weak var dateTimeLabel: UILabel!
    @IBOutlet weak var locationLabel: UILabel!
    
    func setHistoryCell(location: Location!) {
        
        var updTimeDate = location.updateTime
        var updTimeString = dateToString(updTimeDate) as String
        dateTimeLabel.text = updTimeString
        locationLabel.text = location.address as String
    }
    
    func dateToString(date: NSDate) -> NSString { 
        let formatter = NSDateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("HH:mm:ss, yyyy-MM-dd")
        formatter.stringFromDate(date)
        var updateTime: NSString = formatter.stringFromDate(date)
        return updateTime
    }
}
