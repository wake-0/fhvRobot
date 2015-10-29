/*
 * Robot.cpp
 *
 *  Created on: 22.10.2015
 *      Author: Nicolaj Hoess
 */

#include "../../include/Robot.h"
#include "../../source/dmcc_lib/dmcc.h"
#include <stdio.h>
#define MOTOR_LEFT				(1)
#define MOTOR_RIGHT				(2)
#define MOTOR_LEFT_POLARITY		(1)
#define MOTOR_RIGHT_POLARITY	(-1)
#define SPEED_TO_PWM(x)		(x * 100)


namespace FhvRobot {

Robot::Robot() {
	// Open session of DMCC library
	session = DMCCstart(0);
	if (session < 0)
	{
		// TODO Throw exception
	}
}

Robot::~Robot() {
	DMCCend(session);
}

bool Robot::MotorStop() {

	setAllMotorPower(session, 0, 0);

	return true;
}

bool Robot::MotorLeft(int percent) {
	setMotorPower(session, MOTOR_LEFT, SPEED_TO_PWM(percent) * MOTOR_LEFT_POLARITY);
	return !(getMotorDir(session, MOTOR_LEFT) == -1);
}

bool Robot::MotorRight(int percent) {
	setMotorPower(session, MOTOR_RIGHT, SPEED_TO_PWM(percent) * MOTOR_RIGHT_POLARITY);
	return !(getMotorDir(session, MOTOR_RIGHT) == -1);
}

} /* namespace FhvRobot */
