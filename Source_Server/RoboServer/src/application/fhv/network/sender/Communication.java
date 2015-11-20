package network.sender;

import communication.managers.IAnswerHandler;
import communication.managers.IDataReceivedHandler;

public abstract class Communication implements Runnable, IDataReceivedHandler, IAnswerHandler {
}
