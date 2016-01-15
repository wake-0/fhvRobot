/*
 * sensor_testsuite.cpp
 *
 *  Created on: 15.01.2016
 *      Author: Nicolaj Hoess
 */

#include "unistd.h"
#include "../../include/sensors/I2C.h"
#include "../../include/sensors/Lidar.h"

bool test_i2c();
bool test_lidar();

int main(void)
{
	unsigned int tests = 0;
	unsigned int tests_ok = 0;

	printf("Running testsuite\n");

	printf("Running Test: I2C API\n");
	tests_ok += (test_i2c()) ? 1 : 0;
	tests++;

	printf("Running Test: Lidar API\n");
	tests_ok += (test_lidar()) ? 1 : 0;
	tests++;

	for (volatile int i = 0; i < 0x0FFFF; i++);

	printf("Testsuite done.\n");
	printf("---------------\n");
	printf("Tests OK: %d\n", tests_ok);
	printf("Tests Failed: %d\n", tests - tests_ok);

	return 0;
}

bool test_i2c()
{
	printf("Opening I2C-2\n");
	FhvRobot::I2C i2c(I2C_2);
	printf("Setting slave to 0x68\n");
	i2c.SetSlave(0x68);
	printf("Reading value 0x00\n");
	char value = i2c.ReadByte(0x00);
	printf("Read value is %d\n", value);
	return true;
}

bool test_lidar()
{
	printf("Opening I2C-2\n");
	FhvRobot::I2C i2c(I2C_2);
	FhvRobot::Lidar lidar(&i2c);
	printf("Init lidar\n");
	if (!lidar.InitLidar()) {
		printf("Init lidar failed\n");
		return false;
	}
	printf("Instantiating lidar and reading 50 values\n");
	for (int i = 0; i < 100; i++) {
		printf("%d. Read distance=%d\n", (i+1), lidar.ReadDistance());
		usleep(1000 * 100);
	}
	return true;
}

