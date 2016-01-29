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
#include "sensors/I2C.h"
#include "sensors/FusionFilter.h"
#include "sensors/MPU9150.h"
#include "sensors/Lidar.h"
#include "GPIO/GPIOManager.h"

namespace FhvRobot {

class Controller : ApplicationCallback {
private:
	Robot robot;
	ConnectionAPI* connection;
	GPIO::GPIOManager* gpioManager;
	bool running;
	char serverAddress[255];
	char* path;

	bool christKindle;
	bool blockForward;
	int lastDistance;
public:
	Controller(char* path, MPU9150* mpu, FusionFilter* filter, GPIO::GPIOManager* gp, Lidar* lidar);
	virtual ~Controller();

	void Init();
	bool Start(char* serverIp, char* name); // Non-Returning

	void MotorCommand(unsigned int motorNum, int motorSpeed);
	void CameraOn(char* host, int port);
	void CameraOff();
	void ForceDisconnect();
	void TriggerLED();

	Robot* getRobot() { return &robot; }

	void setBlockForward(bool value) { blockForward = value; }
	void setChristKindle(bool value) { christKindle = value; }
};

} /* namespace FhvRobot */

#endif /* SOURCE_IMPL_CONTROLLER_H_ */
