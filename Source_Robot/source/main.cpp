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

int main(int argc, char** argv)
{

	Controller controller;
	controller.Init();
	controller.Start(); // Non-returning

	return 0;
}
