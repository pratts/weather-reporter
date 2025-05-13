package http.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	private List<Location> parseLocations(String json) {
		List<Location> result = new ArrayList<Location>();

		// Split by curly brace blocks (naively)
		String[] items = json.split("\\},\\{");

		for (String item : items) {
			Location loc = new Location();

			loc.key = extract(item, "\"Key\":\"", "\"");
			loc.name = extract(item, "\"EnglishName\":\"", "\"");
			loc.country = extract(item, "\"Country\":\\{[^}]*?\"EnglishName\":\"", "\"");
			loc.region = extract(item, "\"Region\":\\{[^}]*?\"EnglishName\":\"", "\"");
			loc.timezone = extract(item, "\"TimeZone\":\\{[^}]*?\"Name\":\"", "\"");

			try {
				loc.rank = Integer.parseInt(extract(item, "\"Rank\":", ","));
				loc.latitude = Double.parseDouble(extract(item, "\"Latitude\":", ","));
				loc.longitude = Double.parseDouble(extract(item, "\"Longitude\":", ","));
			} catch (Exception ignored) {
			}

			result.add(loc);
		}

		return result;
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
