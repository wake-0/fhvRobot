/*
 * UdpConnection.cpp
 *
 *  Created on: 29.10.2015
 *      Author: Nicolaj Hoess
 */

#include "../../include/Debugger.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/time.h>
#include "../../include/ConnectionLayer.h"

namespace FhvRobotProtocolStack {

/* Implementation of UDP Transport Layer */
UdpConnection::~UdpConnection() {
	sock = 0;
}

bool UdpConnection::Connect(const char* address, int port) {
	Debugger(INFO) << "Connecting to " << address << " at " << port << " (UDP)\n";

	memset(&addr, 0, sizeof(struct sockaddr_in));

	addr->sin_family = AF_INET;
	addr->sin_port = htons(port);
	//addr.sin_addr.s_addr = inet_addr(address);
	if (inet_aton(address, &addr->sin_addr)==0)
	{
		Debugger(ERROR) << "Invalid server address given\n";
		return false;
	}

	sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if (sock == -1)
	{
		Debugger(ERROR) << "Failed to create socket\n";
		return false;
	}

	// NOTE: This would be TCP/IP
	//ret = connect(sock, (struct sockaddr *) &addr, sizeof(addr));

	// Start receive thread
	Debugger(VERBOSE) << "Starting receive thread for UDP connection\n";
	int res = pthread_create(&receiveThread, NULL, &UdpConnection::ReceiveLoopHelper, this);
	if (res == 0)
	{
		Debugger(ERROR) << "Error creating receive thread\n";
		return false;
	}
	Debugger(VERBOSE) << "Receive thread started succesfully\n";
	return true;
}

bool UdpConnection::Send(const char* msg, unsigned int len) {
	Debugger(VERBOSE) << "Sending message with len=" << len << " (UDP)\n";

	int ret = send(sock, msg, len, 0);

	if (ret < 0) {
		Debugger(ERROR) << "Message failed to send (UDP)\n";
		return false;
	}
	return true;
}

void* UdpConnection::ReceiveLoop()
{
	int res = 0;
	char buf[512];
	struct sockaddr sender;
	socklen_t len = sizeof(sender);
	while (1 /* endless */)
	{
		res = recvfrom(sock, buf, 512, 0, &sender, &len);
		if (res == -1)
		{
			Debugger(ERROR) << "Udp Connection recvfrom(..) returned -1\n";
		}
		else
		{
			Debugger(INFO) << "Message received with len=" << res << "\n";
			callback->MessageReceived(buf, res);
		}
	}
	return NULL;
}



/* Implementation of Session Layer */

#define SESSION_LAYER_EMPTY_MASK					(0b00000000)
#define SESSION_LAYER_REQUEST_BIT_MASK				(0b10000000)
#define SESSION_LAYER_VERSION_MASK					(0b00000011)
#define SESSION_REQUEST_TIMEOUT_S					(3)


SessionLayer::SessionLayer(ProtocolLayer* lower) : ProtocolLayer(lower)
{
	 sessId = 0;
	 pthread_mutex_init(&mutex, NULL);
	 pthread_cond_init(&condition_var, NULL);
}
int SessionLayer::GetMessageDecorationLength(const char* inMsg, unsigned int inLen)
{
	return 2;
}

void SessionLayer::ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int outLen)
{
	char* buf = outMsg;
	Debugger(VERBOSE) << "Setting session id\n";

	// Add session id as prefix
	*(buf)			= (SESSION_LAYER_EMPTY_MASK);
	//*(buf) 		= (sessId & 0xFF000000 >> 24);
	//*(buf + 1) 	= (sessId & 0x00FF0000 >> 16);
	//*(buf + 2) 	= (sessId & 0x0000FF00 >> 8);
	*(buf + 1) 		= (sessId & 0x000000FF >> 0);

	Debugger(VERBOSE) << "Session id set\n";
	// Add original message
	Debugger(VERBOSE) << "Copying original message\n";
	memcpy((buf + 2), inMsg, inLen);
	Debugger(VERBOSE) << "Original message copied\n";
}

