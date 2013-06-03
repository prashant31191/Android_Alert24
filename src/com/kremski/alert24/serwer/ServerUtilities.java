package com.kremski.alert24.serwer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.serwer.requests.GetRequest;
import com.kremski.alert24.serwer.requests.PostRequest;

public class ServerUtilities {

	private String host;
	private final static String LOG_TAG = ServerUtilities.class.toString();

	public static final String SERWER_ADDRESS = "http://api.alert24.piotrgankiewicz.pl";
	public static final String SENDER_ID = "532473435676";
	public static final String SERWER_RESPONSE_ERROR_CODE_KEY = "ErrorCode";
	public static final String SERWER_RESPONSE_MESSAGE_KEY = "SystemNotification";
	public static final String SERWER_RESPONSE_STATUS_KEY = "ResponseStatus";

	public ServerUtilities(String host) {
		this.host = host;
	}

	public String makePostRequest(PostRequest request) throws ClientProtocolException, IOException{
		InputStream responseStream = null; 

		try {
			responseStream = makePostRequest(request.getContent(), host + request.getAction());
			return CharStreams.toString(new InputStreamReader(responseStream));
		} 
		finally {
			Closeables.closeQuietly(responseStream);
		}
	}

	private InputStream makePostRequest(String content, String url) throws ClientProtocolException, IOException{
		HttpURLConnection connection = createURLConnectionForRequest(url, "POST", true);
		writeContentThroughStream(connection.getOutputStream(), content);
		return getResponseStreamFromConnection(connection);
	}

	private HttpURLConnection createURLConnectionForRequest(String url, String requestMethod, boolean doOutput) throws MalformedURLException, IOException {
		HttpURLConnection httpcon = (HttpURLConnection) ((new URL(url).openConnection()));

		prepareURLConnectionForRequest(httpcon, requestMethod, doOutput);

		httpcon.connect();

		return httpcon;
	}

	private void prepareURLConnectionForRequest(HttpURLConnection urlConnection, String requestMethod, boolean doOutput) throws ProtocolException {
		urlConnection.setDoOutput(doOutput);
		urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		urlConnection.setRequestProperty("Accept", "application/json");
		urlConnection.setRequestMethod(requestMethod);
	}


	private void writeContentThroughStream(OutputStream destStream, String text) throws IOException {
		OutputStreamWriter writer = null; 

		try {
			writer = new OutputStreamWriter(destStream);
			writer.write(text);
			writer.close();
		}
		finally {
			Closeables.closeQuietly(writer);
		}
	}
	
	private InputStream getResponseStreamFromConnection(HttpURLConnection connection) throws IOException {
		if (connection.getResponseCode() != 200) {
			return connection.getErrorStream();
		} else {
			return connection.getInputStream();
		}
	}

	public String makeGetRequest(GetRequest request) throws IllegalStateException, ClientProtocolException, IOException {
		InputStream responseStream = null;
		
		try {
			responseStream = makeGetRequest(host + request.getAction());
			return CharStreams.toString(new InputStreamReader(responseStream));
		} 
		finally {
			Closeables.closeQuietly(responseStream);
		}
	}
	
	private InputStream makeGetRequest(String url) throws ClientProtocolException, IOException{
		HttpURLConnection connection = createURLConnectionForRequest(url, "GET", false);
		return getResponseStreamFromConnection(connection);
	}
	
	//TODO refactor this shit
	public String sendPhotoBitmap(String urlToUploadPhoto, Bitmap bitmap) throws ClientProtocolException, IOException {
		InputStream content = null;

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 70, bos);
			byte[] data = bos.toByteArray();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(urlToUploadPhoto);
			ByteArrayBody bab = new ByteArrayBody(data, "event_photo.jpeg");
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("uploaded", bab);
			postRequest.setHeader("Accept", "application/json");
			postRequest.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(postRequest);
			content = response.getEntity().getContent();
			return CharStreams.toString(new InputStreamReader(content));
		} 
		finally {
			Closeables.closeQuietly(content);
		}
	}

	public static boolean isResponseSuccessfull(String result) {
		return !result.contains(SERWER_RESPONSE_ERROR_CODE_KEY);
	}


}