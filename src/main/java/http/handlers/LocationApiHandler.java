package http.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import config.AppConfig;
import http.BaseHttpClient;
import models.Location;
import models.ReportingException;

public class LocationApiHandler {

	public List<Location> getTop50Locations()
			throws IOException, InterruptedException, ReportingException, ClassNotFoundException {
		String url = AppConfig.LOCATION_API_URL + "?apikey=" + AppConfig.API_KEY;

		String locationsResponse = BaseHttpClient.getInstance().get(url);
		return this.parseLocations(locationsResponse);
	}

	/**
	 * @param json
	 * @return
	 */
	private List<Location> parseLocations(String json) {
		JSONArray locations = new JSONArray(json);
		List<Location> result = new ArrayList<>();
		locations.forEach(((l) -> {
			Location loc = new Location();
			loc.key = ((org.json.JSONObject) l).getString("Key");
			loc.name = ((org.json.JSONObject) l).getString("EnglishName");
			loc.country = ((org.json.JSONObject) l).getJSONObject("Country").getString("EnglishName");
			loc.region = ((org.json.JSONObject) l).getJSONObject("Region").getString("EnglishName");
			loc.timezone = ((org.json.JSONObject) l).getJSONObject("TimeZone").getString("Name");
			loc.latitude = ((org.json.JSONObject) l).getJSONObject("GeoPosition").getDouble("Latitude");
			loc.longitude = ((org.json.JSONObject) l).getJSONObject("GeoPosition").getDouble("Longitude");
			loc.rank = ((org.json.JSONObject) l).getInt("Rank");
			result.add(loc);
		}));
		return result;
	}
}
