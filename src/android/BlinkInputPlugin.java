package com.outsystems.blinkinput;

import android.content.Intent;
import android.widget.Toast;

import com.microblink.MicroblinkSDK;
import com.microblink.entities.detectors.Detector;
import com.microblink.entities.detectors.quad.document.DocumentDetector;
import com.microblink.entities.detectors.quad.document.DocumentSpecification;
import com.microblink.entities.detectors.quad.document.DocumentSpecificationPreset;
import com.microblink.util.RecognizerCompatibility;
import com.microblink.util.RecognizerCompatibilityStatus;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class BlinkInputPlugin extends CordovaPlugin {

    private static String init = "init";
    private static String supported = "check_supported_device";
    private static String cheque = "cheque";
    private static String id = "id_scan";
    private static String a4PaperPortrait = "a4_portrait";
    private static String a4PaperLandscape = "a4_landscape";

    private final static int CHEQUE_REQUEST_CODE = 1111;
    private final static int ID_REQUEST_CODE = 1112;
    private final static int PORTRAIT_REQUEST_CODE = 1113;
    private final static int LANDSCAPE_REQUEST_CODE = 1114;



    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (init.equals(action)) {
            MicroblinkSDK.setLicenseKey(args.getString(0), cordova.getActivity());
            callbackContext.success();
            return true;
            /*
             * Check if current device is able to use the SDK
             */
        } else if (supported.equals(action)) {
            RecognizerCompatibilityStatus supportStatus = RecognizerCompatibility.getRecognizerCompatibilityStatus(cordova.getActivity());
            if (supportStatus != RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED) {
                callbackContext.error(supportStatus.name());
            } else {
                callbackContext.success();
            }
            return true;
        } else if (cheque.equals(action)) {
            //Intent intent = new Intent(cordova.getActivity(), MenuActivity.class);
            //cordova.getActivity().startActivity(intent);
            this.cheque();
            return true;
        } else if (id.equals(action)) {
            this.idScan();
            return true;
        } else if (a4PaperPortrait.equals(action)) {
            this.a4Portrait();
            return true;
        } else if (a4PaperLandscape.equals(action)) {
            this.a4Landscape();
            return true;
        }

        callbackContext.error("An error occurred");
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

    }

    private void cheque() {
        DocumentSpecification chequeSpec = DocumentSpecification.createFromPreset(
                DocumentSpecificationPreset.DOCUMENT_SPECIFICATION_PRESET_CHEQUE);
        buildDocumentDetectorElement(chequeSpec, CHEQUE_REQUEST_CODE);
    }

    private void idScan() {
        DocumentSpecification idSpec = DocumentSpecification.createFromPreset(
                DocumentSpecificationPreset.DOCUMENT_SPECIFICATION_PRESET_ID1_CARD);
        buildDocumentDetectorElement(idSpec, ID_REQUEST_CODE);
    }

    private void a4Portrait() {
        DocumentSpecification a4PortraitSpec = DocumentSpecification.createFromPreset(
                DocumentSpecificationPreset.DOCUMENT_SPECIFICATION_PRESET_A4_PORTRAIT);
        buildDocumentDetectorElement(a4PortraitSpec, PORTRAIT_REQUEST_CODE);
    }

    private void a4Landscape() {
        DocumentSpecification a4LandscapeSpec = DocumentSpecification.createFromPreset(
                DocumentSpecificationPreset.DOCUMENT_SPECIFICATION_PRESET_A4_LANDSCAPE);
        buildDocumentDetectorElement(a4LandscapeSpec, LANDSCAPE_REQUEST_CODE);
    }

    private void buildDocumentDetectorElement(DocumentSpecification documentSpec, int requestCode) {
        // prepare document detector with defined document specification
        final DocumentDetector documentDetector = new DocumentDetector(documentSpec);
        // set minimum number of stable detections to return detector result
        documentDetector.setNumStableDetectionsThreshold(3);
        cordova.startActivityForResult((CordovaPlugin)this, buildDetectorIntent(documentDetector), requestCode);
    }

    /**
     * Builds intent that can be used to start the {@link DetectorActivity} with given detector.
     * @param detector Detector that will be used.
     * @return Intent that can be used to start the {@link DetectorActivity} with given detector.
     */
    private Intent buildDetectorIntent(Detector detector) {
        Intent intent = new Intent(cordova.getActivity(), DetectorActivity.class);
        // pass prepared detector to activity
        intent.putExtra(DetectorActivity.EXTRAS_DETECTOR, detector);
        return intent;
    }

}

