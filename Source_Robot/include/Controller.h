/*
 * Controller.h
 *
 *  Created on: 13.11.2015
 *      Author: Nicolaj Hoess
 */

#ifndef SOURCE_IMPL_CONTROLLER_H_
#define SOURCE_IMPL_CONTROLLER_H_

#include "ConnectionAPI.h"

namespace FhvRobot {

class Controller : ApplicationCallback {
public:
	Controller();
	virtual ~Controller();

	void Init();
	void Start(); // Non-Returning

	void MotorCommand(unsigned int motorNum, int motorSpeed);
	void CameraEnable(bool cameraEnable);
};

} /* namespace FhvRobot */

#endif /* SOURCE_IMPL_CONTROLLER_H_ */
