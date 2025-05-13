package models;

public class Location {
	public String key;
	public String name;
	public String country;
	public String region;
	public String timezone;
	public int rank;
	public double latitude;
	public double longitude;
	public WeatherCondition weatherCondition;

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getCountry() {
		return country;
	}

	public String getRegion() {
		return region;
	}

	public String getTimezone() {
		return timezone;
	}

	public int getRank() {
		return rank;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public WeatherCondition getWeatherCondition() {
		return weatherCondition;
	}
}
