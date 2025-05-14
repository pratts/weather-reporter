package http.handlers;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

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
		WeatherCondition w = new WeatherCondition();
		JSONArray jsonArray = new JSONArray(json);
		JSONObject obj = jsonArray.getJSONObject(0);
		w.weatherText = obj.getString("WeatherText");
		w.isDayTime = obj.getBoolean("IsDayTime");
		w.observationTime = obj.getLong("EpochTime");
		w.temparatureC = obj.getJSONObject("Temperature").getJSONObject("Metric").getDouble("Value");
		w.temparatureF = obj.getJSONObject("Temperature").getJSONObject("Imperial").getDouble("Value");
		return w;
	}
}
