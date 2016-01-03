/*
 * UdpConnection.cpp
 *
 *  Created on: 29.10.2015
 *      Author: Nicolaj Hoess
 */

#include "../../include/Debugger.h"

#include <stdio.h>
#include <string.h>
#include <sys/time.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include "../../include/ConnectionLayer.h"

namespace FhvRobotProtocolStack {

/* Implementation of UDP Transport Layer */
UdpConnection::~UdpConnection() {
	sock = 0;
}

bool UdpConnection::Connect(const char* address, int port) {
	Debugger(INFO) << "Connecting to " << address << " at " << port << " (UDP)\n";

	struct sockaddr_in temp;
	if (inet_pton(AF_INET, address, &(temp.sin_addr)) == 0)
	{
		Debugger(ERROR) << "Given IP address: " << address << " is invalid\n";
		return false;
	}
	memset(&addr, 0, sizeof(struct sockaddr_in));
	addr.sin_family = AF_INET;
	Debugger(VERBOSE) << "Zeroed addr struct\n";

	addr.sin_port = htons(port);
	//addr.sin_addr.s_addr = inet_addr(address);

	Debugger(VERBOSE) << "Trying to resolve hostname\n";
	if (inet_aton(address, &addr.sin_addr)==0)
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

	Debugger(VERBOSE) << "Setting in addr\n";
	memset((char *) &in_addr, 0, sizeof(in_addr));
	in_addr.sin_family = AF_INET;
	in_addr.sin_port = htons(port);
	in_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	if (bind(sock, (const sockaddr*) &in_addr, sizeof(struct sockaddr_in)) == -1)
	{
		Debugger(ERROR) << "Failed to bind socket in UdpConnection\n";
		return false;
	}

	// NOTE: This would be TCP/IP
	//ret = connect(sock, (struct sockaddr *) &addr, sizeof(addr));

	// Start receive thread
	Debugger(VERBOSE) << "Starting receive thread for UDP connection\n";
	int res = pthread_create(&receiveThread, NULL, UdpConnection::ReceiveLoopHelper, this);

	if (res < 0)
	{
		Debugger(ERROR) << "Error creating receive thread. Return value is " << res << "\n";
		return false;
	}
	Debugger(VERBOSE) << "Receive thread started succesfully\n";
	return true;
}

bool UdpConnection::Send(const char* msg, unsigned int len) {
	Debugger(VERBOSE) << "Sending message with len=" << len << " (UDP)\n";

	int ret = sendto(sock, msg, len, 0, (const sockaddr*)&addr, sizeof(struct sockaddr_in));

	if (ret < 0) {
		Debugger(ERROR) << "Message failed to send (UDP): errno=" << errno << "\n";
		return false;
	}
	Debugger(VERBOSE) << "Message sent correctly in Transport layer. ret=" << ret << "\n";
	return true;
}

bool UdpConnection::CloseConnection() {
	close(sock);
	pthread_cancel(receiveThread);
	return true;
}

void* UdpConnection::ReceiveLoop()
{
	Debugger(INFO) << "Hello I'm the receive thread.\n";
	Debugger(INFO) << "My callback is " << callback << "\n";
	int res = 0;
	char buf[4096];
	struct sockaddr_in sender;
	socklen_t len = sizeof(sender);
	while (1 /* endless */)
	{
		res = recvfrom(sock, buf, 512, 0, (struct sockaddr*)&sender, &len);
		if (res == -1)
		{
			Debugger(ERROR) << "Udp Connection recvfrom(..) returned -1: errno=" << errno << "\n";
			Debugger(ERROR) << "Closing connection and stopping receive thread\n";
			shutdown(sock, 0);
			break;
		}
		else
		{
			Debugger(INFO) << "Message received with len=" << res << "\n";
			Debugger(VERBOSE) << "Message raw data: \n";
			for (int i = 0; i < res; i++)
			{
				Debugger(VERBOSE) << buf[i] << " ";
			}
			Debugger(VERBOSE) << "\n";

			if (ProtocolLayer::callback != NULL)
			{
				Debugger(VERBOSE) << "Calling callback at address " << ProtocolLayer::callback << "\n";
				ProtocolLayer::callback->MessageReceived(buf, res);
				Debugger(VERBOSE) << "Callback called\n";
			}
			else
			{
				Debugger(WARNING) << "Could not call callback (0 address)\n";
			}
		}
	}
	return NULL;
}



/* Implementation of Session Layer */

#define SESSION_LAYER_EMPTY_MASK					(0b00000000)
#define SESSION_LAYER_REQUEST_BIT_MASK				(0b00000001)
#define SESSION_LAYER_VERSION_MASK					(0b00000011)
#define SESSION_LAYER_SESSION_ACCEPT_MASK			(0b00000001)
#define SESSION_LAYER_SESSION_DECLINE_MASK			(0b00000000)
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

void SessionLayer::ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int outLen, tPacketFlags flags)
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
	Debugger(VERBOSE) << "SessionLayer decomposing message\n";
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
		Debugger(VERBOSE) << "Now trying to get a session id";

		char msg[2] = { SESSION_LAYER_REQUEST_BIT_MASK , 0 };
		result = lowerLayer->Send(msg, 2);
		if (result == true)
		{
			if (sessId == 0)
			{
				struct timespec timeToWait;
				struct timeval tv;

				gettimeofday(&tv,NULL);

				int timeInMs = 5000;
				timeToWait.tv_sec = time(NULL) + timeInMs / 1000;
				timeToWait.tv_nsec = tv.tv_usec * 1000 + 1000 * 1000 * (timeInMs % 1000);
				timeToWait.tv_sec += timeToWait.tv_nsec / (1000 * 1000 * 1000);
				timeToWait.tv_nsec %= (1000 * 1000 * 1000);

				Debugger(VERBOSE) << "Session is blocked until response\n";
				pthread_mutex_lock(&mutex);
				pthread_cond_timedwait(&condition_var, &mutex, &timeToWait);
				pthread_mutex_unlock(&mutex);
				Debugger(VERBOSE) << "Session is unblocked\n";
			}
			if (sessId != 0)
			{
				Debugger(INFO) << "Got session id " << sessId << "\n";
				return true;
			}
		}
	}
	return false;
}

