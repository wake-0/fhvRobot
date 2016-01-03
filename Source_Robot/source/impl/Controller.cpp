/*
 * Controller.cpp
 *
 *  Created on: 13.11.2015
 *      Author: Nicolaj Hoess
 */

#include "../../include/Controller.h"
#include "../../include/Debugger.h"
#include <sys/time.h>
#include <unistd.h>

#define DEFAULT_ROBOT_PORT		(998)
#define TIMEOUT_MS				(5000)

namespace FhvRobot {

Controller::Controller() {
	connection = NULL;
	running = false;
}

Controller::~Controller() {
	delete(connection);
}

void Controller::Init()
{
	robot.MotorStop(true);
}

bool Controller::Start(char* serverIp) {
	connection = new ConnectionAPI(this);
	UdpConnection udp;
	SessionLayer sess(&udp);
	PresentationLayer pres(&sess);
	ApplicationLayer app(&pres);
	udp.SetCallback(&sess);
	sess.SetCallback(&pres);
	pres.SetCallback(&app);
	app.SetCallback(connection);

	connection->SetConnection(&app);
	bool res = false;

	while (res == false) {
		Debugger(INFO) << "Trying to connecto to the server " << serverIp << " at " << DEFAULT_ROBOT_PORT << "\n";

		res = connection->Connect("Controlled Nico", serverIp, DEFAULT_ROBOT_PORT);
		if (res == false) {
			Debugger(WARNING) << "Connection was not succesful. Trying to reconnect in 10s...\n";
			usleep(10 * 1000 * 1000);
		}
	}
	Debugger(INFO) << "Connection was succesful\n";
	running = true;
	while(running)
	{
		connection->SendHeartBeat();

		usleep(1000000);

		// Check if we have received a heartbeat (or any other message) within a given timespan
		struct timeval tp;
		gettimeofday(&tp, NULL);
		long int ms = tp.tv_sec * 1000 + tp.tv_usec / 1000;
		if (connection->GetLastMessageTime() + TIMEOUT_MS < ms)
		{
			connection->Disconnect();
			robot.MotorStop(true);
			break;
		}
	}
	return running;
}

void Controller::MotorCommand(unsigned int motorNum, int motorSpeed)
{
	Debugger(VERBOSE) << "Controller got motor command " << motorNum << " with payload=" << motorSpeed << "\n";
	if (motorNum == MOTOR_BOTH)
	{
		robot.MotorLeft(motorSpeed, false);
		robot.MotorRight(motorSpeed, false);
	}
	else if (motorNum == MOTOR_RIGHT)
	{
		robot.MotorRight(motorSpeed, false);
	}
	else if (motorNum == MOTOR_LEFT)
	{
		robot.MotorLeft(motorSpeed, false);
	}
}

void Controller::CameraEnable(bool cameraEnable)
{

}

void Controller::ForceDisconnect()
{
	running = false;
}

} /* namespace FhvRobot */
