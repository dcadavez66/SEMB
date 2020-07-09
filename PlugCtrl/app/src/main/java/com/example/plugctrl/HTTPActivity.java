package com.example.plugctrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPActivity {

    private static final String USER_AGENT = "Mozilla/5.0";

    public String GET_URL = "https://";

    public String POST_URL = "https://";

    public String POST_PARAMS = "";

    public String respGET = "";
    public String respPOST = "";
    /*public static void main(String[] args) throws IOException {

        sendGET();
        System.out.println("GET DONE");
        sendPOST();
        System.out.println("POST DONE");
    }*/

    public  String sendGET() throws IOException {
        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            respGET = response.toString();
            return respGET;
        } else {
            System.out.println("GET request not worked");
            respGET = "failed";
            return respGET;
        }

    }

    public  String sendPOST() throws IOException {
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            respPOST = response.toString();
            return respPOST;
        } else {
            System.out.println("POST request not worked");
            respPOST = "failed";
            return  respPOST;
        }
    }

    public void setGET_URL(String GET_URL) {
        this.GET_URL = GET_URL;
    }

    public void setPOST_URL(String POST_URL){
        this.POST_URL = POST_URL;
    }

    public void setPOST_PARAMS(String POST_PARAMS) {
        this.POST_PARAMS = POST_PARAMS;
    }
}