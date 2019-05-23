package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    EditText server_port, client_address, client_port, info_key, info_value;
    TextView response_textView;
    Button connect, get_info;

    ServerThread serverThread;
    ClientThread clientThread;

    private ConnectButtonOnClickListener connect_button = new ConnectButtonOnClickListener();
    private class ConnectButtonOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String serverPort = server_port.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    private GetInfoListener get_button = new GetInfoListener();
    private class GetInfoListener implements Button.OnClickListener{
        @Override
        public void onClick(View view) {
            String clientAddress = client_address.getText().toString();
            String clientPort = client_port.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String info_k = info_key.getText().toString();
            String info_v = info_value.getText().toString();

            if (info_k == null || info_k.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            //TODO
            response_textView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), info_k, info_v, response_textView
            );
            clientThread.start();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        server_port = (EditText) findViewById(R.id.server_port_editText);
        client_address = (EditText) findViewById(R.id.client_address_editText);
        connect = (Button) findViewById(R.id.connect_button);
        client_port = (EditText) findViewById(R.id.client_port_editText);
        connect.setOnClickListener(connect_button);

        info_key = (EditText) findViewById(R.id.info_key);
        info_value = (EditText) findViewById(R.id.info_value);
        response_textView = (TextView) findViewById(R.id.response_textView);

        get_info = (Button) findViewById(R.id.client_get_button);
        get_info.setOnClickListener(get_button);
    }
}
