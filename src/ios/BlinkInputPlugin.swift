import MicroBlink

class BlinkInputPlugin: CDVPlugin {
    
    private var scanViewController: ViewController?

    func initSDK(_ command:CDVInvokedUrlCommand) {
        let license:String = command.arguments[0] as? String ?? ""
        MBMicroblinkSDK.init().setLicenseKey(license)
        
        let pluginResult = CDVPluginResult(
            status: CDVCommandStatus_OK
        )
        
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
    }
    
    func chequeOCR(_ command:CDVInvokedUrlCommand) {
        self.scanViewController = ViewController()
     
        scanViewController?.startScanTapped()
    }
    
}