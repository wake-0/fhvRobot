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
	unsigned char ReadByte(char byte);
	unsigned char ReadByte(char devAddress, char byte);
	void ReadByte(char devAddress, char byte, char* buf);
	bool WriteByte(char byte, char value);
	bool WriteByte(char devAddress, char byte, char value);

	char ReadBits(char devAddr, char regAddr, char bitStart, char length, char *data);
	bool WriteBits(char devAddr, char regAddr, char bitStart, char length, char data);

	char ReadBit(char devAddr, char regAddr, char bitNum, char *data);
	bool WriteBit(char devAddr, char regAddr, char bitNum, char data);

	bool WriteWord(char devAddr, char regAddr, short data);
};

} /* namespace FhvRobot */

#endif /* INCLUDE_SENSORS_I2C_H_ */
