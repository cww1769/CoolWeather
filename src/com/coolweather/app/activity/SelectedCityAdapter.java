package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.R;
import com.coolweather.app.util.Logger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SelectedCityAdapter extends ArrayAdapter<String> {

	private int resourceId;
	private SelectedCityActivity mContext;
	private Logger logger = Logger.getLogger(this.getClass());

	public SelectedCityAdapter(Context context, int resource,
			List<String> objects) {
		super(context, resource, objects);
		mContext = (SelectedCityActivity)context;
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		logger.d( "position=" + position);
		if (convertView != null){
			logger.d( "convertView.toString()=" + convertView.toString() + ";convertView.hashCode()=" + convertView.hashCode());
		}
		if (parent != null){
			logger.d( "parent.toString()=" + parent.toString() + ";parent.hashCode()=" + parent.hashCode());
		}
		String cityName = getItem(position);	

		View view;
		ViewHolder viewHolder;

		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.cityNameView = (TextView) view.findViewById(R.id.selected_city_name);
			viewHolder.removeCity = (Button) view.findViewById(R.id.remove);
			viewHolder.addCity = (Button) view.findViewById(R.id.add);
			viewHolder.up = (Button) view.findViewById(R.id.up);
			viewHolder.down = (Button) view.findViewById(R.id.down);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder)view.getTag();
		}

		
		viewHolder.cityNameView.setText(cityName);
		viewHolder.cityNameView.setOnClickListener(selectCityListener);
		viewHolder.cityNameView.setTag(position);
		viewHolder.addCity.setOnClickListener(addCityListener);		
		if (position == 0){
			viewHolder.removeCity.setVisibility(View.INVISIBLE);	
			viewHolder.up.setVisibility(View.INVISIBLE);	
			viewHolder.down.setVisibility(View.INVISIBLE);	
		}else{
			viewHolder.removeCity.setOnClickListener(removeCityListener);			
			viewHolder.up.setOnClickListener(upCityListener);
			viewHolder.down.setOnClickListener(downCityListener);
			viewHolder.removeCity.setTag(position);
			viewHolder.up.setTag(position);
			viewHolder.down.setTag(position);
		}
		logger.d( "view.toString()=" + view.toString() + ";view.hashCode()=" + view.hashCode());
		return view;
	}
	
	private class ViewHolder{
		TextView cityNameView;
		Button addCity;
		Button removeCity;
		Button up;
		Button down;
	}

	private OnClickListener selectCityListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			int listpos = (int)view.getTag();
			logger.d( "select a city with position = " + listpos);		
			Intent intent = new Intent(mContext, WeatherActivity.class);
			intent.putExtra("selectedCity", mContext.getCityList().get(listpos));
			mContext.startActivity(intent);		
		}

	};
	private OnClickListener addCityListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			logger.i( "add a city");
			Intent intent = new Intent(mContext, ProvinceCityActivity.class);
			mContext.startActivityForResult(intent, 2);
			
		}

	};

	private OnClickListener removeCityListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Button button = (Button)view;
			int listpos = (int)button.getTag();
			logger.i( "remove a city with position = " + listpos);
			List<String> cityList = mContext.getCityList();
			cityList.remove(listpos);
			notifyDataSetChanged();
		}

	};

	private OnClickListener upCityListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			int listpos = (int)view.getTag();
			logger.i( "up a city with position = " + listpos);
			List<String> cityList = mContext.getCityList();
			if (listpos > 1){
				String tempCity = cityList.get(listpos);
				cityList.add(listpos - 1, tempCity);
				cityList.remove(listpos + 1);
				notifyDataSetChanged();
			}
		}
	};

	private OnClickListener downCityListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			int listpos = (int)view.getTag();
			logger.i( "down a city with position = " + listpos);		
			List<String> cityList = mContext.getCityList();
			if (listpos < cityList.size()-1){
				String tempCity = cityList.get(listpos);
				cityList.remove(listpos);
				cityList.add(listpos + 1, tempCity);
				notifyDataSetChanged();
			}

		}

	};


}
