package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String requestType;
    private String cheie;
    private String valoare;
    private TextView weatherForecastTextView;

    private Socket socket;

    public ClientThread(String address, int port, String requestType, String cheie, String valoare, TextView weatherForecastTextView) {
        this.address = address;
        this.port = port;
        this.requestType = requestType;
        this.cheie = cheie;
        this.valoare = valoare;
        this.weatherForecastTextView = weatherForecastTextView;
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
            printWriter.println(requestType);
            printWriter.flush();
            printWriter.println(cheie);
            printWriter.flush();
            printWriter.println(valoare);
            printWriter.flush();
            String weatherInformation;
            while ((weatherInformation = bufferedReader.readLine()) != null) {
                final String finalizedWeateherInformation = weatherInformation;
                weatherForecastTextView.post(new Runnable() {
                   @Override
                    public void run() {
                       weatherForecastTextView.setText(finalizedWeateherInformation);
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
