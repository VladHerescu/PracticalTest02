package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }



        String pageSourceCode = null;
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (key / value type!");


//            String word = bufferedReader.readLine();
//            if (word == null || word.isEmpty()) {
//                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (word");
//                return;
//            }
//
//            HashMap<String, String> data = serverThread.getData();
//            String responseInformation = null;
//
//
//            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + word);
//            ResponseHandler<String> responseHandler = new BasicResponseHandler();
//            pageSourceCode = httpClient.execute(httpGet, responseHandler);
            String method = "";
            String key = "";
            String value = "";
            method = bufferedReader.readLine();
            if (method.equals("get")) {
                key = bufferedReader.readLine();
                HashMap<String, Long> timestamps = serverThread.getTimetamps();
                HashMap<String, String> data = serverThread.getData();
                if (data.containsKey(key)) {
                    if (timestamps.get(key) - System.currentTimeMillis() > 60000) {
                        printWriter.println("none\n");
                    } else {
                        printWriter.println(data.get(key));
                    }
                } else {
                    printWriter.println("none\n");
                }
            } else {
                key = bufferedReader.readLine();
                value = bufferedReader.readLine();
                serverThread.setData(key, value);
                serverThread.setTimestamps(key, System.currentTimeMillis());
            }

//            printWriter.println(result);
            printWriter.flush();


        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
//        } catch (JSONException jsonException) {
//            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
//            if (Constants.DEBUG) {
//                jsonException.printStackTrace();
//            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }

    }

}