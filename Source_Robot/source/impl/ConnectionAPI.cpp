/*
 * ConnectionAPI.cpp
 *
 *  Created on: 13.11.2015
 *      Author: Nicolaj Hoess
 */

#include "../../include/ConnectionAPI.h"
#include "../../include/Robot.h"

#include <stdexcept>

#define TYPE_BYTE					(0b00000000)
#define COMMAND_REGISTER			(0b00000001)
#define COMMAND_HEARTBEAT			(0b00000000)

namespace FhvRobot {

static int getMotorValue(signed char command_value);

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

	int command = msg[0];

	// Parse message
	if (command == 10 || command == 11)
	{
		// Right motor == 10
		// Left motor == 11
		callback->MotorCommand((command == 11) ? MOTOR_LEFT : MOTOR_RIGHT, getMotorValue(msg[2]));
	}
	else if (command == 12)
	{
		// Both motors
		callback->MotorCommand(MOTOR_BOTH, getMotorValue(msg[2]));
	}
}

bool ConnectionAPI::Connect(const char* robotName, const char* hostname, int port)
{
	if (connection == NULL)
	{
		Debugger(ERROR) << "Cannot connect: No Connection set. Use SetConnection(..)\n";
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
		char* msg = (char*) malloc(sizeof(char) * robotNameLen + 2);
		if (msg == NULL) {
			Debugger(ERROR) << "Could not allocate memory for sending connect message\n";
			return false;
		}

		msg[0] = COMMAND_REGISTER;
		msg[1] = strlen(robotName);
		memcpy(&msg[2], robotName, msg[1]);
		result = result && connection->Send(msg, robotNameLen + 2);
		free(msg);

		return result;
	}
	return false;
}

bool ConnectionAPI::SendHeartBeat()
{
	char msg[2];
	msg[0] = COMMAND_HEARTBEAT;
	msg[1] = 0;

	return connection->Send(msg, 2);
}

int getMotorValue(signed char command_value)
{
	signed char value = command_value;
	value = (value > 100) ? 100 : value;
	value = (value < -100) ? -100 : value;
	return (int) value;
}

} /* namespace FhvRobot */
