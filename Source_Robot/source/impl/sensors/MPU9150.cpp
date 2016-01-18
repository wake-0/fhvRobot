/*
 * MPU9150.cpp
 *
 *  Created on: 15.01.2016
 *      Author: Nicolaj Hoess
 *
 *  This is ported file from Donny3000@github
 */

#include "../../../include/sensors/MPU9150.h"
#include <unistd.h>

namespace FhvRobot {

MPU9150::MPU9150(I2C* __i2c)
{
	mDeviceAddr = MPU9150_I2C_ADDRESS;
	mCompassAddr = 0x0C;
	i2c = __i2c;
	temp = 0;

	i2c->WriteByte(mDeviceAddr, MPU9150_PWR_MGMT_1, 0x80);
	usleep(1000 * 1000);
	i2c->WriteByte(mDeviceAddr, MPU9150_PWR_MGMT_1, 0x00);
	i2c->WriteByte(mDeviceAddr, MPU9150_PWR_MGMT_2, 0x00);

	i2c->WriteByte(mDeviceAddr, MPU9150_ACCEL_CONFIG, 0x00);
	i2c->WriteByte(mDeviceAddr, MPU9150_GYRO_CONFIG, 0x00);

	usleep(1000 * 1000);

	i2c->WriteByte(mDeviceAddr, MPU9150_INT_PIN_CFG, 0x22); // enable bypass mode
	i2c->WriteByte(mDeviceAddr, MPU9150_INT_ENABLE, 0x1);

	// Init compass
	i2c->SetSlave(mCompassAddr);        //change Address to Compass

	i2c->WriteByte(0x0A, 0x00); //PowerDownMode
	i2c->WriteByte(0x0A, 0x0F); //SelfTest
	i2c->WriteByte(0x0A, 0x00); //PowerDownMode

	i2c->SetSlave(mDeviceAddr); //change Address to MPU
}

MPU9150::~MPU9150()
{
}

bool MPU9150::FIFOOverflow()
{
    char data = i2c->ReadByte(mDeviceAddr, MPU9150_INT_STATUS);
    if(data < 0)
        return false;
    else
        return (data & 0x10);
}

bool MPU9150::I2CMasterInterrupt()
{
    char data = i2c->ReadByte(mDeviceAddr, MPU9150_INT_STATUS);
    if(data < 0)
        return false;
    else
        return (data & 0x08);
}

bool MPU9150::DataReady()
{
    char data = i2c->ReadByte(mDeviceAddr, MPU9150_INT_STATUS);
    if(data < 0)
        return false;
    else
        return (data & 0x01);
}

void MPU9150::Sleep(bool enable)
{
    if( enable )
        i2c->WriteByte(mDeviceAddr, MPU9150_PWR_MGMT_1, 0x40);
    else
        // Wake up the device and set the clock source to the X-axis gyroscope
        i2c->WriteByte(mDeviceAddr, MPU9150_PWR_MGMT_1, 0x01);
}

void MPU9150::SetI2CBypassEnabled(bool enabled)
{
	i2c->WriteBit(mDeviceAddr, MPU9150_INT_PIN_CFG, 1, enabled);
	if (enabled)
		i2c->WriteByte(mDeviceAddr, MPU9150_INT_PIN_CFG, 0x02);
}

void MPU9150::EnableCompassModule()
{
    /* Enable compass/magnetometer */
    i2c->WriteByte(mCompassAddr, 0x0A, 0x01); //enable the magnetometer
}

char MPU9150::WhoAmI()
{
    return i2c->ReadByte(mDeviceAddr, MPU9150_WHO_AM_I);
}

short MPU9150::GetAccelerometerX()
{
    return (i2c->ReadByte(mDeviceAddr, MPU9150_ACCEL_XOUT_H) << 8) |
            i2c->ReadByte(mDeviceAddr, MPU9150_ACCEL_XOUT_L);
}

short MPU9150::GetAccelerometerY()
{
    return (i2c->ReadByte(mDeviceAddr, MPU9150_ACCEL_YOUT_H) << 8) |
            i2c->ReadByte(mDeviceAddr, MPU9150_ACCEL_YOUT_L);
}

short MPU9150::GetAccelerometerZ()
{
    return (i2c->ReadByte(mDeviceAddr, MPU9150_ACCEL_ZOUT_H) << 8) |
            i2c->ReadByte(mDeviceAddr, MPU9150_ACCEL_ZOUT_L);
}

short MPU9150::GetGyroscopeX()
{
    return (i2c->ReadByte(mDeviceAddr, MPU9150_GYRO_XOUT_H) << 8) |
            i2c->ReadByte(mDeviceAddr, MPU9150_GYRO_XOUT_L);
}

short MPU9150::GetGyroscopeY()
{
    return (i2c->ReadByte(mDeviceAddr, MPU9150_GYRO_YOUT_H) << 8) |
            i2c->ReadByte(mDeviceAddr, MPU9150_GYRO_YOUT_L);
}

short MPU9150::GetGyroscopeZ()
{
    return (i2c->ReadByte(mDeviceAddr, MPU9150_GYRO_ZOUT_H) << 8) |
            i2c->ReadByte(mDeviceAddr, MPU9150_GYRO_ZOUT_L);
}

void MPU9150::GetCompassCalibration(float* calibData)
{
	char rawData[3];  // x/y/z gyro register data stored here
	i2c->WriteByte(mCompassAddr, 0x0A, 0x00); // Power down
	usleep(1000 * 10);
	i2c->WriteByte(mCompassAddr, 0x0A, 0x0F); // Enter Fuse ROM access mode
	usleep(1000 * 10);
	rawData[0] = i2c->ReadByte(mCompassAddr, 0x10);
	rawData[1] = i2c->ReadByte(mCompassAddr, 0x11);
	rawData[2] = i2c->ReadByte(mCompassAddr, 0x12);

	calibData[0] =  (float)(rawData[0] - 128)/256.0f + 1.0f; // Return x-axis sensitivity adjustment values
	calibData[1] =  (float)(rawData[1] - 128)/256.0f + 1.0f;
	calibData[2] =  (float)(rawData[2] - 128)/256.0f + 1.0f;
}

bool MPU9150::GetCompass(short* mx, short* my, short* mz)
{
	char buffer[6] = { 0 };
	i2c->WriteByte(mCompassAddr, 0x0A, 0x01); // toggle enable data read from magnetometer, no continuous read mode!
	usleep(1000 * 10);
	// Only accept a new magnetometer data read if the data ready bit is set and
	// if there are no sensor overflow or data read errors
	if(i2c->ReadByte(mCompassAddr, 0x02) & 0x01) { // wait for magnetometer data ready bit to be set
	    for (int i = 0; i < 6; i++) {
			buffer[i] = i2c->ReadByte(mCompassAddr, 0x03 + i);
	    }
	    *mx = (((short)buffer[1]) << 8) | buffer[0];
	    *my = (((short)buffer[3]) << 8) | buffer[2];
	    *mz = (((short)buffer[5]) << 8) | buffer[4];
		return true;
	}
	return false;
}

short MPU9150::GetTemp()
{
	unsigned char bH = i2c->ReadByte(mDeviceAddr, MPU9150_TEMP_OUT_H);
	unsigned char bL = i2c->ReadByte(mDeviceAddr, MPU9150_TEMP_OUT_L);
	Debugger(VERBOSE) << "Reading raw temp values H=" << (int)(bH) << " L=" << (int)(bL) << "\n";
    return (bH << 8) | bL;
}

} /* namespace FhvRobot */
