
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.auth.oauth.*;
import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.services.oauth2.Oauth2;
//import com.google.api.client.extensions.servlet.auth.oauth2.*;
////import com.google.api.client.extensions.appengine.auth.oauth2.*;
import com.google.api.services.oauth2.model.*;

import java.util.*;
import java.io.*;

import com.google.api.services.drive.model.File;

import java.lang.*;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.google.api.client.auth.openidconnect.*;
import com.google.api.client.googleapis.services.*;




public class APIstuff {
	
	 private static final String CLIENTSECRET_LOCATION = "/client_secret.json";
	  private static final String APPLICATION_NAME = "Your app name";
	  private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
	  private static final List<String> SCOPES = Arrays.asList(
	      "https://www.googleapis.com/auth/drive.file",
	      "email",
	      "profile");

	  private static GoogleAuthorizationCodeFlow flow = null;
	  private static final JacksonFactory JSON_FACTORY =
	      JacksonFactory.getDefaultInstance();
	  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	
	  static GoogleAuthorizationCodeFlow getFlow() throws IOException {
		    if (flow == null) {
		      InputStream in =
		          APIstuff.class.getResourceAsStream(CLIENTSECRET_LOCATION);
		      GoogleClientSecrets clientSecret =
		          GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		      flow = new GoogleAuthorizationCodeFlow.Builder(
		          HTTP_TRANSPORT, JSON_FACTORY, clientSecret, SCOPES)
		          .setAccessType("offline")
		          .setApprovalPrompt("force")
		          .build();
		    }
		    return flow;
		  }
	  
	  
	  static Credential exchangeCode(String authorizationCode)
		       throws IOException {

		      GoogleAuthorizationCodeFlow flow = getFlow();
		      GoogleTokenResponse response = flow
		          .newTokenRequest(authorizationCode)
		          .setRedirectUri(REDIRECT_URI)
		          .execute();
		      return flow.createAndStoreCredential(response, null);

		  }
	  
	  
	static void storeCredentials(String userId, Credential credentials) throws IOException{
	
		BufferedWriter writer = null;
		String title = "UserInfo.txt";
        String homefolder = System.getProperty("user.home");
        java.io.File userInfoFile = new java.io.File(homefolder,title);
        writer = new BufferedWriter(new FileWriter(userInfoFile,false));
        writer.write(userId + "," + credentials.getAccessToken() + "," + credentials.getRefreshToken());
		writer.close();
	}
	
	static Credential getStoredCredentials(String userId){
	
		
		
		return null;
	}
	
	
	static Userinfoplus getUserInfo(Credential credentials)
		    throws IOException {
		    Oauth2 userInfoService = new Oauth2.Builder(
		        HTTP_TRANSPORT, JSON_FACTORY, credentials)
		        .setApplicationName(APPLICATION_NAME)
		        .build();
		    Userinfoplus userInfo = null;
		    
		      userInfo = userInfoService.userinfo().get().execute();
		    
		      return userInfo; 
		  }
	
	public static String getAuthorizationUrl(String emailAddress, String state)
		      throws IOException {
		    GoogleAuthorizationCodeRequestUrl urlBuilder = getFlow()
		        .newAuthorizationUrl()
		        .setRedirectUri(REDIRECT_URI)
		        .setState(state);
		    urlBuilder.set("user_id", emailAddress);
		    return urlBuilder.build();
		  }
	
	public static Credential getCredentials(String authorizationCode, String state)
{
		    String emailAddress = "";
		      Credential credentials = exchangeCode(authorizationCode);
		      Userinfoplus userInfo = getUserInfo(credentials);
		      String userId = userInfo.getId();
		      emailAddress = userInfo.getEmail();
		      if (credentials.getRefreshToken() != null) {
		        storeCredentials(userId, credentials);
		        return credentials;
		      } else {
		        credentials = getStoredCredentials(userId);
		        if (credentials != null && credentials.getRefreshToken() != null) {
		            return credentials;
		        }
		      }
		    
		    // No refresh token has been retrieved.
		    String authorizationUrl = getAuthorizationUrl(emailAddress, state);

	
}

	
	
	
	
	

	static Drive buildService(GoogleCredential credentials) {
	    HttpTransport httpTransport = new NetHttpTransport();
	    JacksonFactory jsonFactory = new JacksonFactory();

	    return new Drive.Builder(httpTransport, jsonFactory, credentials)
	        .build();
	  }
	
	public static String downloadFromDrive(java.io.File file){
	
		
		Drive service = buildService();
        
       	
		
		String fileContents = null;
		
		File Drivefile = new File();
		
		Drivefile.setId("1T_rXb_2CcB4YCA9NS9PyOuboSNcw8dTdnklUYB3O2Fg");
		String downloadUrl = Drivefile.getExportLinks().get("text/csv");
		System.out.println(downloadUrl);
		fileContents = convertStreamToString(downloadFile(service,Drivefile));
		return fileContents;
		
	}
	
	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");

	    return s.hasNext() ? s.next() : "";
	}
	
	private static void printFile(Drive service, String fileId) {

	    try {
	      File file = service.files().get(fileId).execute();

	      System.out.println("Title: " + file.getTitle());
	      System.out.println("Description: " + file.getDescription());
	      System.out.println("MIME type: " + file.getMimeType());
	    } catch (IOException e) {
	      System.out.println("An error occured: " + e);
	    }
	  }

	  /**
	   * Download a file's content.
	   *
	   * @param service Drive API service instance.
	   * @param file Drive File instance.
	   * @return InputStream containing the file's content if successful,
	   *         {@code null} otherwise.
	   */
	  private static InputStream downloadFile(Drive service, File file) {
	    if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
	      try {
	        HttpResponse resp =
	            service.getRequestFactory().buildGetRequest(new GenericUrl("1T_rXb_2CcB4YCA9NS9PyOuboSNcw8dTdnklUYB3O2Fg"))
	                .execute();
	        return resp.getContent();
	      } catch (IOException e) {
	        // An error occurred.
	        e.printStackTrace();
	        return null;
	      }
	    } else {
	      // The file doesn't have any content stored on Drive.
	      return null;
	    }
	  }
	
	
	
	
	
}
