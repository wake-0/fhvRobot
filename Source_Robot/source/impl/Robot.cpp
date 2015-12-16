/*
 * Robot.cpp
 *
 *  Created on: 22.10.2015
 *      Author: Nicolaj Hoess
 */

#include "../../include/Robot.h"
#include "../../source/dmcc_lib/dmcc.h"
#include <stdio.h>
#include <pthread.h>
#include <unistd.h>

#define MOTOR_LEFT_POLARITY		(1)
#define MOTOR_RIGHT_POLARITY	(-1)
#define SPEED_TO_PWM(x)		(x * 100)

#define MOTOR_CONTROL_SLEEP_TIME_US		(10000)

namespace FhvRobot {

Robot::Robot() {
	// Open session of DMCC library
	session = DMCCstart(0);
	if (session < 0)
	{
		// TODO Throw exception
	}
	motorLeftValue = 0;
	motorRightValue = 0;

	Debugger(VERBOSE) << "Starting motor control thread\n";
	int res = pthread_create(&motorControlThread, NULL, Robot::MotorControlLoopHelper, this);

	if (res < 0)
	{
		Debugger(ERROR) << "Error creating receive thread. Return value is " << res << "\n";
	}
	else
	{
		Debugger(VERBOSE) << "Receive thread started succesfully\n";
	}
}

Robot::~Robot() {
	DMCCend(session);
}

void* Robot::MotorControlLoop() {
	Debugger(VERBOSE) << "Hello, this is the motor control thread\n";
	while (1)
	{
		if (MOTOR_LEFT == 1)
			setAllMotorPower(session, SPEED_TO_PWM(motorLeftValue) * MOTOR_LEFT_POLARITY, SPEED_TO_PWM(motorRightValue) * MOTOR_RIGHT_POLARITY);
		else
			setAllMotorPower(session, SPEED_TO_PWM(motorRightValue) * MOTOR_RIGHT_POLARITY, SPEED_TO_PWM(motorLeftValue) * MOTOR_LEFT_POLARITY);

		usleep(MOTOR_CONTROL_SLEEP_TIME_US);
	}
	return NULL;
}

bool Robot::MotorStop(bool forceAction) {
	if (forceAction)
	{
		setAllMotorPower(session, 0, 0);
		return true;
	}
	motorLeftValue = 0;
	motorRightValue = 0;
	return true;
}

bool Robot::MotorLeft(int percent, bool forceAction) {
	if (forceAction)
	{
		setMotorPower(session, MOTOR_LEFT, SPEED_TO_PWM(percent) * MOTOR_LEFT_POLARITY);
		return !(getMotorDir(session, MOTOR_LEFT) == -1);
	}
	motorLeftValue = percent;
	return true;
}

bool Robot::MotorRight(int percent, bool forceAction) {
	if (forceAction)
	{
		setMotorPower(session, MOTOR_RIGHT, SPEED_TO_PWM(percent) * MOTOR_RIGHT_POLARITY);
		return !(getMotorDir(session, MOTOR_RIGHT) == -1);
	}
	motorRightValue = percent;
	return true;
}

} /* namespace FhvRobot */
