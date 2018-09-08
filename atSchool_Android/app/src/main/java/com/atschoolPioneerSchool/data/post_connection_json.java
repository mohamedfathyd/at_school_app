package com.atschoolPioneerSchool.data;

import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class post_connection_json {

    public String makePostRequest(String url, String[] tag, String[] value) throws Exception {

        //this line helps connecting to the internet
        StrictMode.enableDefaults();
        //buffer reader to read the json code from the website
        BufferedReader in = null;
        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost(url);


        //Post Data
        List<NameValuePair> nameValuePair = new ArrayList<>(tag.length);
        for (int i = 0; i < tag.length; i++) {
            nameValuePair.add(new BasicNameValuePair(tag[i], value[i]));
        }

        //Encoding POST data
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //making POST request.
        try {
            HttpResponse response = httpClient.execute(httpPost);

            //reading the json code
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            //converting the result into organized string buffer
            StringBuffer sb = new StringBuffer("");
            String line = "";

            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            //close the buffer reader
            in.close();
            //converting the sting buffer into string
            String page = sb.toString();

            // write response to log
            // Log.d("Http Post Response:", response.toString());
            return page;
        } catch (ClientProtocolException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        } finally {//to make sure the buffer reader is closed
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.d("BBB", e.toString());
                }
            }
        }

        return "";
    }

    public String makePostRequest1(String _url, String[] tag, String[] value) throws Exception {
        URL url = new URL(_url);
        //   HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        java.net.HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        final Uri.Builder builder = new Uri.Builder();
        // .appendQueryParameter("key1", valu1)
        //.appendQueryParameter("key2", value2);
        for (int i = 0; i < tag.length; i++) {
            builder.appendQueryParameter(tag[i], value[i]);
        }

        String query = builder.build().getEncodedQuery();
        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(query);
        writer.flush();
        writer.close();
        os.close();
        connection.connect();


        return "";
    }

    public String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (IOException ex) {
            Log.d(getClass().getName(), ex.getMessage());
        } finally {
            if (c != null) {

                c.disconnect();

            }
        }
        return null;
    }
}
