import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import models.ReportingException;

public class Main {
	public static void main(String[] args) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable r = () -> {
			WeatherReportingService weatherService = new WeatherReportingService();
			try {
				System.out.println("Starting weather report generation...");
				weatherService.reportWeather();
				System.out.println("\nWeather report generation completed.");
			} catch (ClassNotFoundException | IOException | InterruptedException | ReportingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("exception: " + e);
				e.printStackTrace();
			}
		};

		scheduler.scheduleAtFixedRate(r, 5, 60 * 60 * 24 * 7, TimeUnit.SECONDS);
	}
}