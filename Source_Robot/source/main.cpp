/*
 * hellobone.c
 *
 * Version: 1.0
 * Date:	18.10.2013
 *
 * Copyright (c) 2013, jkuhlm - All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * In Eclipse add Include path
 *     C:\gcc-linaro\arm-linux-gnueabihf\libc\usr\include
 *
 */

#include "../include/Controller.h"
#include "../include/Debugger.h"
#include "../include/sensors/FusionFilter.h"
#include "../include/sensors/MPU9150.h"
#include "../include/sensors/I2C.h"
#include "../include/configuration/INIReader.h"
#include <unistd.h>
#include <string.h>

using namespace FhvRobot;

#define DEFAULT_CONFIG_FILE		("config.ini")
#define DEFAULT_SERVER_IP		("83.212.127.13")

int main(int argc, char** argv)
{

	char configFile[255] = { 0 };
	if (argc == 2)
	{
		memcpy(configFile, argv[1], strlen(argv[1]));
	}
	else
	{
		memcpy(configFile, DEFAULT_CONFIG_FILE, strlen(DEFAULT_SERVER_IP));
	}

	std::string serverAddress = DEFAULT_SERVER_IP;

	Debugger(VERBOSE) << "Reading config file " << configFile << "\n";
	INIReader reader(configFile);
	if (reader.ParseError() < 0) {
		Debugger(ERROR) << "Could not read ini file: " << configFile << "\n";
		Debugger(INFO) << "Sample config.ini\n";
		Debugger(INFO) << "[server]\n";
		Debugger(INFO) << "address=127.0.0.1 ; Use YOUR server's ip address instead\n\n";
		Debugger(INFO) << "Using default server ip: " << DEFAULT_SERVER_IP << "\n";
	}
	else
	{
		serverAddress = reader.Get("server", "address", DEFAULT_SERVER_IP);
	}

	FusionFilter filter;
	I2C i2c(I2C_2);
	MPU9150 mpu(&i2c);
	Controller controller(&mpu, &filter);
	controller.Init();
	bool running = true;
	while (running)
	{
		Debugger(INFO) << "Trying to connect...\n";
		running = controller.Start((char*)serverAddress.c_str()); // Returns after a disconnect only
		if (running)
		{
			Debugger(WARNING) << "Disconnect because of timeout\n";
			Debugger(INFO) << "Trying to reconnect in 10s\n";
			usleep(10 * 1000 * 1000);
		}
	}
	Debugger(INFO) << "Stopping robot\n";
	return 0;
}
