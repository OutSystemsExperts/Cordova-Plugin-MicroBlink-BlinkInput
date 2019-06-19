import MicroBlink

class BlinkInputPlugin: CDVPlugin {

    func initSDK(_ command:CDVInvokedUrlCommand) {
        let license:String = command.arguments[0] as? String ?? ""
        MBMicroblinkSDK.init().setLicenseKey(license)
    }
    
}
