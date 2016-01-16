/*
 * Lidar.h
 *
 *  Created on: 15.01.2016
 *      Author: Nicolaj Hoess
 */

#ifndef INCLUDE_SENSORS_LIDAR_H_
#define INCLUDE_SENSORS_LIDAR_H_

#include "I2C.h"

namespace FhvRobot {

class Lidar {
private:
	I2C* i2c;
public:
	Lidar(I2C* i2c);
	virtual ~Lidar();

	bool InitLidar();
	int ReadDistance();
};

} /* namespace FhvRobot */

#endif /* INCLUDE_SENSORS_LIDAR_H_ */
