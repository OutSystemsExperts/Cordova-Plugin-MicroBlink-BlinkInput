import UIKit
import MicroBlink

class ViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func startScanTapped(_ topVC: UIViewController) {
        
        // Create MBFieldByFieldOverlaySettings
        let settings = MBFieldByFieldOverlaySettings(scanElements: MBGenericPreset.getPreset()!)
        
        // Create field by field VC
        let fieldByFieldVC = MBFieldByFieldOverlayViewController(settings: settings, delegate: self)
        
        // Create scanning VC
        let recognizerRunnerViewController: (UIViewController & MBRecognizerRunnerViewController)? = MBViewControllerFactory.recognizerRunnerViewController(withOverlayViewController: fieldByFieldVC)
        
    
        // Present VC
        topVC.present(recognizerRunnerViewController!, animated: true, completion: nil)
    }
}

extension ViewController : MBFieldByFieldOverlayViewControllerDelegate {
    
    func field(_ fieldByFieldOverlayViewController: MBFieldByFieldOverlayViewController, didFinishScanningWith scanElements: [MBScanElement]) {
        
        fieldByFieldOverlayViewController.recognizerRunnerViewController?.pauseScanning()
        
        var dict = [String: String]()
        for element: MBScanElement in scanElements {
            if (element.scanned) {
                dict[element.identifier] = element.value
            }
        }
        
        var description : String = ""
        for (key, value) in dict {
            description += "\(key): \(value)\n"
        }
        
        let alert = UIAlertController(title: "Field by field Result", message: "\(description)", preferredStyle: UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { action in
            self.dismiss(animated: true, completion: nil)
        }))
        
        fieldByFieldOverlayViewController.present(alert, animated: true, completion: nil)
    }
    
    func field(byFieldOverlayViewControllerWillClose fieldByFieldOverlayViewController: MBFieldByFieldOverlayViewController) {
        self.dismiss(animated: true, completion: nil)
    }
}
