package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class SelectedCityActivity extends Activity {
	private Button home = null;
	private List<String> cityList = null;
	SelectedCityAdapter adapter = null;
	
	public List<String> getCityList() {
		return cityList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("vivian", "SelectedCityActivity onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.city_list);

		home = (Button) findViewById(R.id.home);
		home.setOnClickListener(homeListener);

		// get cityList from WeatherActivity
		Intent intent = getIntent();
		cityList = intent.getStringArrayListExtra("cityList");

		adapter = new SelectedCityAdapter(this,
				R.layout.selected_city_item_layout, cityList);
		ListView listView = (ListView) findViewById(R.id.selected_city_list_view);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		Log.i("vivian", "SelectedCityActivity onResume");
		super.onResume();
		// TODO: move to oncreate
	}

	@Override
	protected void onPause() {
		Log.i("vivian", "SelectedCityActivity onPause begin");		
		if (!Utility.isListEmpty(cityList)) {
			CoolWeatherDB.getInstance(this).deleteCites();
			CoolWeatherDB.getInstance(this).saveSelectedCities(cityList);
		}
		super.onPause();
		Log.i("vivian", "SelectedCityActivity onPause end");
	}

	@Override
	protected void onStop() {
		Log.i("vivian", "SelectedCityActivity onStop begin");		
		super.onStop();
		Log.i("vivian", "SelectedCityActivity onStop end");
	}
	
	@Override
	protected void onDestroy() {
		Log.i("vivian", "SelectedCityActivity onDestroy begin");
		super.onDestroy();
		Log.i("vivian", "SelectedCityActivity onDestroy end");
	}

	private OnClickListener homeListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent = new Intent(SelectedCityActivity.this,
					WeatherActivity.class);
			startActivity(intent);
		}

	};
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("vivian", "SelectedCityActivity onActivityResult requestCode="
				+ requestCode + "; resultCode=" + resultCode);
		if (requestCode == 2){
			String newCity = data.getStringExtra("newCity");
			cityList.add(newCity);
			adapter.notifyDataSetChanged();
		}

	}

}
