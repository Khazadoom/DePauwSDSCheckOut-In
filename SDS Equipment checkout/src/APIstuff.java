
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





import java.io.*;


import com.google.api.services.drive.model.File;

import java.lang.*;
import java.io.IOException;
import java.io.InputStream;



public class APIstuff {
	
	
	
	public static Credential getCredentials(String authorizationCode, String state)
{
		    String emailAddress = "";
		    try {
		      Credential credentials = exchangeCode(authorizationCode);
		      Userinfoplus userInfo = credentials.;
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
		    } catch (CodeExchangeException e) {
		      e.printStackTrace();
		      // Drive apps should try to retrieve the user and credentials for the
		      // current session.
		      // If none is available, redirect the user to the authorization URL.
		      e.setAuthorizationUrl(getAuthorizationUrl(emailAddress, state));
		      throw e;
		    } catch (NoUserIdException e) {
		      e.printStackTrace();
		    }
		    // No refresh token has been retrieved.
		    String authorizationUrl = getAuthorizationUrl(emailAddress, state);
		    throw new NoRefreshTokenException(authorizationUrl);
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
