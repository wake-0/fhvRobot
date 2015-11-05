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
#include "../../include/ConnectionLayer.h"
#include "../../include/mocks/TransportMock.h"

//#define TEST_ROBOT_MOTOR
//#define TEST_UDP_CONNECTION
#define TEST_LAYERS

bool test_class_Robot(void);
bool test_class_UdpConnection(void);
bool test_classes_layers(void);

using namespace std;
using namespace FhvRobot;
using namespace FhvRobotProtocolStack;

int main(void)
{
	unsigned int tests = 0;
	unsigned int tests_ok = 0;

	printf("Running testsuite\n");

#ifdef TEST_ROBOT_MOTOR
	printf("Running Test: Class Robot\n");

	tests_ok += (test_class_Robot()) ? 1 : 0;
	tests++;
#endif

#ifdef TEST_UDP_CONNECTION
	printf("Running Test: Class UdpConnection\n");

	tests_ok += (test_class_UdpConnection()) ? 1 : 0;
	tests++;
#endif

#ifdef TEST_LAYERS
	printf("Running Test: Protocol Layers\n");

	tests_ok += (test_classes_layers()) ? 1 : 0;
	tests++;
#endif

	printf("Testsuite done.\n");
	printf("---------------\n");
	printf("Tests OK: %d\n", tests_ok);
	printf("Tests Failed: %d\n", tests - tests_ok);

	return 0;
}

bool test_classes_layers() {
	// Instantiate the protocol layers and a mock for transport
	TransportMock mock(NULL, NULL);

	char test[] = "Test";
	mock.Send(test, 4);

	return true;
}

bool test_class_UdpConnection() {
	UdpConnection connection(NULL, NULL);

	printf("Trying to connect to invalid hostname (1337)\n");
	bool res = connection.Connect("127.0", 1337); // Call should fail
	if (res == true) {
		printf("FAILED\n");
		return false;
	}
	printf("Trying to send message after invalid connection\n");
	res = connection.Send("X", 1);
	if (res == true) {
		printf("FAILED\n");
		return false;
	}

	printf("Trying to connect to server 83.212.127.13:997\n");
	res = connection.Connect("83.212.127.13", 997); // Call should succeed
	if (res == false) {
		printf("FAILED\n");
		return false;
	}

	printf("Trying to send message with valid connection\n");
	res = connection.Send("X", 1);
	if (res == false) {
		printf("FAILED\n");
		return false;
	}

	return true;
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
