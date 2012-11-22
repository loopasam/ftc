package controllers;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import play.Logger;
import play.Play;
import play.libs.Crypto;
import play.libs.Time;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;

//TODO: put the right URL (instead of dev)
public class Security extends Controller {

	@Before(unless={"login", "auth", "logout"})
	static void checkAccess() throws Throwable {
		Logger.info("-----");
		Logger.info("Admin - At before");
		if(!session.contains("username")) {
			Logger.info("User not logged, shows login screen");
			login();
		}
		Logger.info("Admin - User logged");
	}

	public static void login() throws Throwable {

		Http.Cookie remember = request.cookies.get("rememberme");
		if(remember != null) {
			Logger.info("Admin - remeber different of null");
			int firstIndex = remember.value.indexOf("-");
			int lastIndex = remember.value.lastIndexOf("-");
			if (lastIndex > firstIndex) {
				String sign = remember.value.substring(0, firstIndex);
				String restOfCookie = remember.value.substring(firstIndex + 1);
				String username = remember.value.substring(firstIndex + 1, lastIndex);
				String time = remember.value.substring(lastIndex + 1);
				Date expirationDate = new Date(Long.parseLong(time)); // surround with try/catch?
				Date now = new Date();
				if (expirationDate == null || expirationDate.before(now)) {
					Logger.info("Admin - cookie expired");
					logout();
				}
				if(Crypto.sign(restOfCookie).equals(sign)) {
					Logger.info("Admin - correct cookie");
					session.put("username", username);
					Administration.index();
				}
			}
		}

		Logger.info("Admin - Doing the Google OAuth");
		//The user is supposed to let access to it's information
		String urlGoogleOAuth = "https://accounts.google.com/o/oauth2/auth?" +
				"scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+" +
				"&state=%2Fprofile&redirect_uri=http%3A%2F%2F" +
				"localhost:9000%2Fauth" +
				"&response_type=code" +
				"&client_id=1009954177381.apps.googleusercontent.com" +
				"&approval_prompt=auto";
		//The response is redirected on the auth() method.
		redirect(urlGoogleOAuth);
	}



	public static void auth(String code) {

		Logger.info("Admin - Back from Google OAuth");
		//Depending on what the user entered, could be an error
		if(code == null){
			Logger.info("Admin - Code is null --> home page");
			redirect("Application.index");
		}

		Logger.info("Admin - Call for token");
		//call for access token
		HttpResponse res = WS.url("https://accounts.google.com/o/oauth2/token").
				setParameter("code", code).setParameter("client_id", "1009954177381.apps.googleusercontent.com").
				setParameter("client_secret", "ClzAiwp6nuTvRqbQpkMq4GS7").setParameter("redirect_uri", "http://localhost:9000/auth").
				setParameter("grant_type", "authorization_code").post();

		JsonObject json = res.getJson().getAsJsonObject();

		if(json.get("access_token") != null){
			String accessToken = json.get("access_token").getAsString();
			HttpResponse resToken = WS.url("https://www.googleapis.com/oauth2/v1/userinfo?access_token="+accessToken).get();

			JsonObject profile = resToken.getJson().getAsJsonObject();
			if(profile.get("email") != null){
				String email = profile.get("email").getAsString().replaceAll("\\\"", "");
				String[] admins = ((String) Play.configuration.get("admin.emails")).split(",");
				for (String emailAdmin : admins) {
					if(email.equals(emailAdmin)){
						session.put("username", email);
						Date expiration = new Date();
						String duration = "1d";
						expiration.setTime(expiration.getTime() + Time.parseDuration(duration));
						response.setCookie("rememberme", Crypto.sign(email + "-" + expiration.getTime()) + "-" + email + "-" + expiration.getTime(), duration);
						Logger.info("Admin - Everything went fine");
						Administration.index();
					}
				}

			}

		}
		Logger.info("Admin - Access token was null going to show the home page");
		Application.index();
	}

	public static void logout() throws Throwable {
		session.clear();
		response.removeCookie("rememberme");
		Logger.info("Admin - logout");
		Application.index();
	}



}
