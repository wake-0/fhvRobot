package app.robo.fhv.roboapp.communication;

import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkClient {

    private final MediaStreaming mediaStreaming;
    private CommunicationClient communicationClient;

    public NetworkClient(CommunicationClient.ICommunicationCallback commCallback, MediaStreaming.IFrameReceived frameCallback) throws SocketException, UnknownHostException {
        communicationClient = new CommunicationClient(commCallback);
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
}
