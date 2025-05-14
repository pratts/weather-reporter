package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import models.ReportingException;

public class BaseHttpClient {
	private HttpClient client = HttpClient.newHttpClient();;
	private static final BaseHttpClient instance = new BaseHttpClient(); // singleton

	private BaseHttpClient() {
		// private constructor to prevent instantiation
	}

	public static BaseHttpClient getInstance() {
		return instance;
	}

	public String get(String url) throws IOException, InterruptedException, ReportingException {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
		HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new ReportingException(response.statusCode(), response.toString());
		}

		return response.body();
	}

	public String get(String url, Map<String, String> headers)
			throws IOException, InterruptedException, ReportingException {
		Builder builder = HttpRequest.newBuilder();
		builder.uri(URI.create(url));
		builder.GET();

		for (Map.Entry<String, String> it : headers.entrySet()) {
			builder.header(it.getKey(), it.getValue());
		}
		HttpRequest request = builder.build();
		HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new ReportingException(response.statusCode(), response.toString());
		}

		return response.body();
	}

	public String post(String url, Map<String, String> headers, String body)
			throws IOException, InterruptedException, ReportingException {
		HttpRequest.Builder builder = HttpRequest.newBuilder();

		for (Map.Entry<String, String> it : headers.entrySet()) {
			builder.header(it.getKey(), it.getValue());
		}
		builder.uri(URI.create(url));
		builder.POST(HttpRequest.BodyPublishers.ofString(body));
		HttpRequest request = builder.build();

		HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new ReportingException(response.statusCode(), response.body());
		}
		return response.body();
	}

	public int delete(String url, Map<String, String> headers)
			throws IOException, InterruptedException, ReportingException {
		HttpRequest.Builder builder = HttpRequest.newBuilder();

		for (Map.Entry<String, String> it : headers.entrySet()) {
			builder.header(it.getKey(), it.getValue());
		}
		builder.uri(URI.create(url));
		builder.DELETE();
		HttpRequest request = builder.build();

		HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.statusCode();
	}
}
