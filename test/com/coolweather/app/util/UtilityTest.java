package com.coolweather.app.util;

import android.util.Log;
import junit.framework.TestCase;

public class UtilityTest extends TestCase {
	
	private static final String TAG = "vivian";
	public void testGetWeatherInfo(){
		Log.i(TAG, "testGetWeatherInfo begin");
		Log.i(TAG, "testGetWeatherInfo bend");
	}
	
	public void testGetProvinces(){
		Log.i(TAG, "testGetProvinces begin");
		String data ="01|����,02|�Ϻ�,03|���,04|����,05|������,06|����,07|����,08|���ɹ�,09|�ӱ�,10|ɽ��,11|����,12|ɽ��,13|�½�,14|����,15|�ຣ,16|����,17|����,18|����,19|����,20|����,21|�㽭,22|����,23|����,24|����,25|����,26|����,27|�Ĵ�,28|�㶫,29|����,30|����,31|����,32|���,33|����,34|̨��";
		Utility.getProvinces(data);
		Log.i(TAG, "testGetProvinces bend");
	}

}
