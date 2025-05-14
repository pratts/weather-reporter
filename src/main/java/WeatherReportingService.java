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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getAndReportWeather()
			throws ClassNotFoundException, IOException, InterruptedException, ReportingException {
		if (this.driveApiHandler == null) {
			throw new ReportingException(500, "Driver APIs not available");
		}

		List<Location> locations = this.locationApi.getTop50Locations();

		Map<String, WeatherCondition> cityWeatherMap = getWeatherConditionForCities(
				locations.stream().map(l -> l.key).toArray(String[]::new));

		locations.forEach((l) -> {
			l.weatherCondition = cityWeatherMap.get(l.key);
		});

		String[][] csv = DataUtils.locationDataToCsvRows(locations);

		String folderId = this.driveApiHandler.checkAndCreateSharedFolder();
		String excelFileId = this.driveApiHandler.uploadExcel(csv, folderId);
		if (excelFileId == null) {
			throw new ReportingException(500, "Error while creating excel on drive");
		}

		this.driveApiHandler.generatePublicLink(excelFileId);
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
