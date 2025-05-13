import java.io.IOException;

import models.ReportingException;

public class Main {
	public static void main(String[] args) {
		WeatherReportingService weatherService = new WeatherReportingService();
		try {
			weatherService.getAndReportWeather();
		} catch (ClassNotFoundException | IOException | InterruptedException | ReportingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}