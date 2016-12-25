package com.coolweather.app.model;

public class WeatherInfo {
	private String cityName = "Bei Jing";
	private String cityId = "0101";
	private String high_tmp = "0";
	private String low_tmp = "0";	
	private String current_tmp = "0";	
	private String publishTime = "00:00";
	private String weatherId = "101010100";
	private String date = "";
	private String day_weather = "Sunny";
	private String night_weather = "Sunny";
	private String current_weather = "Sunny";
	
	private String wind = "";
	private String wind_scal = "";

	public String getCurrent_weather() {
		return current_weather;
	}
	public void setCurrent_weather(String current_weather) {
		this.current_weather = current_weather;
	}
	public String getCurrent_tmp() {
		return current_tmp;
	}
	public void setCurrent_tmp(String current_tmp) {
		this.current_tmp = current_tmp;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getHigh_tmp() {
		return high_tmp;
	}
	public void setHigh_tmp(String high_tmp) {
		this.high_tmp = high_tmp;
	}
	public String getLow_tmp() {
		return low_tmp;
	}
	public void setLow_tmp(String low_tmp) {
		this.low_tmp = low_tmp;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public String getWeatherId() {
		return weatherId;
	}
	public void setWeatherId(String weatherId) {
		this.weatherId = weatherId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDay_weather() {
		return day_weather;
	}
	public void setDay_weather(String day_weather) {
		this.day_weather = day_weather;
	}
	public String getNight_weather() {
		return night_weather;
	}
	public void setNight_weather(String night_weather) {
		this.night_weather = night_weather;
	}
	public String getWind() {
		return wind;
	}
	public void setWind(String wind) {
		this.wind = wind;
	}
	public String getWind_scal() {
		return wind_scal;
	}
	public void setWind_scal(String wind_scal) {
		this.wind_scal = wind_scal;
	}

	
	
	
}
