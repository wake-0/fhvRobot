/*
 * UdpConnection.h
 *
 *  Created on: 29.10.2015
 *      Author: Nicolaj Hoess
 */

#ifndef INCLUDE_CONNECTIONLAYER_H_
#define INCLUDE_CONNECTIONLAYER_H_

#include "stdlib.h"

using namespace std;

namespace FhvRobotProtocolStack {

class ProtocolLayerCallback {
public:
	ProtocolLayerCallback() { }
	virtual ~ProtocolLayerCallback() { }
	virtual void MessageReceived(char* msg, unsigned int len) = 0;
};

class ProtocolLayer : public ProtocolLayerCallback {
private:
	ProtocolLayerCallback* callback;
	ProtocolLayer* lowerLayer;

protected:
	virtual void ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int* outLen) = 0;

	/**
	 *  \brief This function decomposes a received message and returns true if the message is a valid message acc.
	 *  	   to the defined protocol, false otherwise. If the function returns false the outMsg and outLen must
	 *  	   not be used by the caller.
	 */
	virtual bool DecomposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int* outLen) = 0;
public:
	ProtocolLayer(ProtocolLayer* lower, ProtocolLayerCallback* cb) { lowerLayer = lower; callback = cb; }
	virtual ~ProtocolLayer() { lowerLayer = 0; callback = 0; }

	virtual bool Send(const char* msg, unsigned int len) {
		char* outMsg = NULL;
		unsigned int outLen = 0;
		ComposeMessage(msg, len, outMsg, &outLen);
		return lowerLayer->Send(outMsg, outLen);
	}

	virtual void MessageReceived(char* msg, unsigned int len) {
		char* outMsg = NULL;
		unsigned int outLen = 0;
		bool result = DecomposeMessage(msg, len, outMsg, &outLen);
		if (result) {
			callback->MessageReceived(outMsg, outLen);
		}
	}

	virtual bool Connect(const char* address, int port) {
		return lowerLayer->Connect(address, port);
	}
};

class TransportLayer : public ProtocolLayer {
private:

public:
	TransportLayer(ProtocolLayer* lower, ProtocolLayerCallback* cb) : ProtocolLayer(lower, cb) { }
	virtual ~TransportLayer() { }
	void ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int* outLen) {
		outMsg = (char*) inMsg;
		*outLen = inLen;
	}
	bool DecomposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int* outLen) {
		outMsg = (char*) inMsg;
		*outLen = inLen;
		return true;
	}
};

class UdpConnection : public TransportLayer {
private:
	int sock;
public:
	UdpConnection(ProtocolLayer* lower, ProtocolLayerCallback* cb) : TransportLayer(lower, cb) { sock = 0; }
	virtual ~UdpConnection();

	bool Connect(const char* address, int port);
	bool Send(const char* msg, unsigned int len);
};

} /* namespace ConnectionLayer */

#endif /* INCLUDE_CONNECTIONLAYER_H_ */
