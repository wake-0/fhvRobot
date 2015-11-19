/*
 * UdpConnection.h
 *
 *  Created on: 29.10.2015
 *      Author: Nicolaj Hoess
 */

#ifndef INCLUDE_CONNECTIONLAYER_H_
#define INCLUDE_CONNECTIONLAYER_H_

#include <stdlib.h>
#include <typeinfo>
#include <memory>
#include <string.h>
#include <pthread.h>
#include "Debugger.h"

using namespace std;

namespace FhvRobotProtocolStack {

/* Abstract definitions of Protocol Layers */

class ProtocolLayerCallback {
public:
	ProtocolLayerCallback() { }
	virtual ~ProtocolLayerCallback() { }
	virtual void MessageReceived(const char* msg, unsigned int len) = 0;
};

class ProtocolLayer : public ProtocolLayerCallback {
protected:
	ProtocolLayerCallback* callback;
	ProtocolLayer* lowerLayer;

	virtual int GetMessageDecorationLength(const char* inMsg, unsigned int inLen) { return 0; }

	virtual void ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int outLen) = 0;

	/**
	 *  \brief This function decomposes a received message and returns true if the message is a valid message acc.
	 *  	   to the defined protocol, false otherwise. If the function returns false the outMsg and outLen must
	 *  	   not be used by the caller.
	 */
	virtual bool DecomposeMessage(const char* inMsg, unsigned int inLen, char** outMsg, unsigned int* outLen) = 0;

public:
	ProtocolLayer(ProtocolLayer* lower) { lowerLayer = lower; callback = NULL; }
	virtual ~ProtocolLayer() { lowerLayer = 0; callback = 0; }
	void SetCallback(ProtocolLayerCallback* cb) { callback = cb; }

	virtual bool Send(const char* msg, unsigned int len) {
		Debugger(VERBOSE) << "Sending message in " << typeid(this).name() << "\n";
		unsigned int outLen = GetMessageDecorationLength(msg, len) + len;
		char* outMsg = (char*) malloc(sizeof(char) * (outLen));
		Debugger(VERBOSE) << "Composing message\n";
		ComposeMessage(msg, len, outMsg, outLen);
		Debugger(VERBOSE) << "Message composed correctly\n";

		bool result = lowerLayer->Send(outMsg, outLen);

		free (outMsg);
		return result;
	}

	virtual void MessageReceived(const char* msg, unsigned int len) {
		char* outMsg = NULL;
		unsigned int outLen = 0;
		bool result = DecomposeMessage(msg, len, &outMsg, &outLen);
		if (result) {
			callback->MessageReceived(outMsg, outLen);
		}
	}

	virtual bool Connect(const char* address, int port) {
		return lowerLayer->Connect(address, port);
	}
};

/* Transport Layer (inc. concrete class definitions) */
class TransportLayer : public ProtocolLayer {
private:

public:
	TransportLayer() : ProtocolLayer(0) { }
	virtual ~TransportLayer() { }
	void ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int outLen) {
		memcpy(outMsg, inMsg, inLen);
	}
	bool DecomposeMessage(const char* inMsg, unsigned int inLen, char** outMsg, unsigned int* outLen) {
		*outMsg = (char*) inMsg;
		*outLen = inLen;
		return true;
	}
};

class UdpConnection : public TransportLayer {
private:
	int sock;
	struct sockaddr_in* addr;
    pthread_t receiveThread;

    void* ReceiveLoop();
    static void *ReceiveLoopHelper(void* context)
    {
        return ((UdpConnection*)context)->ReceiveLoop();
    }
public:
	UdpConnection() : TransportLayer() { sock = 0; addr = 0; receiveThread = 0; }
	virtual ~UdpConnection();

	bool Connect(const char* address, int port);
	bool Send(const char* msg, unsigned int len);
};


/* Session Layer */

class SessionLayer : public ProtocolLayer {
private:
	int sessId;
    pthread_mutex_t mutex;
    pthread_cond_t condition_var;
public:
	SessionLayer(ProtocolLayer* lower);
	virtual ~SessionLayer() { }
	int GetMessageDecorationLength(const char* inMsg, unsigned int inLen);
	void ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int outLen);
	bool DecomposeMessage(const char* inMsg, unsigned int inLen, char** outMsg, unsigned int* outLen);
	bool Connect(const char* address, int port);
	void MessageReceived(const char* msg, unsigned int len);
};

/* Presentation Layer */

class PresentationLayer : public ProtocolLayer {
private:

public:
	PresentationLayer(ProtocolLayer* lower) : ProtocolLayer(lower) {  }
	virtual ~PresentationLayer() { }
	int GetMessageDecorationLength(const char* inMsg, unsigned int inLen);
	void ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int outLen);
	bool DecomposeMessage(const char* inMsg, unsigned int inLen, char** outMsg, unsigned int* outLen);
};

/* Presentation Layer */

/* Presentation Layer */

class ApplicationLayer : public ProtocolLayer {
private:

public:
	ApplicationLayer(ProtocolLayer* lower) : ProtocolLayer(lower) {  }
	virtual ~ApplicationLayer() { }
	int GetMessageDecorationLength(const char* inMsg, unsigned int inLen);
	void ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int outLen);
	bool DecomposeMessage(const char* inMsg, unsigned int inLen, char** outMsg, unsigned int* outLen);
};

/* Presentation Layer */

} /* namespace ConnectionLayer */

#endif /* INCLUDE_CONNECTIONLAYER_H_ */
