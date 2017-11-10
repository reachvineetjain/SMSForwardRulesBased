package com.nehvin.smsforwardrulesbased;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Future;

/**
 * Created by Neha on 27-Sep-17.
 */

public class DNDStatusLoader extends AsyncTaskLoader<String> {

    private Context context;

    public DNDStatusLoader(Context ctx)
    {
        super(ctx);
        this.context = ctx;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        String result = "";
        URL url ;
        HttpURLConnection urlConnection = null;
        InputStream in = null;

        try {
//            HttpResponse<JsonNode> response = Unirest.post("https://dnd.p.mashape.com/")
//                    .header("X-Mashape-Key", "1OmfXbVSymmshltVBggebOyhIIidp1jhy1qjsnpbCfSPshKf5I")
//                    .header("Content-Type", "application/json")
//                    .header("Accept", "application/json")
//                    .body("{\"mobile\":\"9820602603\"}")
//                    .asJson();

            HttpRequestWithBody hb = Unirest.post("https://dnd.p.mashape.com/");
            hb.header("X-Mashape-Key","1OmfXbVSymmshltVBggebOyhIIidp1jhy1qjsnpbCfSPshKf5I");
            hb.header("Content-Type", "application/json");
            hb.header("Accept", "application/json");
            hb.body("{\"mobile\":\"9820243666\"}");
            Future<HttpResponse<JsonNode>> future = hb.asJsonAsync(new Callback<JsonNode>() {

                public void failed(UnirestException e) {
                    System.out.println("The request has failed");
                }

                public void completed(HttpResponse<JsonNode> response) {
                    int code = response.getStatus();
                    Headers headers = response.getHeaders();
                    JsonNode body = response.getBody();
                    InputStream rawBody = response.getRawBody();
                }

                public void cancelled() {
                    System.out.println("The request has been cancelled");
                }

            });



        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @NonNull
    private HttpURLConnection getHttpURLConnection(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        return urlConnection;
    }

    /**
     * Uses a buffered reader to read a complete line rather than a single character at a time.
     * @param inpStream
     * @return
     */
    private String readFromStream(InputStream inpStream){

        Scanner s = new Scanner(inpStream, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
