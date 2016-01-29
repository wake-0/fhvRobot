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
#include "sensors/Lidar.h"
#include "GPIO/GPIOManager.h"
#include "GPIO/GPIOConst.h"

#define MOTOR_LEFT				(1)
#define MOTOR_RIGHT				(2)
#define MOTOR_BOTH				(3)

namespace FhvRobot {

class Robot {
private:
	int session;
	int motorLeftValue;
	int motorRightValue;
	float factor;
    pthread_t motorControlThread;
    MPU9150* mpu;
    FusionFilter* filter;
    GPIO::GPIOManager* manager;
    Lidar* lidar;
    float calib[3];

    void* MotorControlLoop();
    static void *MotorControlLoopHelper(void* context)
    {
    	Debugger(VERBOSE) << "ReceiveLoopHelper calling private method\n";
        return ((Robot*)context)->MotorControlLoop();
    }
    bool GetOrientationPrecise(float* roll, float* pitch, float* yaw);
public:
	Robot(MPU9150* mpu, FusionFilter* filter, GPIO::GPIOManager* man, Lidar* lidar);
	virtual ~Robot();

	void setFactor(float value) {
		Debugger(INFO) << "Setting motor factor to " << value << "\n";
		factor = value;
	}
	float getFactor() { return factor; }

	bool MotorStop(bool forceAction);
	bool MotorLeft(int percent, bool forceAction);
	bool MotorRight(int percent, bool forceAction);

	bool GetOrientation(short* roll, short* pitch, short* yaw);
	bool GetOrientation_10(short* roll, short* pitch, short* yaw);

	void TriggerLED();
	bool IsLidarEnabled();
	int ReadLidar();

	int GetMotorRightValue() { return motorRightValue; }
	int GetMotorLeftValue() { return motorLeftValue; }
};

}

#endif /* ROBOT_H_ */
