package edu.northeastern.final_project;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FDAFoodDatabaseConnector {
    private String API_KEY = BuildConfig.API_KEY;
    private String BASE_URL_FOOD="https://api.nal.usda.gov/fdc/v1/food";
    private String BASE_URL_FOODS="https://api.nal.usda.gov/fdc/v1/foods";
    private String SEARCH_BASE_URL = BASE_URL_FOODS + "/search?api_key=" + API_KEY + "&query=";

    public FDAFoodDatabaseConnector() {
        Log.d("FDAFoodDatabaseConnector", "Initializing FDAFoodDatabaseConnector!");
    }

    public String search(String searchString) {
        String fmtSearchString = formatSearchString(searchString);
        URL searchURL = checkURLFormation(SEARCH_BASE_URL + fmtSearchString);
        Log.d("FDAFoodDatabaseConnector", searchURL.toString());
        String returnString = getRequest(searchURL);
        // Log.d("FDAFoodDatabaseConnector", returnString);
        return returnString;

    }

    protected String formatSearchString(String searchString) {
        searchString = searchString.replace(" ", "%20");
        return searchString;
    }

    public static String getRequest(URL url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            InputStream inputStream = conn.getInputStream();
            String resp = convertStreamToString(inputStream);
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String len;

            while((len=bufferedReader.readLine()) != null) {
                stringBuilder.append(len);
            }
            bufferedReader.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static URL checkURLFormation(String urlString) {
        try {
            URL target_url = new URL(urlString);
            return target_url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}


class FDAKeywordQuery {
    private String kwQuery = null;
    private String queryResponse = null;
    public FDAKeywordQuery(String keyword) {
        Log.d("FDAKeywordQuery", "Starting FDA Keyword search");
        kwQuery = keyword;
    }

    public JsonObject search() {
        Log.d("FDAKeywordQuery", "Starting FDA Keyword search");
        // TODO: Error handling
        FDAThread runnableAPIThread = new FDAThread();
        Thread apiThread = new Thread(runnableAPIThread);
        apiThread.start();
        try {
            apiThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        JsonObject jsonResponse = null;
        if (queryResponse != null) {
            jsonResponse = (JsonObject) JsonParser.parseString(queryResponse);
        }
        Log.d("FDAKeywordQuery", String.valueOf(jsonResponse));
        return jsonResponse;
    }

    class FDAThread implements Runnable {
        @Override
        public void run() {
            Log.d("FDAKeywordQuery", "Connecting to FDA DB through thread");
            FDAFoodDatabaseConnector FDADBConn = new FDAFoodDatabaseConnector();
            queryResponse = FDADBConn.search(kwQuery);
        }
    }
}
