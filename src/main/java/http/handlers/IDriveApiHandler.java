package http.handlers;

import java.io.IOException;

import models.ReportingException;

public interface IDriveApiHandler {
	public String uploadExcel(String[][] content, String folderId)
			throws IOException, InterruptedException, ReportingException;

	public void shareFile(String fileName) throws IOException, InterruptedException, ReportingException;

	public String shareFileWithEmailNotification(String fileName)
			throws IOException, InterruptedException, ReportingException;

	public String checkAndCreateSharedFolder() throws IOException, InterruptedException, ReportingException;

	public void updateToken();
}
