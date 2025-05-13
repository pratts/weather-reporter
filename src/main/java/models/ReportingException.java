package models;

public class ReportingException extends Throwable {
	private int code;
	private String message;

	public ReportingException(int code, String message) {
		// TODO Auto-generated constructor stub
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return this.code;
	}

	public String toString() {
		return String.format("Exception with code %d and message: %s", this.code, this.message);
	}
}
