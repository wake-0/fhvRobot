/*
 * Robot.h
 *
 *  Created on: 22.10.2015
 *      Author: Nicolaj Hoess
 */

#ifndef ROBOT_H_
#define ROBOT_H_

#include "Debugger.h"

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

    void* MotorControlLoop();
    static void *MotorControlLoopHelper(void* context)
    {
    	Debugger(VERBOSE) << "ReceiveLoopHelper calling private method\n";
        return ((Robot*)context)->MotorControlLoop();
    }
public:
	Robot();
	virtual ~Robot();

	bool MotorStop(bool forceAction);
	bool MotorLeft(int percent, bool forceAction);
	bool MotorRight(int percent, bool forceAction);
};

}

#endif /* ROBOT_H_ */
