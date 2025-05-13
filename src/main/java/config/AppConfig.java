package config;

public class AppConfig {
	public static String LOCATION_API_URL = "http://dataservice.accuweather.com/locations/v1/topcities/50";
	public static String WEATHER_API_URL = "http://dataservice.accuweather.com/currentconditions/v1/";
	public static String API_KEY = System.getenv("API_KEY");

	public static String DRIVE_FOLDER_NAME = "Iion-Weather-Reports";
	public static String DRIVE_API_URL = "https://www.googleapis.com/drive/v3/drives";

	public static String[] EMAIL_IDS = System.getenv("EMAIL_IDS").split(",");

	static {

	}
}
