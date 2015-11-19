/*
 * ConnectionAPI.cpp
 *
 *  Created on: 13.11.2015
 *      Author: Nicolaj Hoess
 */

#include "../../include/ConnectionAPI.h"
#include <stdexcept>

#define TYPE_BYTE					(0b00000000)
#define COMMAND_CONNECT				(0b00000000)

namespace FhvRobot {

ConnectionAPI::ConnectionAPI(ApplicationCallback* cb) {
	if (cb == NULL)
	{
		Debugger(WARNING) << "ConnectionAPI not fully working (no callback specified)\n";
		//throw std::invalid_argument("ApplicatoinCallback must not be NULL.");
	}
	connection = NULL;
	callback = cb;
}

ConnectionAPI::~ConnectionAPI() {
	callback = NULL;
}

void ConnectionAPI::SetConnection(FhvRobotProtocolStack::ProtocolLayer* c)
{
	connection = c;
}

void ConnectionAPI::MessageReceived(const char* msg, unsigned int len)
{


}

bool ConnectionAPI::Connect(const char* robotName, const char* hostname, int port)
{
	if (connection == NULL)
	{
		Debugger(ERROR) << "Cannot connect: No Connection set to API\n";
		return false;
	}
	size_t robotNameLen = strlen(robotName);
	if (robotNameLen <= 0) {
		Debugger(ERROR) << "Tried to connect with empty robot name\n";
		return false;
	}
	Debugger(VERBOSE) << "Connecting to " << hostname << ":" << port << "\n";
	bool result = connection->Connect(hostname, port);
	Debugger(VERBOSE) << "Connection result=" << result << "\n";

	if (result == true) {
		// New Application message to actually connect to the server
		Debugger(VERBOSE) << "Sending connect message\n";
		char* msg = (char*) malloc(sizeof(char) * robotNameLen + 1);
		if (msg == NULL) {
			Debugger(ERROR) << "Could not allocate memory for sending connect message\n";
			return false;
		}

		msg[0] = COMMAND_CONNECT;
		memcpy(&msg[1], robotName, strlen(hostname));
		result = result && connection->Send(msg, robotNameLen + 2);
		free(msg);

		return result;
	}
	return false;
}

bool ConnectionAPI::SendHeartBeat()
{

	return true;
}

} /* namespace FhvRobot */
