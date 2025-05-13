package http.handlers;

import java.io.IOException;

import models.ReportingException;

public interface IDriveApiHandler {
	public String uploadExcel(String[][] content) throws IOException, InterruptedException, ReportingException;

	public void updatePermissions(String fileName) throws IOException, InterruptedException, ReportingException;

	public String generatePublicLink(String fileName) throws IOException, InterruptedException, ReportingException;

	public boolean sendExcelInEmail(String fileName);
}
