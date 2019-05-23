package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String info_key;
    private String info_value;
    private TextView responseTextView;
    private Socket socket;

    public ClientThread(String address, int port, String info_key, String info_value, TextView responseTextView) {
        this.address = address;
        this.port = port;
        this.info_key = info_key;
        this.info_value = info_value;
        this.responseTextView = responseTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            if (info_value == null || info_value.equals("")) {
                printWriter.println("get");
                printWriter.println(info_key);
            } else {
                printWriter.println("put");
                printWriter.println(info_key);
                printWriter.println(info_value);
            }
            printWriter.flush();
            String information;

            //TODO
            while ((information = bufferedReader.readLine()) != null) {
                final String Information = information;
                responseTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        responseTextView.setText(responseTextView.getText().toString() + Information);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}

