/*
 * Robot.h
 *
 *  Created on: 22.10.2015
 *      Author: Nicolaj Hoess
 */

#ifndef ROBOT_H_
#define ROBOT_H_

#include "Debugger.h"
#include "sensors/MPU9150.h"
#include "sensors/FusionFilter.h"

#define MOTOR_LEFT				(1)
#define MOTOR_RIGHT				(2)
#define MOTOR_BOTH				(3)

namespace FhvRobot {

class Robot {
private:
	int session;
	int motorLeftValue;
	int motorRightValue;
    pthread_t motorControlThread;
    MPU9150* mpu;
    FusionFilter* filter;
    float calib[3];

    void* MotorControlLoop();
    static void *MotorControlLoopHelper(void* context)
    {
    	Debugger(VERBOSE) << "ReceiveLoopHelper calling private method\n";
        return ((Robot*)context)->MotorControlLoop();
    }
public:
	Robot(MPU9150* mpu, FusionFilter* filter);
	virtual ~Robot();

	bool MotorStop(bool forceAction);
	bool MotorLeft(int percent, bool forceAction);
	bool MotorRight(int percent, bool forceAction);

	bool GetOrientation(short* roll, short* pitch, short* yaw);
};

}

#endif /* ROBOT_H_ */
