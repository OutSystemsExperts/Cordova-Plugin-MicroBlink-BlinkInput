import Foundation
import MicroBlink

class MBGenericPreset {
    
    class func getPreset() -> [MBScanElement]? {
        var scanElements = [MBScanElement]()
        
        let priceElement = MBScanElement(identifier: "Amount", parser: MBAmountParser())
        priceElement.localizedTitle = "Amount"
        priceElement.localizedTooltip = "Scan amount"
        scanElements.append(priceElement)
        
        let dateElement = MBScanElement(identifier: "Date", parser: MBDateParser())
        dateElement.localizedTitle = "Date"
        dateElement.localizedTooltip = "Scan date"
        scanElements.append(dateElement)
        
        let nameElement = MBScanElement(identifier: "Pay to Name", parser: MBRegexParser(regex: "[A-Za-z ]+"))
        nameElement.localizedTitle = "Pay to Name"
        nameElement.localizedTooltip = "Scan pay to full name"
        scanElements.append(nameElement)
        
        return scanElements
    }
}
