var exec = require('cordova/exec');
const pluginName = 'BlinkInputPlugin';


exports.init = function (success, error, license) {
    exec(success, error, pluginName, 'initSDK', [license]);
};

exports.check_supported_device = function (success, error) {
    exec(success, error, pluginName, 'check_supported_device', []);
};

exports.chequeScan = function (success, error) {
    exec(success, error, pluginName, 'cheque', []);
};

exports.idScan = function (success, error) {
    exec(success, error, pluginName, 'id_scan', []);
};

exports.portraitScan = function (success, error) {
    exec(success, error, pluginName, 'a4_portrait', []);
};

exports.landscapeScan = function (success, error) {
    exec(success, error, pluginName, 'a4_landscape', []);
};


exports.chequeOCR = function (success, error) {
    exec(success, error, pluginName, 'cheque_ocr', []);
};