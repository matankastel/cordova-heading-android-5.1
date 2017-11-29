var exec = require('cordova/exec');

var heading = {
    watchHeading: function(successCallback, errorCallback, args0) {

        var win = function(result) {
            var ch = {
                heading: result.magneticHeading,
                trueHeading: result.trueHeading, 
                headingAccuracy: result.headingAccuracy,
                timestamp: result.timestamp
            };
            successCallback(ch);
        };
        var fail = errorCallback && function(code) {
            var ce = code;
            errorCallback(ce);
        };

        exec(win, fail, 'Heading', 'watchHeading', [args0]);
    },
    getHeading: function(successCallback){
        var win = function(result) {
            var ch = result;
            successCallback(ch);
        };
        
        exec(win, null, 'Heading', 'getHeading');
    },
    stopWatch: function(successCallback) {

        var win = function(result) {
            var ch = result;
            successCallback(ch);
        };
        
        exec(win, null, 'Heading', 'stopWatch');
    },
    resumeWatch: function(successCallback) {

        var win = function(result) {
            var ch = result;
            successCallback(ch);
        };
        
        exec(win, null, 'Heading', 'resumeWatch');
    },
    startWatch: function(successCallback) {

        var win = function(result) {
            var ch = result;
            successCallback(ch);
        };
        
        exec(win, null, 'Heading', 'startWatch');
    }
}

module.exports = heading;
