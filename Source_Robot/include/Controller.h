/*
 * Controller.h
 *
 *  Created on: 13.11.2015
 *      Author: Nicolaj Hoess
 */

#ifndef SOURCE_IMPL_CONTROLLER_H_
#define SOURCE_IMPL_CONTROLLER_H_

#include "ConnectionAPI.h"
#include "Robot.h"

namespace FhvRobot {

class Controller : ApplicationCallback {
private:
	Robot robot;
	ConnectionAPI* connection;
	bool running;
	char serverAddress[255];
public:
	Controller();
	virtual ~Controller();

	void Init();
	bool Start(char* serverIp); // Non-Returning

	void MotorCommand(unsigned int motorNum, int motorSpeed);
	void CameraOn(char* host, int port);
	void CameraOff();
	void ForceDisconnect();
};

} /* namespace FhvRobot */

#endif /* SOURCE_IMPL_CONTROLLER_H_ */
