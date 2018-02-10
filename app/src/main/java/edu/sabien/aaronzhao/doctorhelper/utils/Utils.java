package edu.sabien.aaronzhao.doctorhelper.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by AaronZhao on 3/2/16.
 */
public class Utils {

    private static final String TAG = "Utils";
    private static String ipResult;

    public static String readFromTxtfile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_APPEND);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }

    public static JSONObject readFromJsonFile(String fileName, Context context){  //if the method returns null, it fails to load JSON on file
        String jsonString = null;
        JSONObject jsonObject = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");  //if the jsonstring is null, fails to read it
            //jsonString.replaceAll("\\s*|\t|\r|\n","");
            //Log.d(TAG, jsonString);
            jsonObject = new JSONObject(jsonString);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex){
            ex.printStackTrace();
        }
        return jsonObject;
    }

    public static String readFromJsonFileToString(String fileName, Context context){  //if the method returns null, it fails to load JSON on file
        String jsonString = null;
        JSONObject jsonObject = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");  //if the jsonstring is null, fails to read it
            jsonString.replaceAll("\\s*|\t|\r|\n","");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return jsonString;
    }

    public static Properties readProps(String fileName,Context context){
        Properties properties = null;
        try {
            InputStream input = context.getAssets().open(fileName);
            properties = new Properties();
            properties.load(input);
        }catch (IOException ex){
            ex.printStackTrace();
        }catch (Resources.NotFoundException ex){
            ex.printStackTrace();
        }
        return properties;
    }

    public static String getCurrentIP(){  //get current external IPv4 address, only needs to use AsyncTask

        try {
            URL url = new URL("https://ifcfg.me/ipv4");
            SSLContext sslcontext = SSLContext.getInstance("TLSv1");
            sslcontext.init(null,
                    null,
                    null);
            SSLSocketFactory NoSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());

            HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);
            URLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream in = urlConnection.getInputStream();

            if (in != null) {
                //Log.d(TAG, convertStreamToString(in));
                String ipAddr = convertStreamToString(in);
                in.close();
                return ipAddr;
            } else {
                Log.e(TAG, "Nothing in body!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "domain error!");
        }catch (NoSuchAlgorithmException ex){
            Log.e(TAG, "TLSv1 does not exist!");
            ex.printStackTrace();
        }catch (KeyManagementException ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(result);
        return m.replaceAll("");
    }

}
