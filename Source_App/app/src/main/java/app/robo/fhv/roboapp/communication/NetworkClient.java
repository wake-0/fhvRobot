package app.robo.fhv.roboapp.communication;

import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkClient {

    private final MediaStreaming mediaStreaming;
    private CommunicationClient communicationClient;

    public NetworkClient(MediaStreaming.IFrameReceived callback) throws SocketException, UnknownHostException {
        communicationClient = new CommunicationClient();
        mediaStreaming = new MediaStreaming(GlobalSettings.MEDIA_STREAMING_INPUT_PORT, callback);
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
}
