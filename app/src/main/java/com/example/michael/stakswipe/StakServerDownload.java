package com.example.michael.stakswipe;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class StakServerDownload extends AsyncTask<String, Void, String> {
    Context ctxt;

    StakServerDownload(Context c){
        ctxt= c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String tag = params[0];
        String place = params[1];
        String listingUrl = "http://10.0.0.169//stakSwipe/getListing.php?tag="+tag+"&place="+place+";";
        try{
            URL url = new URL(listingUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            /**OutputStream s = conn.getOutputStream();
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s, "UTF-8"));
            String data = URLEncoder.encode("tag", "UTF-8")+"="+URLEncoder.encode(tag, "UTF-8")+"&"+
                    URLEncoder.encode("place", "UTF-8")+"="+URLEncoder.encode(place, "UTF-8");
            w.write(data);
            w.flush();
            w.close();
            s.close();**/

            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String response = "";
            String line = "";
            while((line = reader.readLine())!=null){
                response+=line;
            }
            reader.close();
            inputStream.close();
            conn.disconnect();
            return response;

        }
        catch(MalformedURLException e){
            System.out.println(e);
        }
        catch (IOException e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
    }
}
