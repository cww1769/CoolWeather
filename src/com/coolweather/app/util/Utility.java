package com.coolweather.app.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.coolweather.app.db.City;
import com.coolweather.app.db.Province;
import com.coolweather.app.model.WeatherInfo;

public class Utility {

	/*
	 * 
	 * {"results":[{"location":{"id":"WX4FBXXFKE4F","name":"北京","country":"CN",
	 * "path":"北京,北京,中
	 * 
	 * 国","timezone":"Asia/Shanghai","timezone_offset":"+08:00"},"daily":[{"date":"
	 * 2016-12-07","text_day":"多
	 * 
	 * 云","code_day":"4","text_night":"霾","code_night":"31","high":"8","low":"-
	 * 
	 * 2","precip":"","wind_direction":"南","wind_direction_degree":"180","wind_speed
	 * ":"10","wind_scale":"2"},{"date":"2016-12-
	 * 
	 * 08","text_day":"晴","code_day":"0","text_night":"晴","code_night":"0","high":"
	 * 7","low":"-2","precip":"","wind_direction":"无持续风
	 * 
	 * 向","wind_direction_degree":"","wind_speed":"20","wind_scale":"4"},{"date":"
	 * 2016-12-09","text_day":"多云","code_day":"4","text_night":"多
	 * 
	 * 云","code_night":"4","high":"4","low":"-
	 * 
	 * 5","precip":"","wind_direction":"南","wind_direction_degree":"180","wind_speed
	 * ":"10","wind_scale":"2"}],"last_update":"2016-12-07T18:00:00+08:00"}]}
	 */
	public static List<WeatherInfo> getWeatherInfoDaily(String jsonData) throws Exception{
		ArrayList<WeatherInfo> infolist = new ArrayList<WeatherInfo>();
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			JSONArray results = jsonObject.getJSONArray("results");
			JSONObject result = results.getJSONObject(0);

			JSONObject location = result.getJSONObject("location");
			String cityName = location.getString("name");

			JSONArray daily = result.getJSONArray("daily");
			/*
			 * Log.i("vivian", "length=" + daily.length()); Log.i("vivian",
			 * "ff=" + daily.toString()); Log.i("vivian", "vv=" +
			 * daily.getJSONObject(0).toString());
			 */

			for (int i = 0; i < daily.length(); i++) {
				WeatherInfo info = new WeatherInfo();
				JSONObject obj = daily.getJSONObject(i);
				info.setCityName(cityName);
				info.setDate(obj.getString("date"));
				info.setDay_weather(obj.getString("text_day"));
				info.setNight_weather(obj.getString("text_night"));
				info.setHigh_tmp(obj.getString("high"));
				info.setLow_tmp(obj.getString("low"));
				info.setWind(obj.getString("wind_direction"));
				info.setWind_scal(obj.getString("wind_scale") + "级");
				infolist.add(info);
			}

			/*
			 * //get cityname JSONObject location =
			 * result.getJSONObject("location"); String cityName =
			 * location.getString("name"); weatherInfo.setCityName(cityName);
			 * //get now temperature JSONObject now =
			 * result.getJSONObject("now"); String now_temp =
			 * now.getString("temperature"); weatherInfo.setTemp1(now_temp);
			 * String weatherDesc = now.getString("text");
			 * weatherInfo.setWeather(weatherDesc); String lastUpdate =
			 * result.getString("last_update");
			 * 
			 * //get hh:mm:ss+ZZ String tmp1 = lastUpdate.split("T")[1]; //only
			 * need to hh:mm String[] tmp2 = tmp1.split(":");
			 * weatherInfo.setPublishTime(tmp2[0]+":"+ tmp2[1]);
			 */
		} catch (Exception e) {
			throw e;
		}
		return infolist;
	}

	public static WeatherInfo getWeatherInfoNow(String jsonData) throws Exception{
		WeatherInfo weatherInfo = new WeatherInfo();
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			JSONArray results = jsonObject.getJSONArray("results");
			JSONObject result = results.getJSONObject(0);

			// get cityname
			JSONObject location = result.getJSONObject("location");
			String cityName = location.getString("name");
			weatherInfo.setCityName(cityName);
			// get now temperature
			JSONObject now = result.getJSONObject("now");
			String now_temp = now.getString("temperature");
			weatherInfo.setCurrent_tmp(now_temp);
			String weatherDesc = now.getString("text");
			weatherInfo.setCurrent_weather(weatherDesc);
			String lastUpdate = result.getString("last_update");

			// get hh:mm:ss+ZZ
			String tmp1 = lastUpdate.split("T")[1];
			// only need to hh:mm
			String[] tmp2 = tmp1.split(":");
			weatherInfo.setPublishTime(tmp2[0] + ":" + tmp2[1]);
		} catch (Exception e) {
			throw e;
		}
		return weatherInfo;
	}
	
	public static void updateNowWeather(WeatherInfo info1, WeatherInfo nowInfo){
		if (info1 !=null && nowInfo != null){
			info1.setCurrent_tmp(nowInfo.getCurrent_tmp());
			info1.setCurrent_weather(nowInfo.getCurrent_weather());
			info1.setPublishTime(nowInfo.getPublishTime());
		}
	}

	public static Location getBestLocation(LocationManager locationManager) {
		Location result = null;
		if (locationManager != null) {
			result = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (result != null) {
				Log.i("vivian", "aaa");
				return result;
			} else {
				result = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (result == null) {
					Log.i("vivian", "bbb");
				}
				return result;
			}
		}
		return result;
	}

	public static String getAddress(Context context, Location location) {
		String address = null;
		if (location != null) {
			Log.i("vivian", "latitude=" + location.getLatitude());
			Log.i("vivian", "longtitude=" + location.getLongitude());
			Log.i("vivian", "speed=" + location.getSpeed());
			/*
			 * String url =
			 * "http://maps.googleapis.com/maps/api/geocode/json?latlng=" +
			 * location.getLatitude() + "," + location.getLongitude() +
			 * "&sensor=false"; sendRequestToServer(url, QUERY_LOCATION);
			 */
			Geocoder geo = new Geocoder(context, Locale.CHINA);
			try {
				List<Address> addressList = geo.getFromLocation(
						location.getLatitude(), location.getLongitude(), 1);
				if (addressList.size() > 0) {
					address = addressList.get(0).getAddressLine(0);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			Log.i("vivian", "no location");
		}

		Log.i("vivian", "address=" + address);
		return address;
	}
	
	
	public static void initLocationClient(LocationClient locationClient) {
		
		Log.d("vivian","initLocationClient()");
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(false);// 可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
		option.setIsNeedLocationDescribe(false);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
		locationClient.setLocOption(option);

	}
	
	
	public static boolean isNetworkOK(Context context){
		Log.i("vivian", "enter isNetworkOK");
		boolean isOK = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager != null)
		{
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			
			if (networkInfo != null && networkInfo.length > 0)
			{
				for (int i = 0; i < networkInfo.length; i++)
				{
					/*Log.i("vivian", "networkInfo[" + i + "].getState()" + networkInfo[i].getState());
					Log.i("vivian", "networkInfo[" + i + "].getTypeName()" + networkInfo[i].getTypeName());
					Log.i("vivian", "networkInfo[" + i + "].getSubtypeName()" + networkInfo[i].getSubtypeName());*/
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
					{
						isOK = true;
					}
				}
			}
		}
		Log.i("vivian", "exit isNetworkOK with isOK" + isOK);
		return isOK;
	}
	
	public static void sendRequestToServer(final String url, final int type, final Handler handler){
		new Thread(new Runnable(){
			@Override
			public void run(){
				Log.i("vivian", " sendRequestToServer url=" + url);
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try{
					HttpResponse httpRes = httpclient.execute(httpGet);
					if (httpRes.getStatusLine().getStatusCode() 
							== HttpStatus.SC_OK){
						HttpEntity entity = httpRes.getEntity();
						String response = EntityUtils.toString(entity,"utf-8");	
						//Log.i("vivian", response);
						Message message = handler.obtainMessage();
						message.what = type;
						message.obj = response;
						handler.sendMessage(message);
					}else{
						throw new Exception("invalid status code = " + httpRes.getStatusLine().getStatusCode() );
					}
				}catch(Exception e){
					Message message = handler.obtainMessage();
					message.what = type;
					message.obj = e;
					handler.sendMessage(message);
				}
			}			
		}).start();
	}

	public static List<Province> getProvinces(String data){
		//Log.i("vivian", " getProvinces data=" + data);
		List<Province> provinceList = new ArrayList<Province>();
		if (!isStringEmpty(data)){
			String[] allProvinces = data.split(",");
			if (allProvinces != null && allProvinces.length > 0){
				for (String p : allProvinces){
					
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					provinceList.add(province);
					/*Log.i("vivian", " getProvinces array[0]="+ array[0] + ";array[1]=" + array[1]);
					printBytes(array[0]);*/
				}
			}
		}
		return provinceList;
	}
	
	public static void printBytes(String p){
		byte[] b = p.getBytes();
		for (int i = 0; i < b.length; i++){
			Log.i("vivian", " getProvinces b="+ i + "; b[i]" + b[i]);
		}
	}
	
	public static List<City> getCities(String data, String code){
		//Log.i("vivian", " getCities data=" + data + "code = " + code);
		List<City> cityList = new ArrayList<City>();
		if (!isStringEmpty(data)){
			String[] allCities = data.split(",");
			if (allCities != null && allCities.length > 0){
				for (String p : allCities){
					String[] array = p.split("\\|");
					City city = new City();
					city.setProvinceCode(code);
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					cityList.add(city);
					//Log.i("vivian", "getCities array[0]="+ array[0] + ";array[1]=" + array[1] + ";code=" + code);
				}
			}
		}
		return cityList;
	}
	public static boolean isListEmpty(List<?> list){
		if (list == null || list.isEmpty()){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	public static boolean isStringEmpty(String s){
		if (s == null || s.length() == 0){
			return true;
		}else{
			return false;
		}
	}

	public static <T> void printList(List<T> list){
		if (!isListEmpty(list)){
			for (int i = 0; i < list.size(); i ++){
				Log.i("vivian", "list index=" + i + "; o=" + list.get(i));
			}
		}
	}
	
	public static <E> void copyList(List<E> list1, List<E> list2){
		if (list2 != null){
		list1.clear();
		for (int i = 0; i < list2.size(); i ++){
			list1.add(list2.get(i));
		}
		}
	}
}
