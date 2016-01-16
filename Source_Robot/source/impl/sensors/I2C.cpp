/*
 * I2C.cpp
 *
 *  Created on: 15.01.2016
 *      Author: Nicolaj Hoess
 */

#include "../../../include/sensors/I2C.h"
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <linux/i2c-dev.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

namespace FhvRobot {

I2C::I2C(tI2CBus bus) {
	Debugger(VERBOSE) << "I2C Constructor called\n";
	char filename[255];
	Debugger(INFO) << "Opening I2C-" << bus << "\n";
	sprintf(filename, "/dev/i2c-%d", bus);
	file = open(&filename[0], O_RDWR);
	Debugger(INFO) << "Opened I2C-" << bus << "! File handle " << file << "\n";
	if (file == 0) {
		Debugger(ERROR) << "Could not open I2C-" << bus << "\n";
	}
}

I2C::~I2C() {
	Debugger(INFO) << "Deconstructor called of I2C\n";
	close(file);
}

bool I2C::SetSlave(char slave_address)
{
	Debugger(VERBOSE) << "Setting slave address of I2C " << (int)slave_address << "\n";
	int addr = slave_address;
	if (ioctl(file, I2C_SLAVE, addr) < 0) {
	    Debugger(ERROR) << "Setting slave failed\n";
	    return false;
	}
	return true;
}

char I2C::ReadByte(char byte)
{
    unsigned char buf[2];
    buf[0] = byte;
    if (write(file, buf, 1) != 1) {
    	Debugger(ERROR) << "Writing I2C for reading failed\n";
    }

    if (read(file, buf, 1) != 1) {
    	Debugger(ERROR) << "Reading I2C failed\n";
    }
    return buf[0];
}

bool I2C::WriteByte(char byte, char value)
{
    unsigned char buf[2];
    buf[0] = byte;
    buf[1] = value;

    if (write(file, buf, 2) != 2) {
        Debugger(ERROR) << "Writing I2C failed\n";
        return false;
    }
    return true;
}

} /* namespace FhvRobot */
