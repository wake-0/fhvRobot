/*
 * Robot.h
 *
 *  Created on: 22.10.2015
 *      Author: Nicolaj Hoess
 */

#ifndef ROBOT_H_
#define ROBOT_H_

#define MOTOR_LEFT				(1)
#define MOTOR_RIGHT				(2)
#define MOTOR_BOTH				(3)

namespace FhvRobot {

class Robot {
private:
	int session;
public:
	Robot();
	virtual ~Robot();

	bool MotorStop();
	bool MotorLeft(int percent);
	bool MotorRight(int percent);

};

}

#endif /* ROBOT_H_ */
