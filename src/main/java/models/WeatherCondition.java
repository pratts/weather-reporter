package models;

public class WeatherCondition {
	public String weatherText;
	public boolean isDayTime;
	public double temparatureC;
	public double temparatureF;
	public long observationTime;

	public String getWeatherText() {
		return weatherText;
	}

	public boolean isDayTime() {
		return isDayTime;
	}

	public double getTemparatureC() {
		return temparatureC;
	}

	public double getTemparatureF() {
		return temparatureF;
	}

	public long getObservationTime() {
		return observationTime;
	}

	public String toString() {
		return this.weatherText;
	}
}