package com.alelievangelista.popularmovies;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by aevangelista on 15-07-21.
 */
public class JSONParser {

    // constructor
    public JSONParser() {

    }

    public JSONObject getJSONFromUrl(String myURL) {

        String json = null;

        // Making HTTP request
        try {

            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while ((json = br.readLine()) != null) {
                    sb.append(json+"\n");
                }
                br.close();

                json = sb.toString();

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readJSONtoObj(json);

    }


    private JSONObject readJSONtoObj(String json){

        JSONObject jObj = null;

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        if (jObj != null){
            Log.d("readJSONtoObj: ", "The JSON is: " + json);
        }

        return jObj;

    }
}
