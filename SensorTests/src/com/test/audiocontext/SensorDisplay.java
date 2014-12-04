package com.test.audiocontext;

import java.util.List;
import java.lang.Math;
import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class SensorDisplay extends Activity implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean mInitialized;
    private StringBuilder sensorList;
    private double mLastX, mLastY, mLastZ;
    private final float NOISE = (float) 0.9f;
    private final double ALPHA = 0.15f;
    private double[] gravity = new double[3];
    private double[] maxValues =  new double[3];
    private TextView tvSensorInfo, tvX, tvY, tvZ, tvSteps, tvMaxX, tvMaxY, tvMaxZ;
    private int stepsCount = 0;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        List<Sensor> deviceSensors;

        super.onCreate(savedInstanceState);
        gravity = new double[]{0,0,0};
        maxValues = new double[]{0,0,0};
        setContentView(R.layout.main);
        System.out.println("Audiocontext test1");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mInitialized = false;
        deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        sensorList = new StringBuilder("Sensors:\n");//+= deviceSensors;
        for (Sensor s : deviceSensors)
        {
            if (s == null) continue;
            sensorList.append(s.getName()).append(", ");
            sensorList.append(s.getType()).append("\n");
        }
        sensorList.setLength (sensorList.length () - "\n".length ());
        tvSensorInfo = (TextView) findViewById(R.id.sensor_info);
        tvSensorInfo.setText(sensorList.toString());
        tvX = (TextView) findViewById(R.id.x_value);
        tvY = (TextView) findViewById(R.id.y_value);
        tvZ = (TextView) findViewById(R.id.z_value);
        tvMaxX = (TextView) findViewById(R.id.max_x);
        tvMaxY = (TextView) findViewById(R.id.max_y);
        tvMaxZ = (TextView) findViewById(R.id.max_z);
        tvSteps = (TextView) findViewById(R.id.step_count);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor,
            SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        // lowpass filter
        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

        // highpass filter
        x = event.values[0] - gravity[0];
        y = event.values[1] - gravity[1];
        z = event.values[2] - gravity[2];

        if(!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");
            mInitialized = true;
        } else {
            double deltaX = Math.abs(mLastX - x);
            double deltaY = Math.abs(mLastY - y);
            double deltaZ = Math.abs(mLastZ - z);
            if (deltaX < NOISE) deltaX = (float) 0.0;
            if (deltaY < NOISE) deltaY = (float) 0.0;
            if (deltaZ < NOISE) deltaZ = (float) 0.0;
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            tvX.setText(Double.toString(deltaX));
            tvY.setText(Double.toString(deltaY));
            tvZ.setText(Double.toString(deltaZ));
            maxValues[0] = Math.max(maxValues[0], x);
            maxValues[1] = Math.max(maxValues[1], y);
            maxValues[2] = Math.max(maxValues[2], z);
            if (deltaX > deltaY) {
             // Horizontal shake
             // do something here if you like
             
            } else if (deltaY > deltaX) {
             // Vertical shake
             // do something here if you like
             
            } else if ((deltaZ > deltaX) && (deltaZ > deltaY)) {
                // Z shake
                stepsCount = stepsCount + 1;
                if (stepsCount >= 0) {
                tvSteps.setText(String.valueOf(stepsCount));
                }
                tvMaxX.setText(Double.toString(maxValues[0]));
                tvMaxY.setText(Double.toString(maxValues[1]));
                tvMaxZ.setText(Double.toString(maxValues[2]));
                maxValues[0] = 0.0;
                maxValues[1] = 0.0;
                maxValues[2] = 0.0;
                // Just for indication purpose, I have added vibrate function
                // whenever our count moves past multiple of 10
                if ((stepsCount % 10) == 0) {
                    //Vibrator.vibrate(100);
                }
            } else {
            // no shake detected
            }
        }
    }
}
