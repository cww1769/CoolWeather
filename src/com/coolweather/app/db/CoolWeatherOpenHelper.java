package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper{
	
	/*create the weatherinfo table
	 * 
	 */
	
	public static final String CREATE_WEATHERINFO = "create table weatherInfo (id integer primary key autoincrement,"
			+ "cityName text, " + "high_tmp text, " + "low_tmp text, " + "current_tmp text, " + "publishTime text, " 
			+ "date text, " + "day_weather text, " + "night_weather text, " + "current_weather text)";
	
	public static final String CREATE_SAVEDCITY = "create table savedCity (id integer primary key autoincrement,"
			+ "cityName text, " + "priority integer)";

	public static final String CREATE_PROVINCE = "create table province (id integer primary key autoincrement,"
			+ "provinceCode text,"	+ "provinceName text)";
	
	public static final String CREATE_CITY = "create table city (id integer primary key autoincrement, "
			+ "cityCode text,"	+ "cityName text, "  + "provinceCode text)";
	
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("vivian", "CoolWeatherOpenHelper on Create");
		db.execSQL(CREATE_WEATHERINFO);
		db.execSQL(CREATE_SAVEDCITY);
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.i("vivian", "CoolWeatherOpenHelper on onUpgrade. oldVersion = " + oldVersion + "; newVersion = " + newVersion);
		switch(oldVersion){
		case 1:
			db.execSQL(CREATE_PROVINCE);
			db.execSQL(CREATE_CITY);
		default:
		
		}
	}
	

}