void SessionLayer::MessageReceived(const char* msg, unsigned int len)
{
	// Check if this is the answer for our session request
	if (len == 2 && msg[0] == SESSION_LAYER_SESSION_ACCEPT_MASK && msg[1] != 0)
	{
		// Accepted session
		sessId = msg[1];
		pthread_cond_broadcast(&condition_var);
		return;
	}
	else if (len == 2 && msg[0] == SESSION_LAYER_SESSION_DECLINE_MASK && msg[1] == 0)
	{
		// Declined session
		sessId = 0;
		pthread_cond_broadcast(&condition_var);
	}
	else if (len >= 2 && msg[1] == sessId && sessId != 0)
	{
		ProtocolLayer::MessageReceived(msg, len);
	}
}

bool SessionLayer::CloseConnection() {
	bool res = ProtocolLayer::CloseConnection();
	sessId = 0;
	return res;
}


/* PresentationLayer */

#define PRESENTATION_LAYER_EMPTY_FLAGS			(0b00000000)

int PresentationLayer::GetMessageDecorationLength(const char* inMsg, unsigned int inLen)
{
	return 1;
}

void PresentationLayer::ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int outLen, tPacketFlags flags)
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
	Debugger(VERBOSE) << "PresentationLayer decomposing message\n";
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

void ApplicationLayer::ComposeMessage(const char* inMsg, unsigned int inLen, char* outMsg, unsigned int outLen, tPacketFlags flags)
{
	outMsg[0] = flags | APPLICATION_LAYER_EMPTY_FLAGS;
	memcpy(&outMsg[1], inMsg, inLen);
}

bool ApplicationLayer::DecomposeMessage(const char* inMsg, unsigned int inLen, char** outMsg, unsigned int* outLen)
{
	Debugger(VERBOSE) << "ApplicationLayer decomposing message\n";
	if (inMsg[0] != APPLICATION_LAYER_EMPTY_FLAGS)
	{
		return false;
	}
	*outMsg = (char*) &inMsg[1];
	*outLen = inLen - 1;
	return true;
}

} /* namespace ConnectionLayer */
