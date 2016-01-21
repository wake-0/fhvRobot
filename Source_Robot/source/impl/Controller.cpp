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
#include <stdio.h>
#include <string>

#define DEFAULT_ROBOT_PORT		(998)
#define TIMEOUT_MS				(5000)
#define STREAM_APP				("fhvrobot_streaming")


namespace FhvRobot {

static std::string exec(char* cmd);

Controller::Controller(MPU9150* mpu, FusionFilter* filter) : robot(mpu, filter) {
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

	short roll, pitch, yaw;

	strcpy(serverAddress, serverIp);

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
		for (int i = 0; i < 30; i++) {
			robot.GetOrientation_10(&roll, &pitch, &yaw);
			connection->SendOrientation(roll, pitch, yaw);
			usleep(1000000 / 30);
		}
		connection->SendHeartBeat();

		// Check if we have received a heartbeat (or any other message) within a given timespan
		struct timeval tp;
		gettimeofday(&tp, NULL);
		long int ms = tp.tv_sec * 1000 + tp.tv_usec / 1000;
		if (connection->GetLastMessageTime() + TIMEOUT_MS < ms)
		{
			connection->Disconnect();
			robot.MotorStop(true);
			CameraOff();
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

void Controller::CameraOn(char* host, int port)
{
	Debugger(INFO) << "Starting stream on " << host << " at " << port << "\n";
	char streamHost[255];
	int host_len = strlen(host);
	char* hostname;
	if (host_len == 1 && host[0] == '@')
	{
		hostname = serverAddress;
	}
	else
	{
		hostname = host;
	}
	sprintf(streamHost, "./fhvrobot_streaming %s %d > 0&", hostname, port);

	// Check if stream already running
	char pidCall[255];
	sprintf(pidCall, "pidof %s", STREAM_APP);
	int s = system(pidCall);
	if (s == 0)
	{
		CameraOff();
	}

	system(streamHost);
}

void Controller::CameraOff()
{
	Debugger(INFO) << "Stopping stream\n";
	// Get PID of our streaming application
	char pidCall[255];
	sprintf(pidCall, "pidof %s", STREAM_APP);
	std::string ret = exec(pidCall);
	int pid = atoi( ret.c_str() );
	Debugger(INFO) << "Streaming service was running with pid=" << pid << "\n";
	if (pid > 0)
	{
		memset(pidCall, 0, 255);
		sprintf(pidCall, "kill %d", pid);
		system(pidCall);
	}
}

void Controller::ForceDisconnect()
{
	running = false;
}

std::string exec(char* cmd) {
    FILE* pipe = popen(cmd, "r");
    if (!pipe) return "ERROR";
    char buffer[128];
    std::string result = "";
    while(!feof(pipe)) {
        if(fgets(buffer, 128, pipe) != NULL)
            result += buffer;
    }
    pclose(pipe);
    return result;
}

} /* namespace FhvRobot */
