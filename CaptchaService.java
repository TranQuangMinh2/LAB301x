package com.trnqngmnh.library;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {

	@Autowired
	private CaptchaConfig captchaConfig;

	@Autowired
	private RestTemplate restTemplate;

	/*
	 * private static final String GOOGLE_RECAPTCHA_VERIFY_URL =
	 * "https://www.google.com/recaptcha/api/siteverify";
	 */
	private static final String RECAPTCHA_SECRET_KEY = "6LcoJwEqAAAAAECLx8J6zN8P0A8dZrJSHYDYh2vL";
	private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	// public boolean verifyRecaptcha(String recaptchaResponse) {
	/*
	 * if (recaptchaResponse == null || recaptchaResponse.isEmpty()) { return false;
	 * } Map<String, String> body = new HashMap<>(); body.put("secret",
	 * captchaConfig.getRecaptchaSecretKey()); body.put("response",
	 * recaptchaResponse);
	 * 
	 * Map<String, Object> response = null; try { response =
	 * restTemplate.postForObject(GOOGLE_RECAPTCHA_VERIFY_URL, body, Map.class); }
	 * catch (Exception e) { e.printStackTrace(); return false; }
	 * 
	 * if (response == null) { return false; }
	 * 
	 * Boolean success = (Boolean) response.get("success"); return success != null
	 * && success;
	 */// }
	@SuppressWarnings("deprecation")
	public boolean verifyRecaptcha(String recaptchaResponse) {
		try {
			String url = RECAPTCHA_VERIFY_URL + "?secret=" + RECAPTCHA_SECRET_KEY + "&response=" + recaptchaResponse;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
			System.out.println("Sending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			System.out.println("Response: " + response.toString());

			JSONObject jsonResponse = new JSONObject(response.toString());
			return jsonResponse.getBoolean("success");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
