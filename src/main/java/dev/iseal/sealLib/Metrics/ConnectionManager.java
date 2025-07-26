package dev.iseal.sealLib.Metrics;

import dev.iseal.sealLib.SealLib;
import dev.iseal.sealUtils.utils.ExceptionHandler;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager {

    private final Logger l = SealLib.getPlugin().getLogger();

    private static ConnectionManager instance;
    public static ConnectionManager getInstance() {
        if (instance == null)
            instance = new ConnectionManager();
        return instance;
    }

    public String[] sendDataToModrinth(String endpoint) {
        return initConnection("https://api.modrinth.com/v2/" + endpoint, "GET", "", false);
    }

    /*
        * @param endpoint The endpoint to connect to
        * @param method The method to use
        * @param payload The payload to send
        * @param errorOnFail Whether to throw an exception if the connection returns a non-200 response
        *
        * @return A string array containing the response and the response code
     */
    private String[] initConnection(String endpoint, String method, String payload, boolean errorOnFail) {
        try {
            // Creating a URL object
            HttpsURLConnection connection = getHttpsURLConnection(endpoint, method, payload);

            // Retrieving the response code
            int responseCode = connection.getResponseCode();

            if (SealLib.isDebug())
                l.info("Response code: " + responseCode);

            // Processing the response
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return new String[]{response.toString(), String.valueOf(responseCode)};
            } else {
                if (errorOnFail)
                    ExceptionHandler.getInstance().dealWithException(new IOException("The API returned a non-200 result. Check any updates on the discord if it is down"), Level.WARNING, "API_ERROR_CODE_"+responseCode, l);
            }
        } catch (Exception e) {
            ExceptionHandler.getInstance().dealWithException(e, Level.WARNING, "API_CONN_FAILED", l, endpoint, method, payload);
        }
        return new String[]{"", "-1"};
    }

    private HttpsURLConnection getHttpsURLConnection(String endpoint, String method, String payload) throws IOException {
        URL url = new URL(endpoint);

        if (SealLib.isDebug())
            l.info("Connecting to " + url);

        // Opening a connection
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        // Setting the timeout - 3 seconds so if server goes down again it doesn't take too long
        connection.setConnectTimeout(3000);

        // Setting the request method
        connection.setRequestMethod(method);

        if (method.equals("POST"))
            // Setting the content type
            connection.setRequestProperty("Content-Type", "application/json");

        if (!payload.isEmpty()) {
            // Allowing output
            connection.setDoOutput(true);
            // Send request body
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(payload);
            out.flush(); // Ensure all data is sent
            out.close(); // Close the stream
        }
        return connection;
    }
}
