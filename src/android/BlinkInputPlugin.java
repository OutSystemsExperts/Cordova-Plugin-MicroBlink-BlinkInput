package com.outsystems.blinkinput;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import com.microblink.MicroblinkSDK;
import com.microblink.activity.FieldByFieldScanActivity;
import com.microblink.entities.detectors.Detector;
import com.microblink.entities.detectors.quad.document.DocumentDetector;
import com.microblink.entities.detectors.quad.document.DocumentSpecification;
import com.microblink.entities.detectors.quad.document.DocumentSpecificationPreset;
import com.microblink.entities.parsers.amount.AmountParser;
import com.microblink.entities.parsers.config.fieldbyfield.FieldByFieldBundle;
import com.microblink.entities.parsers.config.fieldbyfield.FieldByFieldElement;
import com.microblink.entities.parsers.date.DateParser;
import com.microblink.entities.parsers.regex.RegexParser;
import com.microblink.uisettings.ActivityRunner;
import com.microblink.uisettings.FieldByFieldUISettings;
import com.microblink.util.RecognizerCompatibility;
import com.microblink.util.RecognizerCompatibilityStatus;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class BlinkInputPlugin extends CordovaPlugin {

    private static final String init = "initSDK";
    private static final String supported = "check_supported_device";
    private static final String cheque = "cheque";
    private static final String id = "id_scan";
    private static final String a4PaperPortrait = "a4_portrait";
    private static final String a4PaperLandscape = "a4_landscape";
    private static final String chequeOCR = "chequeOCR";

    private static final int CHEQUE_REQUEST_CODE = 1111;
    private static final int ID_REQUEST_CODE = 1112;
    private static final int PORTRAIT_REQUEST_CODE = 1113;
    private static final int LANDSCAPE_REQUEST_CODE = 1114;

    private static final int BLINK_INPUT_REQUEST_CODE = 3113;
    private static final int EMAIL_REQUEST_CODE = 3114;
    private static final int DATE_REQUEST_CODE = 3115;
    private static final int AMOUNT_REQUEST_CODE = 3116;
    private static final int NAME_REQUEST_CODE = 3118;

    private CallbackContext mContextCallback;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        mContextCallback = callbackContext;

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
        } else if (chequeOCR.equals(action)){
            onSimpleIntegrationClick();
            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == FieldByFieldScanActivity.RESULT_OK) {
            switch (requestCode) {
                case CHEQUE_REQUEST_CODE: uriToImage(intent.getExtras().get("uri").toString()); break;

                case ID_REQUEST_CODE: uriToImage(intent.getExtras().get("uri").toString()); break;

                case LANDSCAPE_REQUEST_CODE: uriToImage(intent.getExtras().get("uri").toString()); break;

                case PORTRAIT_REQUEST_CODE: uriToImage(intent.getExtras().get("uri").toString()); break;

                case EMAIL_REQUEST_CODE: break; //TODO:

                case DATE_REQUEST_CODE: break; //TODO:

                case AMOUNT_REQUEST_CODE: break; //TODO:

                case NAME_REQUEST_CODE: break; //TODO:

                case BLINK_INPUT_REQUEST_CODE: mFieldByFieldBundle.loadFromIntent(intent); blinkinput(); break;

                default: break;
            }
        } else {
            mContextCallback.error("Action cancelled");
        }
    }

    private void uriToImage(String uri) {
        Uri selectedfile = Uri.parse(uri);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(cordova.getActivity().getContentResolver(), selectedfile);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] byteArray = outputStream.toByteArray();

            String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image", encodedString)
                    .put("success", true);
            mContextCallback.success(jsonObject);
        } catch (Exception e) {
            mContextCallback.error(e.getMessage());
        }
    }

    private void blinkinput() {
        String ammout = mTotalAmountParser.getResult().toString();
        String date = mDateParser.getResult().toString();
        String payTo = mPayToNameParser.getResult().toString();
        JSONObject json = new JSONObject();

        try {
            json.put("amount", ammout)
                    .put("date", date)
                    .put("payTo", payTo)
                    .put("success", true);
            mContextCallback.success(json);
        } catch(JSONException e) {
            mContextCallback.error(e.getMessage());
        }
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

    private AmountParser mTotalAmountParser;
    private DateParser mDateParser;
    private RegexParser mPayToNameParser;
    private FieldByFieldBundle mFieldByFieldBundle;

    public void onSimpleIntegrationClick() {
        /*
         * In this simple example we will use BlinkInput SDK and provided scan activity to scan
         * invoice fields: amount, tax amount and IBAN to which amount has to be paid.
         */

        mTotalAmountParser = new AmountParser();
        mDateParser = new DateParser();
        mPayToNameParser = new RegexParser("[A-Za-z ]+");
        mFieldByFieldBundle = new FieldByFieldBundle(
                new FieldByFieldElement("Total Amount", "Position Total Amount in this frame", mTotalAmountParser),
                new FieldByFieldElement("Date", "Position Date in this frame", mDateParser),
                new FieldByFieldElement("Pay to Name", "Position Pay to Name in this frame", mPayToNameParser)
        );

        // we use FieldByFieldUISettings - settings for FieldByFieldScanActivity
        FieldByFieldUISettings scanActivitySettings = new FieldByFieldUISettings(mFieldByFieldBundle);

        // this helper method should be used for starting the provided activities with prepared
        // scan settings
        cordova.setActivityResultCallback(this);
        ActivityRunner.startActivityForResult(cordova.getActivity(), BLINK_INPUT_REQUEST_CODE, scanActivitySettings);
    }

}