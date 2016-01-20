/*
 * FusionFilter.h
 *
 *  Created on: 17.01.2016
 *      Author: Nicolaj Hoess
 */

#ifndef INCLUDE_SENSORS_FUSIONFILTER_H_
#define INCLUDE_SENSORS_FUSIONFILTER_H_

#include <math.h>

#define PI  (3.14159265358979323846f)
#define Kp 2.0f * 5.0f // these are the free parameters in the Mahony filter and fusion scheme, Kp for proportional feedback, Ki for integral
#define Ki 0.0f

namespace FhvRobot {

class FusionFilter {
private:
	float q[4];
	float deltat;
	float GyroMeasError;
	float beta;
	float eInt[3];
	int lastUpdate, firstUpdate, now;
public:
	FusionFilter()
	{
		lastUpdate = 0;
		firstUpdate = 0;
		now = 0;
		q[0] = 1.0f;
		q[1] = 0.0f;
		q[2] = 0.0f;
		q[3] = 0.0f;
		deltat = 0.0f;
		GyroMeasError = PI * (60.0f / 180.0f);
		beta = sqrt(3.0f / 4.0f) * GyroMeasError;
	}
	virtual ~FusionFilter();

	void UpdateValues(float ax, float ay, float az, float gx, float gy, float gz, float mx, float my, float mz);
	void ReadValues(float* roll, float* pitch, float* yaw);
};

} /* namespace FhvRobot */

#endif /* INCLUDE_SENSORS_FUSIONFILTER_H_ */
