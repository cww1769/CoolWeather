package com.coolweather.app.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.coolweather.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.WeatherInfo;
import com.coolweather.app.util.Trans2PinYin;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {

	private Button cityNameButton;
	private TextView publishText;
	private TextView currentDateText;
	private TextView currentTempText;
	private TextView currentWeatherText;

	private LocationClient locationClient;
	private Button refresh = null;
	private Button addCity = null;

	private static HashMap<String, List<WeatherInfo>> weatherinfoMap = new HashMap<String, List<WeatherInfo>>();
	List<WeatherInfo> weatherInfolist = new ArrayList<WeatherInfo>();
	WeatherAdapter adapter = null;

	private static List<String> cityList = null;// city name is chinese
	private static int currentIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("vivian", "WeatherActivity onCreate");
	
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		// Initialize components
		cityNameButton = (Button) findViewById(R.id.city_name);
		cityNameButton.setOnClickListener(cityNameListener);
		publishText = (TextView) findViewById(R.id.publish_time);
		currentDateText = (TextView) findViewById(R.id.current_date);
		currentTempText = (TextView) findViewById(R.id.current_temp);
		currentWeatherText = (TextView) findViewById(R.id.current_weather);
		refresh = (Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(refreshListener);
		addCity = (Button) findViewById(R.id.add_city);
		addCity.setOnClickListener(addCityListener);
		adapter = new WeatherAdapter(WeatherActivity.this,
				R.layout.weather_item_layout, weatherInfolist);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(adapter);	
		listView.setClickable(false);
		listView.setFocusable(false);
		listView.setItemsCanFocus(false);
		listView.setOnTouchListener(listTouchListener);
		
		
		// get the city list from DB
		cityList = CoolWeatherDB.getInstance(this).getSavedCity();
		// if this activity is start by selectCityActivity, then show the city
		// weather
		Intent intent = getIntent();
		String selectedCity = null;
		if (intent != null) {
			selectedCity = intent.getStringExtra("selectedCity");
		}
		if (!Utility.isStringEmpty(selectedCity)) {
			currentIndex = cityList.indexOf(selectedCity);
			showWeather(selectedCity);
		} else {//not from selectActivity
			if (!Utility.isListEmpty(cityList)) {
				String cityName = cityList.get(currentIndex);
				showWeather(cityName);
			} else {
				cityList = new ArrayList<String>();
			}

			if (Utility.isNetworkOK(getApplicationContext())) {
				queryCurLoc();
			} else {
				// use the saved data and start a service to detect the network
				// is
				// avaiable
				Toast.makeText(this, "network is unavailable",
						Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	protected void onResume() {
		Log.i("vivian", "WeatherActivity onResume");
		super.onResume();

	}

	@Override
	protected void onPause() {
		Log.i("vivian", "WeatherActivity onPause");
		// save the DB data, in case the system kill it directly
		if (cityList != null && cityList.size() > 0) {
			CoolWeatherDB.getInstance(this).deleteCites();
			CoolWeatherDB.getInstance(this).saveSelectedCities(cityList);
			for (String city : cityList) {
				List<WeatherInfo> infolist = weatherinfoMap.get(city);
				if (infolist != null && infolist.size() > 0) {
					// delete the old date
					CoolWeatherDB.getInstance(this).deleteWeatherInfo(city);
					CoolWeatherDB.getInstance(this).saveWeatherInfo(infolist);
				}
			}

		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Log.i("vivian", "WeatherActivity onDestroy");
		super.onDestroy();
	}

	private float x1 = 0, x2 = 0;
	
	

	//	@Override
	//public boolean onTouchEvent(MotionEvent event) {
	public static int threshold = 100;
	private void flipWeather(MotionEvent event){
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			x1 = event.getX();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			x2 = event.getX();
			if (x1 - x2 > threshold) {
				// move left
				Log.i("vivian", "move left. currentIndex=" + currentIndex
						+ ";cityList.size()=" + cityList.size());
				if (currentIndex < cityList.size() - 1) {
					currentIndex++;
				} else{
					currentIndex = 0;
				}
				showWeather(cityList.get(currentIndex));
			} else if (x2 - x1 > threshold) {
				// move right
				Log.i("vivian", "move right. currentIndex=" + currentIndex
						+ "cityList.size();=" + cityList.size());
				if (currentIndex > 0) {
					currentIndex--;
				} else {
					currentIndex = cityList.size() - 1;
				}
				showWeather(cityList.get(currentIndex));

			}
			x1 = 0;
			x2 = 0;
		}
	}

	
	private void showWeather(String currentCity) {
		cityNameButton.setText(currentCity);
		List<WeatherInfo> infolist = weatherinfoMap.get(currentCity);

		if (!Utility.isListEmpty(infolist)) {
			updateWeather(infolist);
		} else {
			// if there is old data in DB, use it first
			infolist = CoolWeatherDB.getInstance(this).getWeatherInfo(
					currentCity);
			if (!Utility.isListEmpty(infolist)) {
				weatherinfoMap.put(currentCity, infolist);
			}
			updateWeather(infolist);
			queryWeatherInfo(currentCity);
		}
	}

	private void queryCurLoc() {
		// get the current location
		locationClient = new LocationClient(this);
		Utility.initLocationClient(locationClient);
		locationClient.registerLocationListener(myLocationListener);
		locationClient.start();
	}
	
	private OnTouchListener listTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			//Log.i("vivian", "listTouchListener arg0 = " + arg0.getClass().getName() + "; arg1=" + arg1.getAction());
			flipWeather(arg1);
			return false;
		}

		
	};

	private OnClickListener refreshListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Log.i("vivian", "onClick refresh");
			if (Utility.isNetworkOK(getApplicationContext())) {
				String currentCity = cityList.get(currentIndex);
				String showCity = cityNameButton.getText().toString();
				if (currentIndex == 0 && currentCity.equals(showCity)) {
					queryCurLoc();
				} else {					
					queryWeatherInfo(showCity);
				}
			} else {
				// use the saved data and start a service to detect the network
				// is avaiable
				Toast.makeText(WeatherActivity.this, "network is unavailable",
						Toast.LENGTH_LONG).show();
			}
		}

	};

	private OnClickListener addCityListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent = new Intent(WeatherActivity.this,
					SelectedCityActivity.class);
			intent.putStringArrayListExtra("cityList",
					(ArrayList<String>) cityList);
			startActivity(intent);
		}

	};
	
	private OnClickListener cityNameListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Log.i("vivian", "change a city and view its weather");
			Intent intent = new Intent(WeatherActivity.this, ProvinceCityActivity.class);
			startActivityForResult(intent, 1);
		}

	};

	private BDLocationListener myLocationListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.i("vivian", "onReceiveLocation");
			locationClient.unRegisterLocationListener(this);
			locationClient.stop();
			locationClient = null;
			String rawCity = location.getCity();
			Log.i("vivian", "" + rawCity);
			String city = rawCity.substring(0, rawCity.length() - 1);
			// save current city
			if (cityList.size() > 0) {
				cityList.remove(0);
			}
			cityNameButton.setText(city);
			cityList.add(0, city);
			Log.i("vivian", "cityList.size() = " + cityList.size());
			queryWeatherInfo(city);
		}
	};

	private final static String baseUrl = "https://api.thinkpage.cn/v3/";
	private final static String key = "qjep6bnezutgrbus";

	private void queryWeatherInfo(String city) {
		// trans to pingyin
		String city_pinyin = Trans2PinYin.trans2PinYin(city);
		final String daily_url = baseUrl + "weather/daily.json?key=" + key
				+ "&location=" + city_pinyin
				+ "&language=zh-Hans&unit=c&start=0&days=7";
		final String now_url = baseUrl + "weather/now.json?key=" + key
				+ "&location=" + city_pinyin + "&language=zh-Hans&unit=c";
		Utility.sendRequestToServer(now_url, QUERY_NOW, handler);
		Utility.sendRequestToServer(daily_url, QUERY_DAILY, handler);

	}

	public static final int QUERY_NOW = 0;
	public static final int QUERY_DAILY = 1;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd",
			Locale.CHINA);

	private void updateWeather(List<WeatherInfo> infolist) {
		if (!Utility.isListEmpty(infolist)) {
			// update current weather
			WeatherInfo info = infolist.get(0);
			publishText.setText(info.getPublishTime() + " ·¢²¼");
			currentTempText.setText(info.getCurrent_tmp() + " ¡æ");
			currentWeatherText.setText(info.getCurrent_weather());
			currentDateText.setText(dateFormat.format(new Date()));
		}else{
		//clean the layout
			infolist = new ArrayList<WeatherInfo>();
			publishText.setText("");
			currentTempText.setText("");
			currentWeatherText.setText("");
			currentDateText.setText(dateFormat.format(new Date()));
			
		}
		
		Utility.copyList(weatherInfolist, infolist);
		adapter.notifyDataSetChanged();

	}

	private boolean daily_flag = false;
	private WeatherInfo nowWeatherinfo = null;
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i("vivian", "handleMessage");
			String cityName = cityNameButton.getText().toString();
			if (msg.obj instanceof Exception) {
				Toast.makeText(WeatherActivity.this, "unable to load data",	Toast.LENGTH_SHORT).show();
				return;
			} else if (msg.obj instanceof String) {
				String weatherJson = (String) msg.obj;				
				List<WeatherInfo> infolist = null;
				switch (msg.what) {
				case QUERY_DAILY:
					try{
						infolist = Utility.getWeatherInfoDaily(weatherJson);
					}catch(Exception e){
						Toast.makeText(WeatherActivity.this, "unable to load data",	Toast.LENGTH_SHORT).show();
						return;
					}
					weatherinfoMap.put(cityName, infolist);
					daily_flag = true;
					break;
				case QUERY_NOW:
					try{
						nowWeatherinfo = Utility.getWeatherInfoNow(weatherJson);
					}catch(Exception e){
						Toast.makeText(WeatherActivity.this, "unable to load data",	Toast.LENGTH_SHORT).show();
						return;
					}
					break;
				default:
					break;
				}
				// only when now and daily data both come, update the weather
				if (daily_flag && nowWeatherinfo != null) {
					infolist = weatherinfoMap.get(cityName);
					if (!Utility.isListEmpty(infolist)){
						Utility.updateNowWeather(infolist.get(0), nowWeatherinfo);
						updateWeather(infolist);
					}else{
						Toast.makeText(WeatherActivity.this, "unable to load data",	Toast.LENGTH_SHORT).show();
					}
					// reset flag
					daily_flag = false;
					nowWeatherinfo = null;
				}
			}

		}
	};
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("vivian", "WeatherActivity onActivityResult requestCode="
				+ requestCode + "; resultCode=" + resultCode);
		if (requestCode == 1){
			String newCity = data.getStringExtra("newCity");
			showWeather(newCity);
			//cityList.add(newCity);
		}

	}

}
