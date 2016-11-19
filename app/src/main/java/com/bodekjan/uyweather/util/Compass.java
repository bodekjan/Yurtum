package com.bodekjan.uyweather.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class Compass {

	private final float MAX_ROATE_DEGREE = 1.0f;
	private SensorManager mSensorManager;
	private Sensor mOrientationSensor;
	private Sensor mPressureSensor;
	private float mDirection;
	private float mTargetDirection;
	private AccelerateInterpolator mInterpolator;
	protected final Handler mHandler = new Handler();
	private float azimuth = 0f;
	private float currectAzimuth = 0;
	float showDegre=0;
	DecimalFormat df = new DecimalFormat("#");
	DecimalFormat pr = new DecimalFormat("#.##");
	// compass arrow to rotate
	public ImageView arrowView = null;
	public TextView arrowDegre =null;
	public TextView altitude = null;
	public TextView pressure = null;

	protected Runnable mCompassViewUpdater = new Runnable() {
		@Override
		public void run() {
			if (true) {
				if (mDirection != mTargetDirection) {

					// calculate the short routine
					float to = mTargetDirection;
					if (to - mDirection > 180) {
						to -= 360;
					} else if (to - mDirection < -180) {
						to += 360;
					}
					// limit the max speed to MAX_ROTATE_DEGREE
					float distance = to - mDirection;
					if (Math.abs(distance) > MAX_ROATE_DEGREE) {
						distance = distance > 0 ? MAX_ROATE_DEGREE : (-1.0f * MAX_ROATE_DEGREE);
					}
					// need to slow down if the distance is short
					mDirection = normalizeDegree(mDirection
							+ ((to - mDirection) * mInterpolator.getInterpolation(Math
							.abs(distance) > MAX_ROATE_DEGREE ? 0.4f : 0.3f)));
					azimuth=mDirection;
					azimuth=360-azimuth;
				}
				adjustArrow();
				mHandler.postDelayed(mCompassViewUpdater, 20);
			}
		}
	};
	public Compass(Context context) {
		initResources();
		initServices(context);
	}
	private void initResources() {
		mDirection = 0.0f;
		mTargetDirection = 0.0f;
		mInterpolator = new AccelerateInterpolator();
	}

	private void initServices(Context context) {
		// sensor manager
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mPressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

		// location manager
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
	}
	public void start() {
		if (mOrientationSensor != null) {
			mSensorManager.registerListener(mOrientationSensorEventListener, mOrientationSensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
		if (mPressureSensor != null) {
			mSensorManager.registerListener(mPressureSensorEventListener, mPressureSensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
		mHandler.postDelayed(mCompassViewUpdater, 20);
	}

	public void stop() {
		if (mOrientationSensor != null) {
			mSensorManager.unregisterListener(mOrientationSensorEventListener);
		}
		if (mPressureSensor != null) {
			mSensorManager.unregisterListener(mPressureSensorEventListener);
		}
	}

	private void adjustArrow() {
		if (arrowView == null) {
			return;
		}
		//Log.i(TAG, "will set rotation from " + currectAzimuth + " to "+ azimuth);
		Animation an = new RotateAnimation(-currectAzimuth, -azimuth,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		currectAzimuth = azimuth;
		showDegre=360-currectAzimuth;
		an.setDuration(500);
		an.setRepeatCount(0);
		an.setFillAfter(true);
		arrowView.startAnimation(an);
		arrowDegre.setText(df.format(showDegre));
	}

	private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			float direction = event.values[0] * -1.0f;
			mTargetDirection = normalizeDegree(direction);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	private SensorEventListener mPressureSensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			//altitude.setText((int) calculateAltitude(event.values[0])+" m");
			//pressure.setText(pr.format(event.values[0] / 10)+" MPa");
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	private float calculateAltitude(float pressure) {
		float sp = 1013.25f; //standard pressure
		return (sp - pressure) * 100.0f / 12.7f;
	}

	private float normalizeDegree(float degree) {
		return (degree + 720) % 360;
	}
}
