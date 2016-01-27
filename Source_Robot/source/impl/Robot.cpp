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

Robot::Robot(MPU9150* __mpu, FusionFilter* __filter, GPIO::GPIOManager* gp) {
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

	mpu = __mpu;
	if (mpu != NULL)
	{
		mpu->Sleep(false);
		mpu->GetCompassCalibration(calib);
	}
	filter = __filter;
	manager = gp;
	if (manager != NULL)
	{
		int pin = GPIO::GPIOConst::getInstance()->getGpioByKey("P8_11");
		manager->exportPin(pin);
		manager->setDirection(pin, GPIO::OUTPUT);
	}

	TriggerLED();
	usleep(100 * 1000);
	TriggerLED();
	usleep(100 * 1000);
	TriggerLED();
	usleep(100 * 1000);
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

bool Robot::GetOrientationPrecise(float* roll, float* pitch, float* yaw) {
    float accel_x = mpu->GetAccelerometerX() / 16384.0; // * G; // * A_GAIN;
    float accel_y = mpu->GetAccelerometerY() / 16384.0; // * G; // * A_GAIN;
    float accel_z = mpu->GetAccelerometerZ() / 16384.0; // * G; // * A_GAIN;

    float gyro_x = mpu->GetGyroscopeX() / 131.0;
    float gyro_y = mpu->GetGyroscopeY() / 131.0;
    float gyro_z = mpu->GetGyroscopeZ() / 131.0;

    short comp_x, comp_y, comp_z;
    float mx, my, mz;
    if (mpu->GetCompass(&comp_x, &comp_y, &comp_z))
    {
        mx = (float)comp_x*mpu->getMRes()*calib[0];
        my = (float)comp_y*mpu->getMRes()*calib[1];
        mz = (float)comp_z*mpu->getMRes()*calib[2];

        filter->UpdateValues(accel_x, accel_y, accel_z, gyro_x*PI/180.0f, gyro_y*PI/180.0f, gyro_z*PI/180.0f, my, mx, mz);
        float r, p, y;
        filter->ReadValues(&r, &p, &y);

    	Debugger(VERBOSE) << "Got orientation values: roll=" << r << ", pitch=" << p << ", yaw=" << y << "\n";

    	/*
        if (r < 0) r=(360+r);
        if (p < 0) p=(360+p);
        */
        if (y < 0) y=(360+y);

        *roll = (r);
        *pitch = (p);
        *yaw = (360 - y);
        return true;
    }
    return false;
}

bool Robot::GetOrientation(short* roll, short* pitch, short* yaw) {
    float r = 0;
    float p = 0;
    float y = 0;
    if (this->GetOrientationPrecise(&r, &p, &y))
    {
        *roll = static_cast<float>(r);
        *pitch = static_cast<float>(p);
        *yaw = static_cast<float>(y);
        return true;
    }
    return false;
}

bool Robot::GetOrientation_10(short* roll, short* pitch, short* yaw) {
    float r = 0;
    float p = 0;
    float y = 0;
    if (this->GetOrientationPrecise(&r, &p, &y))
    {
        *roll = static_cast<float>(r * 10);
        *pitch = static_cast<float>(p * 10);
        *yaw = static_cast<float>(y * 10);
        return true;
    }
    return false;
}

void Robot::TriggerLED()
{
	int pin = GPIO::GPIOConst::getInstance()->getGpioByKey("P8_11");
	manager->setValue(pin, GPIO::HIGH);
	usleep(5 * 1000);
	manager->setValue(pin, GPIO::LOW);
}

} /* namespace FhvRobot */
