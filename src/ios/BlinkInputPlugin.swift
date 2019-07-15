import MicroBlink

@objc(BlinkInputPlugin)
@objcMembers
class BlinkInputPlugin : CDVPlugin {
    
    private var callback : CDVInvokedUrlCommand?

    func initSDK(_ command:CDVInvokedUrlCommand) {
        let license:String = command.arguments[0] as? String ?? ""
        
        DispatchQueue.main.async {
            MBMicroblinkSDK.sharedInstance().setLicenseKey(license)
            let pluginResult = CDVPluginResult(
                status: CDVCommandStatus_OK
            )
            
            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
        }
    }

    func checkSupportedDevice(_ command:CDVInvokedUrlCommand) {
        let error : NSErrorPointer = nil
        //Unsupported false -> Is supported
        if MBMicroblinkSDK.isScanningUnsupported(for: .back, error: error) == false {
            let pluginResult = CDVPluginResult(
                status: CDVCommandStatus_OK
            )
            
            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
        } else {
            let pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR
            )
            
            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
        }
    }
    
    func chequeOCR(_ command:CDVInvokedUrlCommand) {
        DispatchQueue.main.async {
            self.callback = command
            // Create MBFieldByFieldOverlaySettings
            let settings = MBFieldByFieldOverlaySettings(scanElements: MBGenericPreset.getPreset()!)
            
            // Create field by field VC
            let fieldByFieldVC = MBFieldByFieldOverlayViewController(settings: settings, delegate: self)
            
            // Create scanning VC
            let recognizerRunnerViewController: (UIViewController & MBRecognizerRunnerViewController)? = MBViewControllerFactory.recognizerRunnerViewController(withOverlayViewController: fieldByFieldVC)
            
            
            // Present VC
            self.viewController.present(recognizerRunnerViewController!, animated: true, completion: nil)
            //scanViewController.startScanTapped(self.viewController)
        }
    }
    
}

extension BlinkInputPlugin : MBFieldByFieldOverlayViewControllerDelegate {
    func field(_ fieldByFieldOverlayViewController: MBFieldByFieldOverlayViewController, didFinishScanningWith scanElements: [MBScanElement]) {
        
        fieldByFieldOverlayViewController.recognizerRunnerViewController?.pauseScanning()
        
        var dict = [String: String]()
        for element: MBScanElement in scanElements {
            if (element.scanned) {
                dict[element.identifier] = element.value
            }
        }
        
        var result = [String : Any]()
        for (key, value) in dict {
            result.updateValue("\(value)", forKey: "\(key)" )
        }
        result.updateValue(true, forKey: "success")
        
        //let result: [String: Any] = ["success": true, "granted": true]
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: result)
        self.commandDelegate!.send(
            pluginResult,
            callbackId: self.callback?.callbackId
        )
        
        //let alert = UIAlertController(title: "Field by field Result", message: "\(description)", preferredStyle: UIAlertController.Style.alert)
        //alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { action in
        //    self.viewController.dismiss(animated: true, completion: nil)
        //}))
        
        //fieldByFieldOverlayViewController.present(alert, animated: true, completion: nil)
        self.viewController.dismiss(animated: true, completion: nil)
    }
    
    func field(byFieldOverlayViewControllerWillClose fieldByFieldOverlayViewController: MBFieldByFieldOverlayViewController) {
        self.viewController.dismiss(animated: true, completion: nil)
    }
}
