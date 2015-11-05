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

	struct sockaddr_in addr;
	int ret;

	memset(&addr, 0, sizeof(addr));

	addr.sin_family = AF_INET;
	addr.sin_addr.s_addr = inet_addr(address);
	addr.sin_port = htons(port);

	ret = connect(sock, (struct sockaddr *) &addr, sizeof(addr));

	Debugger(VERBOSE) << "Return value from connection: " << ret << "\n";

	if (ret < 0)
	{
		Debugger(ERROR) << "Connection failed\n";
		return false;
	}

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

} /* namespace ConnectionLayer */
