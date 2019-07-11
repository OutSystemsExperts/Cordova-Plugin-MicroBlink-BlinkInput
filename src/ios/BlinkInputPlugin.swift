import MicroBlink

@objc(BlinkInputPlugin)
@objcMembers
class BlinkInputPlugin : CDVPlugin {
    
    private var scanViewController: ViewController?

    func initSDK(_ command:CDVInvokedUrlCommand) {
        let license:String = command.arguments[0] as? String ?? ""
        
        DispatchQueue.main.async {
            MBMicroblinkSDK.sharedInstance().setLicenseKey(license)
        }
        
        
        let pluginResult = CDVPluginResult(
            status: CDVCommandStatus_OK
        )
        
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
    }
    
    func chequeOCR(_ command:CDVInvokedUrlCommand) {
        let scanViewController = ViewController()
    
        
        DispatchQueue.main.async {
            scanViewController.startScanTapped(self.viewController)
        }
    }
    
}
