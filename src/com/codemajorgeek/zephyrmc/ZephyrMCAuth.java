package com.codemajorgeek.zephyrmc;

import java.io.*;
import java.net.*;

import org.apache.commons.io.*;
import org.json.*;

public class ZephyrMCAuth {

	private URL query_url;
	private String username;
	private char[] passwd;

	public ZephyrMCAuth(String url, String username, char[] passwd) throws MalformedURLException {

		query_url = new URL(url);
		this.username = username;
		this.passwd = passwd;
	}

	public String isExisting() throws IOException, JSONException {
		String UUID = null;

		String json = "{ \"method\" : \"zephyrmc.auth\", \"pseudo\" : [ \"" + username + "\" ], \"passwd\" : [ \""
				+ passwd + "\" ] }";
		HttpURLConnection conn = (HttpURLConnection) query_url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		OutputStream os = conn.getOutputStream();
		os.write(json.getBytes("UTF-8"));
		os.flush();
		os.close();

		InputStream in = new BufferedInputStream(conn.getInputStream());
		String result = IOUtils.toString(in, "UTF-8");
		JSONObject response = new JSONObject(result);
		if (!response.getBoolean("exist"))
			return null;
		else
			UUID = response.getString("UUID");

		in.close();
		conn.disconnect();

		return UUID;
	}
	
	public String getPseudo() {
		
		return username;
	}
}
