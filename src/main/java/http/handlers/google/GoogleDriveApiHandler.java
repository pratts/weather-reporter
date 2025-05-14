package http.handlers.google;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.AppConfig;
import http.BaseHttpClient;
import http.handlers.IDriveApiHandler;
import models.ReportingException;

public class GoogleDriveApiHandler implements IDriveApiHandler {
	private GoogleAuthHandler authHandler;
	private String token;

	public GoogleDriveApiHandler() throws Exception {
		this.authHandler = new GoogleAuthHandler();
	}

	@Override
	public String uploadExcel(String[][] content, String folderId)
			throws IOException, InterruptedException, ReportingException {
		if (this.token == null) {
			this.updateToken();
		}

		if (this.token == null) {
			throw new ReportingException(500, "Token is not available");
		}

		int retry = 2;
		while (retry >= 0) {
			try {
				String excelFileName = this.createExcel(folderId);
				this.populateExcelData(excelFileName, content);
				return excelFileName;
			} catch (ReportingException e) {
				e.printStackTrace();
				if (e.getCode() == 401) {
					this.updateToken();
				} else {
					throw e;
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
			retry--;
		}
		return null;
	}

	private String getCurrentTime() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss");
		return now.format(formatter);
	}

	private String createExcel(String folderId) throws IOException, InterruptedException, ReportingException {
		String createUrl = "https://www.googleapis.com/drive/v3/files";
		Map<String, String> headers = Map.of("Authorization", "Bearer " + this.token, "Content-Type",
				"application/json");

		String body = String.format("""
				{
				    "name": "Weather reporting data | %s",
				    "mimeType": "application/vnd.google-apps.spreadsheet",
				    "parents": ["%s"]
				  }""", this.getCurrentTime(), folderId);
		String response = BaseHttpClient.getInstance().post(createUrl, headers, body);
		String spreadsheetId = getIdFromResponse(response);
		return spreadsheetId;
	}

	private void populateExcelData(String fileId, String[][] content)
			throws IOException, InterruptedException, ReportingException {
		String appendUrl = String.format(
				"https://sheets.googleapis.com/v4/spreadsheets/%s/values/Sheet1!A1:append?valueInputOption=USER_ENTERED",
				fileId);
		Map<String, String> headers = Map.of("Authorization", "Bearer " + this.token, "Content-Type",
				"application/json");
		StringBuilder valueJson = new StringBuilder();
		valueJson.append("{ \"values\": [");

		for (int i = 0; i < content.length; i++) {
			String[] row = content[i];
			valueJson.append("[");
			for (int j = 0; j < row.length; j++) {
				String cell = row[j].replace("\"", "\\\"");
				valueJson.append("\"").append(cell).append("\"");
				if (j < row.length - 1)
					valueJson.append(",");
			}
			valueJson.append("]");
			if (i < content.length - 1)
				valueJson.append(",");
		}
		valueJson.append("] }");
		String payload = valueJson.toString();
		BaseHttpClient.getInstance().post(appendUrl, headers, payload);
	}

	public void updateToken() {
		if (this.token == null) {
			try {
				this.token = this.authHandler.getAccessToken();
			} catch (Exception | ReportingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void shareFile(String fileId) throws IOException, InterruptedException, ReportingException {
		Map<String, String> headers = Map.of("Authorization", "Bearer " + this.token, "accept", "application/json");

		String permissionUrl = String.format("https://www.googleapis.com/drive/v3/files/%s/permissions", fileId);

		for (String email : AppConfig.EMAIL_IDS) {
			String body = String.format("{\"role\": \"writer\", \"type\": \"user\", \"emailAddress\": \"%s\"}", email);
			BaseHttpClient.getInstance().post(permissionUrl, headers, body);
		}
	}

	@Override
	public String shareFileWithEmailNotification(String fileId)
			throws IOException, InterruptedException, ReportingException {
		Map<String, String> headers = Map.of("Authorization", "Bearer " + this.token, "accept", "application/json");
		String message = String.format(
				"Here is the public link of the file: https://docs.google.com/spreadsheets/d/%s/edit?usp=sharing",
				fileId);
		String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

		String permissionUrl = String.format("https://www.googleapis.com/drive/v3/files/%s/permissions?emailMessage=%s",
				fileId, encodedMessage);

		for (String email : AppConfig.EMAIL_IDS) {
			String body = String.format("{\"role\": \"writer\", \"type\": \"user\", \"emailAddress\": \"%s\"}", email);
			BaseHttpClient.getInstance().post(permissionUrl, headers, body);
		}
		return String.format("https://docs.google.com/spreadsheets/d/%s/edit?usp=sharing", fileId);
	}

	private String getFolder() throws IOException, InterruptedException, ReportingException {
		Map<String, String> headers = Map.of("Authorization", "Bearer " + this.token, "accept", "application/json");
		String query = String.format("name='%s' and mimeType='application/vnd.google-apps.folder' and trashed=false",
				AppConfig.DRIVE_FOLDER_NAME);
		String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
		String listUrl = "https://www.googleapis.com/drive/v3/files?q=" + encodedQuery;

		String listResponse = BaseHttpClient.getInstance().get(listUrl, headers);

		if (listResponse.contains("\"id\":")) {
			return this.getIdFromResponse(listResponse);
		}
		return null;
	}

	private String createFolder() throws IOException, InterruptedException, ReportingException {
		// 2. Create folder
		String createUrl = "https://www.googleapis.com/drive/v3/files";
		Map<String, String> headers = Map.of("Authorization", "Bearer " + this.token, "Content-Type",
				"application/json");

		String body = String.format("{\"name\": \"%s\", \"mimeType\": \"application/vnd.google-apps.folder\"}",
				AppConfig.DRIVE_FOLDER_NAME);

		String createResponse = BaseHttpClient.getInstance().post(createUrl, headers, body);
		return this.getIdFromResponse(createResponse);
	}

	public String checkAndCreateFolder() throws IOException, InterruptedException, ReportingException {
		String folderId = this.getFolder();

		if (folderId == null) {
			folderId = this.createFolder();
		}

		if (folderId == null) {
			throw new ReportingException(500, "Unable to create folder");
		}

		return folderId;
	}

	public void deleteFolder(String folderId) throws IOException, InterruptedException, ReportingException {
		String deleteUrl = String.format("https://www.googleapis.com/drive/v3/files/%s", folderId);
		Map<String, String> headers = Map.of("Authorization", "Bearer " + this.token, "Content-Type",
				"application/json");

		BaseHttpClient.getInstance().delete(deleteUrl, headers);
	}

	private String getIdFromResponse(String res) {
		Pattern p = Pattern.compile("\"id\":\\s*\"(.*?)\"");
		Matcher m = p.matcher(res);
		if (m.find()) {
			return m.group(1); // Return existing folder ID
		}
		return null;
	}
}
