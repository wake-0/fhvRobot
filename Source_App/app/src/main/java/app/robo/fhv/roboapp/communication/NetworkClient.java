package app.robo.fhv.roboapp.communication;

import android.os.AsyncTask;

import java.net.SocketException;
import java.net.UnknownHostException;

import app.robo.fhv.roboapp.views.IHighScoreManager;

public class NetworkClient {

    private final MediaStreaming mediaStreaming;
    private CommunicationClient communicationClient;

    public NetworkClient(CommunicationClient.ICommunicationCallback commCallback, MediaStreaming.IFrameReceived frameCallback, IHighScoreManager highScoreManager, String serverAddress) throws SocketException, UnknownHostException {
        communicationClient = new CommunicationClient(commCallback, highScoreManager, serverAddress);
        mediaStreaming = new MediaStreaming(GlobalSettings.MEDIA_STREAMING_INPUT_PORT, frameCallback);
    }

    public void start() {
        new Thread(communicationClient).start();
        new Thread(mediaStreaming).start();
    }

    public CommunicationClient getCommunicationClient() {
        return communicationClient;
    }

    public void stop() {
        communicationClient.stop();
        mediaStreaming.stop();
    }

    public void disconnect() {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                getCommunicationClient().sendDisconnect(new CommunicationClient.ISendTaskFinished()
                {
                    @Override
                    public void onFinish() {
                        stop();
                    }
                });
                return null;
            }
        }.execute();
    }
}
