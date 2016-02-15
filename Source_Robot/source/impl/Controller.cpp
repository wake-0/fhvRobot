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
#define BLOCK_DISTANCE			(20)

namespace FhvRobot {

static std::string exec(char* cmd);

Controller::Controller(char* path, MPU9150* mpu, FusionFilter* filter, GPIO::GPIOManager* gp, Lidar* lidar) : robot(mpu, filter, gp, lidar) {
	this->path = path;
	connection = NULL;
	gpioManager = gp;
	christKindle = false;
	blockForward = false;
	lastDistance = 0;
	running = false;
}

Controller::~Controller() {
	delete(connection);
}

void Controller::Init()
{
	robot.MotorStop(true);
}

bool Controller::Start(char* serverIp, char* name) {
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

		res = connection->Connect(name, serverIp, DEFAULT_ROBOT_PORT);
		if (res == false) {
			Debugger(WARNING) << "Connection was not succesful. Trying to reconnect in 10s...\n";
			usleep(10 * 1000 * 1000);
		}
	}
	Debugger(INFO) << "Connection was succesful\n";
	running = true;
	while(running)
	{
		for (int i = 0; i < 20; i++) {
			// Read and send orientation data
			robot.GetOrientation_10(&roll, &pitch, &yaw);
			connection->SendOrientation(roll, pitch, yaw);

			// Read lidar data
			if (robot.IsLidarEnabled())
			{
				int dist = robot.ReadLidar();
				int tempDist = lastDistance;
				lastDistance = dist;
				if (lastDistance < BLOCK_DISTANCE && blockForward && christKindle == false) {
					if (robot.GetMotorLeftValue() > 0) {
						robot.MotorLeft(0, false);
					}
					if (robot.GetMotorRightValue() > 0) {
						robot.MotorRight(0, false);
					}
				}
				if (christKindle == true && lastDistance < 100) {
					robot.MotorLeft((100 - lastDistance) * (-1), false);
					robot.MotorRight((100 - lastDistance) * (-1), false);
				} else if (christKindle == true && lastDistance > 100 && tempDist < 100) {
					robot.MotorStop(false);
				}
				Debugger(INFO) << "Lidar distance: " << dist << "\n";
			}
			usleep(1000000 / 20);
		}
		connection->SendHeartBeat();

		// Check if we have received a heartbeat (or any other message) within a given timespan
		struct timeval tp;
		gettimeofday(&tp, NULL);
		long int ms = tp.tv_sec * 1000 + tp.tv_usec / 1000;
		if (connection->GetLastMessageTime() + TIMEOUT_MS < ms)
		{
			break;
		}
	}
	connection->Disconnect();
	robot.MotorStop(true);
	CameraOff();
	return running;
}

void Controller::MotorCommand(unsigned int motorNum, int motorSpeed)
{
	if (robot.IsLidarEnabled() && blockForward && lastDistance < BLOCK_DISTANCE)
	{
		return;
	}
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
	sprintf(streamHost, "%s/fhvrobot_streaming %s %d > 0&", path, hostname, port);

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

void Controller::TriggerLED()
{
	robot.TriggerLED();
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
