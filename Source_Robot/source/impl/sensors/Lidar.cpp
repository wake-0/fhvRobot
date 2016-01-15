/*
 * Lidar.cpp
 *
 *  Created on: 15.01.2016
 *      Author: Nicolaj Hoess
 */

#include "../../../include/sensors/Lidar.h"
#include <stdio.h>
#include <unistd.h>

namespace FhvRobot {

Lidar::Lidar(I2C* __i2c) {
	i2c = __i2c;
}

Lidar::~Lidar() {
	i2c = NULL;
}

bool Lidar::InitLidar()
{
	bool result = true;
	result &= i2c->SetSlave(0x62);
	result &= i2c->WriteByte(0x45, 0x04);
	usleep(1000);
	result &= i2c->WriteByte(0x04, 0x21);
	usleep(1000);
	result &= i2c->WriteByte(0x11, 0xff);
	usleep(1000);
	result &= i2c->WriteByte(0x00, 0x04);
	usleep(1000);
	return result;
}

int Lidar::ReadDistance()
{
	i2c->SetSlave(0x62);
	char b1 = i2c->ReadByte(0x8f);
	char b2 = i2c->ReadByte(0x90);
	return (b1 << 8) + b2;
}

} /* namespace FhvRobot */
