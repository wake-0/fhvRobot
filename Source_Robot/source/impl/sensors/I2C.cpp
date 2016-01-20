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

unsigned char I2C::ReadByte(char byte)
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

unsigned char I2C::ReadByte(char devAddress, char byte)
{
	SetSlave(devAddress);
	return ReadByte(byte);
}

void I2C::ReadByte(char devAddress, char byte, char* buf)
{
	char r = ReadByte(devAddress, byte);
	*buf = r;
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

bool I2C::WriteByte(char devAddress, char byte, char value)
{
	SetSlave(devAddress);
	return WriteByte(byte, value);
}

char I2C::ReadBits(char devAddr, char regAddr, char bitStart, char length, char *data)
{
	// 01101001 read byte
	// 76543210 bit numbers
	//    xxx   args: bitStart=4, length=3
	//    010   masked
	//   -> 010 shifted
	char b;
	SetSlave(devAddr);
	b = ReadByte(regAddr);
	char mask = ((1 << length) - 1) << (bitStart - length + 1);
	b &= mask;
	b >>= (bitStart - length + 1);
	*data = b;
	return 1;
}

bool I2C::WriteBits(char devAddr, char regAddr, char bitStart, char length, char data)
{
	//      010 value to write
	// 76543210 bit numbers
	//    xxx   args: bitStart=4, length=3
	// 00011100 mask byte
	// 10101111 original value (sample)
	// 10100011 original & ~mask
	// 10101011 masked | value
	char b;
	SetSlave(devAddr);
	b = ReadByte(regAddr);

	char mask = ((1 << length) - 1) << (bitStart - length + 1);
	data <<= (bitStart - length + 1); // shift data into correct position
	data &= mask; // zero all non-important bits in data
	b &= ~(mask); // zero all important bits in existing byte
	b |= data; // combine data with existing byte
	return WriteByte(regAddr, b);
}

char I2C::ReadBit(char devAddr, char regAddr, char bitNum, char *data)
{
	char b;
	SetSlave(devAddr);
	b = ReadByte(regAddr);
	*data = b & (1 << bitNum);
	return 1;
}

bool I2C::WriteBit(char devAddr, char regAddr, char bitNum, char data) {
    char b;
    b = ReadByte(devAddr, regAddr);
    b = (data != 0) ? (b | (1 << bitNum)) : (b & ~(1 << bitNum));
    return WriteByte(devAddr, regAddr, b);
}

bool I2C::WriteWord(char devAddr, char regAddr, short data)
{
	bool result = true;
	SetSlave(devAddr);
	result &= WriteByte(regAddr, 0xFF & (data >> 8));
	result &= WriteByte(regAddr + 1, 0xFF & data);
	return result;
}

} /* namespace FhvRobot */
