/*
 * I2C.h
 *
 *  Created on: 15.01.2016
 *      Author: Nicolaj Hoess
 */

#ifndef INCLUDE_SENSORS_I2C_H_
#define INCLUDE_SENSORS_I2C_H_

#include <stdio.h>
#include "../Debugger.h"

#define I2C_2					(1)
#define I2C_1					(0)
typedef unsigned int tI2CBus;

namespace FhvRobot {

class I2C {
private:
	int file;
public:
	I2C(tI2CBus bus);
	virtual ~I2C();

	bool SetSlave(char slave_address);
	char ReadByte(char byte);
	bool WriteByte(char byte, char value);
};

} /* namespace FhvRobot */

#endif /* INCLUDE_SENSORS_I2C_H_ */
