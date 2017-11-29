use plugin from "window.cordova.plugins.Heading"

functions are 
watchHeading
 if resumeWatch / startWatch not called, this failed with status of comapss as a result, 
 call resumeWatch / startWatch before using.
 if was called proptery a json is returned with 4 key:


getHeading 
 returns the value (as round int), if resumeWatch / startWatch not called, this value maybe not be up o date

stopWatch
 stop the compass listner 


 resumeWatch / startWatch 
 start the compass listner - must be called before getting data


exmaples:
 //start compass
 window.cordova.plugins.Heading.startWatch(function(status){
     // result should be "running"
 });

 //get heading once 
  window.cordova.plugins.Heading.getHeading(function(headingAsInteger) {
      //headingAsInteger
  });

 //getting updated values
 setInterval(() => {
     window.cordova.plugins.Heading.watchHeading(
         funcion(jsonObj) {
                //jsonObj
         },
         function(errMessage) {
                //errMessage
         });
 }, 250)