/*
 * sensor_testsuite.cpp
 *
 *  Created on: 15.01.2016
 *      Author: Nicolaj Hoess
 */

#include "unistd.h"
#include "../../include/sensors/I2C.h"
#include "../../include/sensors/Lidar.h"
#include "../../include/sensors/MPU9150.h"
#include "../../include/sensors/FusionFilter.h"

bool test_i2c();
bool test_lidar();
bool test_mpu9150();

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

	printf("Running Test: MPU9150 API\n");
	tests_ok += (test_mpu9150()) ? 1 : 0;
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

bool test_mpu9150()
{
	printf("Opening I2C-2\n");
	FhvRobot::I2C i2c(I2C_2);
	printf("Init MPU9150\n");
	FhvRobot::MPU9150 m(&i2c);

	FhvRobot::FusionFilter filter;

	printf("Checking connection (whoami)\n");
	printf("%d\n", m.WhoAmI());

	m.Sleep(false);
	float calib[3] = { 0 };
	m.GetCompassCalibration(calib);
	printf("Reading 1000 values\n");
    short comp_x, comp_y, comp_z;
    float mx, my, mz;
    float magbias[3] = { 0 };
	for (int i = 0; i < 1000; i++) {
        if( m.DataReady() )
        {
        	int temp = m.GetTemp();
        	double t = (temp + 11900.0) / 340.0;
        	(void)(t);
      //  	printf("%d. Temperature is \t\t\t\t %f°C\n", (i+1), t);

            float accel_x = m.GetAccelerometerX() / 16384.0; // * G; // * A_GAIN;
            float accel_y = m.GetAccelerometerY() / 16384.0; // * G; // * A_GAIN;
            float accel_z = m.GetAccelerometerZ() / 16384.0; // * G; // * A_GAIN;
     //       printf("%d. Accel data:\n x=%f | y=%f | z=%f\n", (i+1), accel_x, accel_y, accel_z);

            float gyro_x = m.GetGyroscopeX() / 131.0;
            float gyro_y = m.GetGyroscopeY() / 131.0;
            float gyro_z = m.GetGyroscopeZ() / 131.0;
     //       printf("%d. Gyro data:\n x=%f | y=%f | z=%f\n", (i+1), gyro_x, gyro_y, gyro_z);

            //magbias[0] = -5.;   // User environmental x-axis correction in milliGauss
            //magbias[1] = -95.;  // User environmental y-axis correction in milliGauss
            //magbias[2] = -260.; // User environmental z-axis correction in milliGauss

            if (m.GetCompass(&comp_x, &comp_y, &comp_z))
            {
                // Calculate the magnetometer values in milliGauss
                // Include factory calibration per data sheet and user environmental corrections
                mx = (float)comp_x*m.getMRes()*calib[0] - magbias[0];  // get actual magnetometer value, this depends on scale being set
                my = (float)comp_y*m.getMRes()*calib[1] - magbias[1];
                mz = (float)comp_z*m.getMRes()*calib[2] - magbias[2];

    //        	printf("%d. Comp data:\n x=%d | y=%d | z=%d\n", (i+1), comp_x, comp_y, comp_z);
    //        	printf("%d. Comp data:\n x=%f | y=%f | z=%f\n", (i+1), mx, my, mz);
            }

            filter.UpdateValues(accel_x, accel_y, accel_z, gyro_x*PI/180.0f, gyro_y*PI/180.0f, gyro_z*PI/180.0f, my, mx, mz);
            float roll, pitch, yaw;
            filter.ReadValues(&roll, &pitch, &yaw);

            printf("{ %f , %f , %f },\t\t  // %f %f %f \n\r", yaw, pitch, roll, mx, my, mz);
        }
        usleep( 1000 );
	}
	return true;
}

