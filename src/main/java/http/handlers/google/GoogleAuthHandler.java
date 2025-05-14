package http.handlers.google;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import config.GoogleConfig;
import http.BaseHttpClient;
import models.ReportingException;

public class GoogleAuthHandler {
	private final String clientEmail;
	private final PrivateKey privateKey;

	public GoogleAuthHandler() throws Exception {
		this.clientEmail = GoogleConfig.getInstance().getClientEmail();
		String privateKeyPem = GoogleConfig.getInstance().getPrivateKey().replace("\\n", "\n");
		this.privateKey = loadPrivateKey(privateKeyPem);
	}

	private PrivateKey loadPrivateKey(String pem) throws Exception {
		String privateKeyPEM = pem.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");

		byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		return KeyFactory.getInstance("RSA").generatePrivate(spec);
	}

	public String getAccessToken() throws Exception, ReportingException {
		String jwt = createSignedJwt();
		String body = "grant_type="
				+ URLEncoder.encode("urn:ietf:params:oauth:grant-type:jwt-bearer", StandardCharsets.UTF_8)
				+ "&assertion=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);

		Map<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/x-www-form-urlencoded");
		String tokenResponse = BaseHttpClient.getInstance().post("https://oauth2.googleapis.com/token", header, body);

		return extractJsonValue(tokenResponse, "access_token");
	}

	private String createSignedJwt() throws Exception {
		long now = Instant.now().getEpochSecond();
		long exp = now + 3600;

		String header = base64UrlEncode("{\"alg\":\"RS256\",\"typ\":\"JWT\"}");
		String payload = base64UrlEncode("{" + "\"iss\":\"" + clientEmail + "\","
				+ "\"scope\":\"https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/spreadsheets\","
				+ "\"aud\":\"https://oauth2.googleapis.com/token\"," + "\"iat\":" + now + "," + "\"exp\":" + exp + "}");

		String toSign = header + "." + payload;
		byte[] signature = sign(toSign.getBytes(StandardCharsets.UTF_8));
		return toSign + "." + base64UrlEncode(signature);
	}

	private String base64UrlEncode(String input) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(input.getBytes(StandardCharsets.UTF_8));
	}

	private String base64UrlEncode(byte[] input) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
	}

	private byte[] sign(byte[] data) throws Exception {
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initSign(privateKey);
		sig.update(data);
		return sig.sign();
	}

	private String extractJsonValue(String json, String key) {
		JSONObject jsonObject = new JSONObject(json);
		System.out.println("json obj: " + jsonObject);
		if (jsonObject.has(key)) {
			return jsonObject.getString(key);
		} else {
			System.out.println("no key");
			throw new RuntimeException("Key not found: " + key);
		}
	}
}
