package http.handlers;

import java.io.IOException;

import config.AppConfig;
import http.BaseHttpClient;
import models.ReportingException;
import models.WeatherCondition;

public class WeatherConditionApiHandler {
	public WeatherCondition getWeatherConditionForCity(String cityKey)
			throws IOException, InterruptedException, ReportingException, ClassNotFoundException {
		String url = AppConfig.WEATHER_API_URL + cityKey + "?apikey=" + AppConfig.API_KEY;

		String locationsResponse = BaseHttpClient.getInstance().get(url);
		return this.parseWeatherCondition(locationsResponse);
	}

	private WeatherCondition parseWeatherCondition(String json) {
		String[] items = json.split("\\},\\{");
		WeatherCondition w = new WeatherCondition();
		for (String item : items) {
			w.weatherText = extract(item, "\"WeatherText\":\"", "\"");

			// No quotes around booleans or numbers
			w.isDayTime = extract(item, "\"IsDayTime\":", ",").trim().equals("true");

			w.observationTime = Long.parseLong(extract(item, "\"EpochTime\":", ",").trim());

			// Extract the Temperature object
			String tempBlock = extract(item, "\"Temperature\":{", "},\"MobileLink\"");

			// Extract Celsius (Metric)
			String metricBlock = extract(tempBlock, "\"Metric\":{", "},\"Imperial\"");
			String celsius = extract(metricBlock, "\"Value\":", ",");
			w.temparatureC = Double.parseDouble(celsius);

			// Extract Fahrenheit (Imperial)
			String imperialBlock = extract(tempBlock, "\"Imperial\":{", "}");
			String fahrenheit = extract(imperialBlock, "\"Value\":", ",");
			w.temparatureF = Double.parseDouble(fahrenheit);
		}
		return w;
	}

	private static String extract(String src, String prefix, String endChar) {
		int start = src.indexOf(prefix);
		if (start == -1)
			return "";
		start += prefix.length();
		int end = src.indexOf(endChar, start);
		if (end == -1)
			return "";
		return src.substring(start, end);
	}
}
