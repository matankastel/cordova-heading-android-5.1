package cordova.plugin.heading;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.content.Context;

/**
 * This class echoes a string called from JavaScript.
 */
public class Heading extends CordovaPlugin {
    private Compass compass;
    private static final String TAG = "CordovaPlugin";
    private String status = "unknown";

     /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        SensorManager sensorManager = (SensorManager) cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);
        compass = new Compass(sensorManager);
        Log.d(TAG, "initialize compass");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("watchHeading")) {
            this.watchHeading(callbackContext);
            return true;
        }
        if (action.equals("stopWatch")) {
            this.stopWatch(callbackContext);
            return true;
        }
        if (action.equals("resumeWatch")) {
            this.resumeWatch(callbackContext);
            return true;
        }
        if (action.equals("startWatch")) {
            this.startWatch(callbackContext);
            return true;
        }
        if (action.equals("getHeading")){
            this.getHeading(callbackContext);
            return true;
        }
        return false;
    }
    private void getHeading(CallbackContext callbackContext){
        callbackContext.success(Math.round(compass.getAzimuth()));
    }
    
    private void startWatch(CallbackContext callbackContext) {
        this.start();
        callbackContext.success(this.getStatus());
    }

    private void resumeWatch(CallbackContext callbackContext) {
        this.start();
        callbackContext.success(this.getStatus());
    }

    private void stopWatch(CallbackContext callbackContext) {
        this.stop();
        callbackContext.success(this.getStatus());
    }

    private void watchHeading(CallbackContext callbackContext) {
        if(this.getStatus().equals("running")){
            try {
                callbackContext.success(this.getCompassHeading());
            }
            catch (JSONException ex) {
                callbackContext.error("JSONException "+ex.getMessage());
            }
        }
        else {
            callbackContext.error("please use resume before using watch. curent status is:"+this.getStatus());
        }
    
    }

     /**
     * Create the CompassHeading JSON object to be returned to JavaScript
     *
     * @return a compass heading
     */
    private JSONObject getCompassHeading() throws JSONException {
        JSONObject obj = new JSONObject();
        float heading = compass.getAzimuth();
        obj.put("magneticHeading", heading);
        obj.put("trueHeading", heading);
        obj.put("headingAccuracy", 0);
        obj.put("timestamp", System.currentTimeMillis());

        return obj;
    }

    private String getStatus() {
        return this.status;
    }
    private void setStatus(String newStatus) {
        this.status = newStatus;
    }

    private void start(){
        Log.d(TAG, "start compass");
        this.setStatus("running");
        compass.start();
    }

    private void stop(){
        Log.d(TAG, "stop compass");
        this.setStatus("stopped");
        compass.stop();
    }
}

class Compass implements SensorEventListener {
    private static final String TAG = "Compass";

    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currectAzimuth = 0;

    public Compass(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void start() {
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    private void adjustArrow() {
        // Log.i(TAG, "will set rotation from " + currectAzimuth + " to "
        //         + azimuth);

        currectAzimuth = azimuth;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                        * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                        * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                        * event.values[2];
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                        * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                        * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                        * event.values[2];

            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                    mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + 360) % 360;
                adjustArrow();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public float getAzimuth() {
        return this.currectAzimuth;
    }
}