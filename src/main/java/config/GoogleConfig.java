package config;

public class GoogleConfig {
	private final String type = "service_account";

	private final String projectId = System.getenv("PROJECT_ID");
	private final String privateKeyId = System.getenv("PRIVATE_KEY_ID");
	private final String privateKey = System.getenv("PRIVATE_KEY");
	private final String clientEmail = System.getenv("CLIENT_EMAIL");
	private final String clientId = System.getenv("CLIENT_ID");
	private final String authUri = "https://accounts.google.com/o/oauth2/auth";
	private final String tokenUri = "https://oauth2.googleapis.com/token";
	private final String authProviderX509CertUrl = "https://www.googleapis.com/oauth2/v1/certs";
	private final String clientX509CertUrl = System.getenv("CLIENT_X509_CERT_URL");
	private final String universeDomain = "googleapis.com";

	public static GoogleConfig Instance = new GoogleConfig();

	private GoogleConfig() {

	}

	public static GoogleConfig getInstance() {
		return Instance;
	}

	public String getType() {
		return type;
	}

	public String getProjectId() {
		return projectId;
	}

	public String getPrivateKeyId() {
		return privateKeyId;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public String getClientId() {
		return clientId;
	}

	public String getAuthUri() {
		return authUri;
	}

	public String getTokenUri() {
		return tokenUri;
	}

	public String getAuthProviderX509CertUrl() {
		return authProviderX509CertUrl;
	}

	public String getClientX509CertUrl() {
		return clientX509CertUrl;
	}

	public String getUniverseDomain() {
		return universeDomain;
	}

	public void setInstance(GoogleConfig instance) {
		Instance = instance;
	}
}
