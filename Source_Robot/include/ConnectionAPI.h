/*
 * ConnectionAPI.h
 *
 *  Created on: 13.11.2015
 *      Author: Nicolaj Hoess
 */

#ifndef INCLUDE_CONNECTIONAPI_H_
#define INCLUDE_CONNECTIONAPI_H_

#include "ConnectionLayer.h"

using namespace FhvRobotProtocolStack;

namespace FhvRobot {

class ApplicationCallback {
public:
	ApplicationCallback() { }
	virtual ~ApplicationCallback() { }
	virtual void MotorCommand(unsigned int motorNum, int motorSpeed) = 0;
	virtual void CameraOn(char* host, int port) = 0;
	virtual void CameraOff() = 0;
	virtual void ForceDisconnect() = 0;
};

class ConnectionAPI : public ProtocolLayerCallback {
private:
	ApplicationCallback* callback;
	FhvRobotProtocolStack::ProtocolLayer* connection;
	long lastMessageTime;
public:
	ConnectionAPI(ApplicationCallback* cb);
	virtual ~ConnectionAPI();

	void SetConnection(FhvRobotProtocolStack::ProtocolLayer* c);
	bool Connect(const char* robotName, const char* hostname, int port);
	void Disconnect() { connection->CloseConnection(); }
	bool SendHeartBeat();
	bool SendOrientation(short roll, short pitch, short yaw);

	void MessageReceived(const char* msg, unsigned int len);
	long GetLastMessageTime() { return lastMessageTime; }
};

} /* namespace FhvRobot */

#endif /* INCLUDE_CONNECTIONAPI_H_ */
