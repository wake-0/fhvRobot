/*
 * Robot.h
 *
 *  Created on: 22.10.2015
 *      Author: Nicolaj Hoess
 */

#ifndef ROBOT_H_
#define ROBOT_H_

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
