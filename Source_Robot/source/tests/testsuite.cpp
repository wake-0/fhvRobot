/*
 * testsuite.cpp
 *
 *  Created on: 22.10.2015
 *      Author: Nicolaj Hoess
 */


#include "../../include/Robot.h"
#include <stdio.h>
#include <unistd.h>
#include <assert.h>

bool test_class_Robot(void);

using namespace std;
using namespace FhvRobot;

int main(void)
{
	unsigned int tests = 0;
	unsigned int tests_ok = 0;

	printf("Running testsuite\n");

	printf("Running Test: Class Robot\n");

	tests_ok += (test_class_Robot()) ? 1 : 0;
	tests++;


	printf("Testsuite done.\n");
	printf("---------------\n");
	printf("Tests OK: %d\n", tests_ok);
	printf("Tests Failed: %d\n", tests - tests_ok);

	return 0;
}



bool test_class_Robot() {
	Robot r;
	// Both forward
	printf("Running forward with 50\n");
	assert( r.MotorLeft(50) == true);
	assert( r.MotorRight(50) == true);
	sleep(2);

	// Both stop
	printf("Stopping the motor \n");
	assert( r.MotorStop() == true);
	sleep(2);

	// Both reverse
	printf("Running reverse with -50\n");
	assert( r.MotorLeft(-50) == true);
	assert( r.MotorRight(-50) == true);
	sleep(2);

	// Both stop
	printf("Stopping the motor \n");
	assert( r.MotorStop() == true);
	sleep(2);

	// Both forward
	printf("Running forward with 50\n");
	assert( r.MotorLeft(50) == true);
	assert( r.MotorRight(50) == true);
	sleep(2);

	// Both stop
	printf("Stopping the motor \n");
	assert( r.MotorStop() == true);
	sleep(2);

	// Both reverse
	printf("Running reverse with -100\n");
	assert( r.MotorLeft(-100) == true);
	assert( r.MotorRight(-100) == true);
	sleep(2);

	// Both stop
	printf("Stopping the motor \n");
	assert( r.MotorStop() == true);
	sleep(2);

	// Both reverse
	printf("Running forward with 100\n");
	r.MotorLeft(100);
	r.MotorRight(100);
	sleep(2);

	// Both stop
	printf("Stopping the motor \n");
	assert( r.MotorStop() == true);
	sleep(2);

	// Acceleration Test
	printf("Accelerating both motors for 1 percent per 50ms\n");
	for (int i = 0; i < 100; i++)
	{
		r.MotorLeft(i);
		r.MotorRight(i);
		usleep(50000);
	}

	// Both stop
	printf("Stopping the motor \n");
	assert( r.MotorStop() == true);
	sleep(2);

	return true;
}
