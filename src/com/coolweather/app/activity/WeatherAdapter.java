package com.coolweather.app.activity;

import java.util.List;

import com.coolweather.R;
import com.coolweather.app.model.WeatherInfo;
import com.coolweather.app.util.Logger;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WeatherAdapter extends ArrayAdapter<WeatherInfo>{
	
	private Logger logger = Logger.getLogger(this.getClass());
	private int resourceId;
	
	public WeatherAdapter(Context context, int resource,
			List<WeatherInfo> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		
		logger.d( "WeatherAdapter position=" + position);
		if (convertView != null){
			logger.d( "convertView.toString()=" + convertView.toString() + ";convertView.hashCode()=" + convertView.hashCode());
		}
		if (parent != null){
			logger.d( "parent.toString()=" + parent.toString() + ";parent.hashCode()=" + parent.hashCode());
		}
		WeatherInfo info = getItem(position);
		
		View view;
		ViewHolder viewHolder;
		
		if (convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.date =  (TextView)view.findViewById(R.id.date);
			viewHolder.day_weather = (TextView)view.findViewById(R.id.day_weather);
			viewHolder.night_weather = (TextView)view.findViewById(R.id.nigth_weather);
			viewHolder.temp_range = (TextView)view.findViewById(R.id.temp_range);
			viewHolder.wind = (TextView)view.findViewById(R.id.wind);
			viewHolder.wind_scale = (TextView)view.findViewById(R.id.wind_scale);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder)view.getTag();
		}

		viewHolder.date.setText(info.getDate());		
		viewHolder.day_weather.setText(info.getDay_weather());		
		viewHolder.night_weather.setText(info.getNight_weather());		
		viewHolder.temp_range.setText(info.getLow_tmp() + " ~ " + info.getHigh_tmp() + " ¡æ");
		viewHolder.wind.setText(info.getWind());
		viewHolder.wind_scale.setText(info.getWind_scal());
		
		return view;
		
	}

	private class ViewHolder{
		TextView date;
		TextView day_weather;
		TextView night_weather;
		TextView temp_range;
		TextView wind;
		TextView wind_scale;
		
	}
	
	
}
