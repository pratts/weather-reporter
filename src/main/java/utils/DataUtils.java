package utils;

import java.util.Date;
import java.util.List;

import models.Location;

public class DataUtils {
	public static String[][] locationDataToCsvRows(List<Location> locations) {
		String[][] csvRows = new String[locations.size() + 1][12];
		csvRows[0] = "Name,Country,Region,Timezone,Rank,Latitude,Longitude,Weather Text,Is Day Time,Temperature Celsius (C),Temperature Fahrenheit (F),Last Updated At"
				.split(",");
		for (int i = 0; i < locations.size(); i++) {
			csvRows[i + 1][0] = locations.get(i).getName();
			csvRows[i + 1][1] = locations.get(i).getCountry();
			csvRows[i + 1][2] = locations.get(i).getRegion();
			csvRows[i + 1][3] = locations.get(i).getTimezone();
			csvRows[i + 1][4] = String.valueOf(locations.get(i).getRank());
			csvRows[i + 1][5] = String.valueOf(locations.get(i).getLatitude());
			csvRows[i + 1][6] = String.valueOf(locations.get(i).getLongitude());
			csvRows[i + 1][7] = String.valueOf(locations.get(i).getWeatherCondition().getWeatherText());
			csvRows[i + 1][8] = String.valueOf(locations.get(i).getWeatherCondition().isDayTime());
			csvRows[i + 1][9] = String.valueOf(locations.get(i).getWeatherCondition().getTemparatureC());
			csvRows[i + 1][10] = String.valueOf(locations.get(i).getWeatherCondition().getTemparatureF());

			Date obDate = new Date();
			obDate.setTime(locations.get(i).getWeatherCondition().getObservationTime() * 1000);
			String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(obDate);
			csvRows[i + 1][11] = String.valueOf(date);
		}
		return csvRows;
	}
}
