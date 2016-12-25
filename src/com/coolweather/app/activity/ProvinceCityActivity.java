package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.R;
import com.coolweather.app.db.City;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.db.Province;
import com.coolweather.app.model.WeatherInfo;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ProvinceCityActivity extends Activity {

	private List<Province> provinceList;
	private List<City> cityList;
	private ArrayAdapter<String> provinceAdapter;
	private ArrayAdapter<String> cityAdapter;
	private ListView provinceListView;
	private ListView cityListView;
	private List<String> provinceNames = new ArrayList<String>();
	private List<String> cityNames = new ArrayList<String>();
	private Province selectedProvince;
	private City selectedCity;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("vivian", "ProvinceCityActivity onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.province_city);

		provinceListView = (ListView) findViewById(R.id.province_list_view);
		cityListView = (ListView) findViewById(R.id.city_list_view);
		provinceAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, provinceNames);
		cityAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, cityNames);
		provinceListView.setAdapter(provinceAdapter);
		cityListView.setAdapter(cityAdapter);
		provinceListView.setOnItemClickListener(provinceClickListener);
		cityListView.setOnItemClickListener(cityClickListener);
		queryProvinces();
	}

	@Override
	protected void onResume() {
		Log.i("vivian", "ProvinceCityActivity onResume");
		super.onResume();

	}

	private OnItemClickListener provinceClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int index,
				long arg3) {
			Log.i("vivian", "provinceClickListener onItemClick index = "
					+ index + ";arg3 = " + arg3);
			selectedProvince = provinceList.get(index);
			Log.i("vivian", "provinceClickListener onItemClick selectedProvince.getProvinceCode() = "
					+ selectedProvince.getProvinceCode());
			queryCities(selectedProvince.getProvinceCode());
		}

	};

	private OnItemClickListener cityClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int index,
				long arg3) {
			Log.i("vivian", "cityClickListener onItemClick index = " + index
					+ ";arg3 = " + arg3);
			selectedCity = cityList.get(index);
			Log.i("vivian", "cityClickListener onItemClick add city = "
					+ selectedCity.getCityName());
			
			Intent intent = new Intent();
			intent.putExtra("newCity", selectedCity.getCityName());
			setResult(RESULT_OK, intent);
			finish();
		}

	};

	private void queryProvinces() {
		// get the province city data from DB first, cannot get, go to server
		provinceList = CoolWeatherDB.getInstance(this).getProvinces();
		if (!Utility.isListEmpty(provinceList)) {
			provinceNames.clear();
			for (Province province : provinceList) {
				provinceNames.add(province.getProvinceName());
			}
			provinceAdapter.notifyDataSetChanged();
			provinceListView.setSelection(0);
		} else {
			queryFromServer(null, QUERY_PROVINCE);
		}
	}

	private void queryCities(String provinceCode) {
		// get the  city data from DB first, cannot get, go to server
		cityList = CoolWeatherDB.getInstance(this).getCities(provinceCode);
		if (!Utility.isListEmpty(cityList)) {
			cityNames.clear();
			for (City city : cityList) {
				cityNames.add(city.getCityName());
			}
			cityAdapter.notifyDataSetChanged();
			cityListView.setSelection(0);
		} else {
			queryFromServer(provinceCode, QUERY_CITY);
		}
	}
	
	private final static int QUERY_PROVINCE = 2;
	private final static int QUERY_CITY = 3;
	private void queryFromServer(final String code, final int type){
		String url; 
		if (Utility.isStringEmpty(code)){
			url = "http://www.weather.com.cn/data/list3/city.xml";
		}else{
			url = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}
		
		showProgressDialog();
		Utility.sendRequestToServer(url, type, handler);
		
	}
	
	private final Handler handler= new Handler(){		
		@Override
		public void handleMessage(Message msg){
			Log.i("vivian", "ProvinceCityActivity handleMessage");
			closeProgressDialog();
			String data = (String)msg.obj;
			switch(msg.what){
				case QUERY_PROVINCE:					
					provinceList  = Utility.getProvinces(data);
					CoolWeatherDB.getInstance(ProvinceCityActivity.this).saveProvinces(provinceList);
					queryProvinces();
					break;
				case QUERY_CITY:
					cityList  = Utility.getCities(data, selectedProvince.getProvinceCode());
					CoolWeatherDB.getInstance(ProvinceCityActivity.this).saveCities(cityList);
					queryCities(selectedProvince.getProvinceCode());
					break;
				default:
					break;
			}
			
		}
	};
	
	private void showProgressDialog(){
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("loading...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
	}
	private void closeProgressDialog(){
		if (progressDialog != null){
			progressDialog.dismiss();
		}
	}
}
