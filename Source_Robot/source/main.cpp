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

using namespace FhvRobot;

#define DEFAULT_SERVER_IP		("83.212.127.13")

int main(int argc, char** argv)
{

	char serverIp[255] = { 0 };
	if (argc == 2)
	{
		memcpy(serverIp, argv[1], strlen(argv[1]));
	}
	else
	{
		memcpy(serverIp, DEFAULT_SERVER_IP, strlen(DEFAULT_SERVER_IP));
	}
	Controller controller;
	controller.Init();
	controller.Start(serverIp); // Non-returning

	return 0;
}