bool SessionLayer::DecomposeMessage(const char* inMsg, unsigned int inLen, char** outMsg, unsigned int* outLen)
{
	if (inLen < 2) {
		Debugger(WARNING) << "Session layer got invalid message (len < 2)\n";
		return false;
	}
	*outMsg = (char*) (inMsg + 2);
	*outLen = inLen - 2;
	return true;
}

bool SessionLayer::Connect(const char* address, int port)
{
	sessId = 0;
	bool result = lowerLayer->Connect(address, port);
	if (result == true)
	{
		// Try to get a session id from the connected device
		Debugger(VERBOSE) << "Session layer connected with lower layer...\n";
		Debugger(VERBOSE) << "No trying to get a session id";

		char msg[2] = { SESSION_LAYER_REQUEST_BIT_MASK , 0 };
		result = Send(msg, 2);
		if (result == true)
		{
			if (sessId == 0)
			{
				pthread_mutex_lock(&mutex);
				pthread_cond_wait(&condition_var, &mutex);
				pthread_mutex_unlock(&mutex);
			}
			if (sessId != 0)
			{
				return true;
			}
		}
	}
	return false;
}

void SessionLayer::MessageReceived(const char* msg, unsigned int len)
{
	// Check if this is the answer for our session request
	if (len == 2 && msg[0] == SESSION_LAYER_EMPTY_MASK)
	{
		sessId = msg[1];
		pthread_cond_broadcast(&condition_var);
		return;
	}
	ProtocolLayer::MessageReceived(msg, len);
}


/* PresentationLayer */

#define PRESENTATION_LAYER_EMPTY_FLAGS			(0b00000000)

int PresentationLayer::GetMessageDecorationLength(const char* inMsg, unsigned int inLen)
{
	return 1;
}

void PresentationLayer::ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int outLen)
{
	/* THIS CODE IS COMMENTED BY INTENTION as it may be used at a later point
	 * The following code would append a XOR-checksum to the message
	memcpy(outMsg, inMsg, inLen);
	char checksum = 0;
	for (unsigned int i = 0; i < inLen; i++)
	{
		checksum ^= *(outMsg + i);
	}
	*(outMsg + outLen - 1) = checksum;
	*/
	outMsg[0] = PRESENTATION_LAYER_EMPTY_FLAGS;
	memcpy(&outMsg[1], inMsg, inLen);
}

bool PresentationLayer::DecomposeMessage(const char* inMsg, unsigned int inLen, char** outMsg, unsigned int* outLen)
{
	/* THIS CODE IS COMMENTED BY INTENTION as it may be used at a later point
	 * The following code would append a XOR-checksum to the message
	// Check checksum
	char checksum = 0;
	for (unsigned int i = 0; i < inLen - 1; i++)
	{
		checksum ^= *(inMsg + i);
	}
	if (**(outMsg + inLen - 1) != checksum)
	{
		return false;
	}
	*outMsg = (char*) inMsg;
	*outLen -= 1;
	return true;
	*/

	if (inMsg[0] != PRESENTATION_LAYER_EMPTY_FLAGS)
	{
		return false;
	}
	*outMsg = (char*) &inMsg[1];
	*outLen = inLen - 1;
	return true;
}


/* Application Layer */

#define APPLICATION_LAYER_EMPTY_FLAGS			(0b00000000)

int ApplicationLayer::GetMessageDecorationLength(const char* inMsg, unsigned int inLen)
{
	return 1;
}

void ApplicationLayer::ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int outLen)
{
	outMsg[0] = APPLICATION_LAYER_EMPTY_FLAGS;
	memcpy(&outMsg[1], inMsg, inLen);
}

bool ApplicationLayer::DecomposeMessage(const char* inMsg, unsigned int inLen, char** outMsg, unsigned int* outLen)
{
	if (inMsg[0] != APPLICATION_LAYER_EMPTY_FLAGS)
	{
		return false;
	}
	*outMsg = (char*) &inMsg[1];
	*outLen = inLen - 1;
	return true;
}

} /* namespace ConnectionLayer */
