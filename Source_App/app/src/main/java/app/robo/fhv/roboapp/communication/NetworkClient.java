package app.robo.fhv.roboapp.communication;

import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkClient {

    private CommunicationClient communicationClient;

    public NetworkClient() throws SocketException, UnknownHostException {
        communicationClient = new CommunicationClient();
    }

    public void start() {
        new Thread(communicationClient).start();
    }

    public CommunicationClient getCommunicationClient() {
        return communicationClient;
    }
}
