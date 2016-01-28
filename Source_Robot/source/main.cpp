#include "../include/Controller.h"
#include "../include/Debugger.h"
#include "../include/sensors/FusionFilter.h"
#include "../include/sensors/MPU9150.h"
#include "../include/sensors/I2C.h"
#include "../include/configuration/INIReader.h"
#include "../include/GPIO/GPIOManager.h"
#include <signal.h>
#include <sys/types.h>
#include <unistd.h>
#include <sys/stat.h>
#include <string.h>


static void registerSignalHandler(void);
static void signalHandler(int sig);


using namespace FhvRobot;

static Controller* controller;

#define DEFAULT_CONFIG_FILE		("config.ini")
#define DEFAULT_SERVER_IP		("83.212.127.13")
#define DEFAULT_ROBOT_NAME		("FHVrobot")

int main(int argc, char** argv)
{
	registerSignalHandler();

	// Read current path of executable
	char path[255] = { 0 };
	char dest[255] = { 0 };
	pid_t pid = getpid();
	sprintf(path, "/proc/%d/exe", pid);
	if (readlink((const char*)path, dest, 255) == -1)
		Debugger(ERROR) << "Could not read path\n";
	else {
		Debugger(INFO) << "Current path is " << dest << "\n";
	}
	char* curr = &dest[strlen(dest) - 1];
	while (*curr != '/' && curr > dest) {
		*curr = '\0';
		curr--;
	}
	Debugger(INFO) << "Directory is " << dest << "\n";

	char configFile[255] = { 0 };
	if (argc == 2)
	{
		memcpy(configFile, argv[1], strlen(argv[1]));
	}
	else
	{
		sprintf(configFile, "%s/%s", dest, DEFAULT_CONFIG_FILE);
	}

	std::string serverAddress = DEFAULT_SERVER_IP;
	std::string robotName = DEFAULT_ROBOT_NAME;

	Debugger(VERBOSE) << "Reading config file " << configFile << "\n";
	INIReader reader(configFile);
	if (reader.ParseError() < 0) {
		Debugger(ERROR) << "Could not read ini file: " << configFile << "\n";
		Debugger(INFO) << "Sample config.ini\n";
		Debugger(INFO) << "[server]\n";
		Debugger(INFO) << "address=127.0.0.1 ; Use YOUR server's ip address instead\n\n";
		Debugger(INFO) << "[robot]\n";
		Debugger(INFO) << "name=FHVrobot ; Robot's name\n";
		Debugger(INFO) << "Using default server ip: " << DEFAULT_SERVER_IP << "\n";
	}
	else
	{
		serverAddress = reader.Get("server", "address", DEFAULT_SERVER_IP);
		robotName = reader.Get("robot", "name", DEFAULT_ROBOT_NAME);
	}

	FusionFilter filter;
	I2C i2c(I2C_2);
	MPU9150 mpu(&i2c);
	GPIO::GPIOManager* gp = GPIO::GPIOManager::getInstance();
	controller = new Controller(dest, &mpu, &filter, gp);
	controller->Init();
	bool running = true;
	while (running)
	{
		Debugger(INFO) << "Trying to connect...\n";
		running = controller->Start((char*)serverAddress.c_str(), (char*) robotName.c_str()); // Returns after a disconnect only
		if (running)
		{
			Debugger(WARNING) << "Disconnect because of timeout\n";
			Debugger(INFO) << "Trying to reconnect in 10s\n";
			usleep(10 * 1000 * 1000);
		}
	}
	gp->~GPIOManager();
	free(controller);
	Debugger(INFO) << "Stopping robot\n";
	return 0;
}

void registerSignalHandler()
{
	signal(SIGINT, signalHandler);
}

void signalHandler(int sig)
{
	controller->ForceDisconnect();
	signal(SIGINT, SIG_DFL);
}
