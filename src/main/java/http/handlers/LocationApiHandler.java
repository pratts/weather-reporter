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
		List<Location> result = new ArrayList<>();

		// Remove surrounding array brackets
		json = json.trim();
		if (json.startsWith("["))
			json = json.substring(1);
		if (json.endsWith("]"))
			json = json.substring(0, json.length() - 1);

		// Since you are only getting 1 object or many objects separated by "},{" —
		// split accordingly
		String[] items = json.split("(?<=\\}),\\s*(?=\\{)");

		for (String item : items) {
			Location loc = new Location();

			loc.key = extract(item, "\"Key\":\"", "\"");
			if (loc.key.isEmpty())
				loc.key = extract(item, "\"Key\":", ",");

			loc.name = extract(item, "\"EnglishName\":\"", "\"");

			loc.country = extractNestedField(item, "\"Country\"", "\"EnglishName\":\"", "\"");
			loc.region = extractNestedField(item, "\"Region\"", "\"EnglishName\":\"", "\"");
			loc.timezone = extractNestedField(item, "\"TimeZone\"", "\"Name\":\"", "\"");

			try {
				loc.rank = Integer.parseInt(extract(item, "\"Rank\":", ",").trim());
			} catch (Exception ignored) {
			}

			String geoPosition = extractBlock(item, "\"GeoPosition\"");
			if (!geoPosition.isEmpty()) {
				try {
					loc.latitude = Double.parseDouble(extract(geoPosition, "\"Latitude\":", ",").trim());
					loc.longitude = Double.parseDouble(extract(geoPosition, "\"Longitude\":", ",").trim());
				} catch (Exception ignored) {
				}
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

	private static String extractBlock(String src, String blockKey) {
		int start = src.indexOf(blockKey);
		if (start == -1)
			return "";
		int braceStart = src.indexOf("{", start);
		if (braceStart == -1)
			return "";
		int depth = 0;
		for (int i = braceStart; i < src.length(); i++) {
			if (src.charAt(i) == '{')
				depth++;
			else if (src.charAt(i) == '}')
				depth--;
			if (depth == 0)
				return src.substring(braceStart, i + 1);
		}
		return "";
	}

	private static String extractNestedField(String src, String blockKey, String nestedKeyPrefix, String endChar) {
		String block = extractBlock(src, blockKey);
		if (block.isEmpty())
			return "";
		return extract(block, nestedKeyPrefix, endChar);
	}

}
