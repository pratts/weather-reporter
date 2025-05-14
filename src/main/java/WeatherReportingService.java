import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import http.handlers.IDriveApiHandler;
import http.handlers.LocationApiHandler;
import http.handlers.WeatherConditionApiHandler;
import http.handlers.google.GoogleDriveApiHandler;
import models.Location;
import models.ReportingException;
import models.WeatherCondition;
import utils.DataUtils;

public class WeatherReportingService {
	private LocationApiHandler locationApi;
	private WeatherConditionApiHandler weatherApi;
	private IDriveApiHandler driveApiHandler;
	ExecutorService executorService = Executors.newFixedThreadPool(2);

	public WeatherReportingService() {
		this.locationApi = new LocationApiHandler();
		this.weatherApi = new WeatherConditionApiHandler();
		try {
			this.driveApiHandler = this.getDriveHandler();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reportWeather() throws ClassNotFoundException, IOException, InterruptedException, ReportingException {
		if (this.driveApiHandler == null) {
			throw new ReportingException(500, "Driver APIs not available");
		}

		System.out.println("\nFetching location and weather conditions...");
		List<Location> locations = this.getLocationAndWeatherConditions();
		System.out.println("Fetched " + locations.size() + " locations and their weather conditions.");

		System.out.println("\nCreating and sharing weather report...");
		String[][] csv = DataUtils.locationDataToCsvRows(locations);

		System.out.println("\nGenerating excel file...");
		this.createAndShareWeatherReport(csv);
		System.out.println("Excel file generated and shared successfully.");

	}

	private void createAndShareWeatherReport(String[][] data)
			throws IOException, InterruptedException, ReportingException {
		this.driveApiHandler.updateToken();
		String folderId = this.driveApiHandler.checkAndCreateFolder();
		this.driveApiHandler.shareFile(folderId);
		String excelFileId = this.driveApiHandler.uploadExcel(data, folderId);
		if (excelFileId == null) {
			throw new ReportingException(500, "Error while creating excel on drive");
		}

		this.driveApiHandler.shareFileWithEmailNotification(excelFileId);
	}

	private List<Location> getLocationAndWeatherConditions()
			throws ClassNotFoundException, IOException, InterruptedException, ReportingException {
		List<Location> locations = this.locationApi.getTop50Locations();

		Map<String, WeatherCondition> cityWeatherMap = getWeatherConditionForCities(
				locations.stream().map(l -> l.key).toArray(String[]::new));

		locations.forEach((l) -> {
			l.weatherCondition = cityWeatherMap.get(l.key);
		});
		return locations;
	}

	private Map<String, WeatherCondition> getWeatherConditionForCities(String[] cityKeys) {
		Map<String, WeatherCondition> cityWeatherMap = new HashMap<String, WeatherCondition>();

		List<CompletableFuture<WeatherCondition>> futures = new ArrayList<CompletableFuture<WeatherCondition>>();
		for (String key : cityKeys) {
			CompletableFuture<WeatherCondition> future = CompletableFuture.supplyAsync(() -> {
				try {
					WeatherCondition w = this.weatherApi.getWeatherConditionForCity(key);
					cityWeatherMap.put(key, w);
					return w;
				} catch (Exception | ReportingException e) {
					return null;
				}
			}, executorService);
			futures.add(future);
		}
		CompletableFuture<Void> allDone = CompletableFuture
				.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		allDone.join();

		return cityWeatherMap;
	}

	private IDriveApiHandler getDriveHandler() throws Exception {
		return new GoogleDriveApiHandler();
	}
}
