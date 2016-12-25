package com.coolweather.app.util;

import android.util.Log;

public class Logger {

	
	private static final String TAG = "vivian"; 
	private static final int currentLEVEL = Log.INFO;
	private Class<?> mClass = null;

	
	private Logger(Class<?> myclass){
		mClass = myclass;
	}
	
	public static Logger getLogger(Class<?> myclass){
		return new Logger(myclass);
	}
	
	public void v(String msg){
		if (currentLEVEL <= Log.VERBOSE){
			Log.v(TAG, mClass.getName() + ": " + msg);
		}
	}
	
	public void d(String msg){
		if (currentLEVEL <= Log.DEBUG){
			Log.d(TAG, mClass.getName() + ": " + msg);
		}
	}
	
	public void i(String msg){
		if (currentLEVEL <= Log.INFO){
			Log.i(TAG, mClass.getName() + ": " + msg);
		}
	}
	
	public void w(String msg){
		if (currentLEVEL <= Log.WARN){
			Log.w(TAG, mClass.getName() + ": " + msg);
		}
	}
	
	public void e(String msg){
		if (currentLEVEL <= Log.ERROR){
			Log.e(TAG, mClass.getName() + ": " + msg);
		}
	}
}
